package com.netflix.recommendation_service.service;

import com.netflix.recommendation_service.client.AiRecommendationClient;
import com.netflix.recommendation_service.client.ContentServiceClient;
import com.netflix.recommendation_service.client.WatchHistoryServiceClient;
import com.netflix.recommendation_service.dto.MovieDto;
import com.netflix.recommendation_service.dto.RecommendationDto;
import com.netflix.recommendation_service.dto.WatchHistoryDto;
import com.netflix.recommendation_service.dto.ai.AiRecommendationRequest;
import com.netflix.recommendation_service.dto.ai.AiRecommendationResponse;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final int DEFAULT_LIMIT = 10;

    // 🔥 TMDB GENRE MAP (SOURCE OF TRUTH)
    private static final Map<String, String> GENRE_MAP = Map.ofEntries(
            Map.entry("28", "Action"),
            Map.entry("12", "Adventure"),
            Map.entry("16", "Animation"),
            Map.entry("35", "Comedy"),
            Map.entry("80", "Crime"),
            Map.entry("99", "Documentary"),
            Map.entry("18", "Drama"),
            Map.entry("10751", "Family"),
            Map.entry("14", "Fantasy"),
            Map.entry("36", "History"),
            Map.entry("27", "Horror"),
            Map.entry("10402", "Music"),
            Map.entry("9648", "Mystery"),
            Map.entry("10749", "Romance"),
            Map.entry("878", "Sci-Fi"),
            Map.entry("10770", "TV Movie"),
            Map.entry("53", "Thriller"),
            Map.entry("10752", "War"),
            Map.entry("37", "Western")
    );

    private final WatchHistoryServiceClient watchHistoryClient;
    private final ContentServiceClient contentServiceClient;
    private final AiRecommendationClient aiClient;

    public RecommendationService(
            WatchHistoryServiceClient watchHistoryClient,
            ContentServiceClient contentServiceClient,
            AiRecommendationClient aiClient
    ) {
        this.watchHistoryClient = watchHistoryClient;
        this.contentServiceClient = contentServiceClient;
        this.aiClient = aiClient;
    }

    public List<RecommendationDto> recommendMovies(
            String userId,
            int page,
            int limit
    ) {
        int safeLimit = limit <= 0 ? DEFAULT_LIMIT : limit;
        int offset = Math.max(page, 0) * safeLimit;

        // 1️⃣ Fetch watch history
        List<WatchHistoryDto> watchHistory =
                watchHistoryClient.getUserHistory(userId);

        // 2️⃣ Fetch all movies
        List<MovieDto> allMovies =
                contentServiceClient.getAllMovies();

        // 3️⃣ Cold-start fallback
        if (watchHistory == null || watchHistory.isEmpty()) {
            return paginate(
                    allMovies.stream()
                            .map(this::toRecommendationDto)
                            .toList(),
                    offset,
                    safeLimit
            );
        }

        // 4️⃣ Build candidate movies (exclude watched)
        Set<Long> watchedIds = watchHistory.stream()
                .map(WatchHistoryDto::getTmdbMovieId)
                .collect(Collectors.toSet());

        List<MovieDto> candidates = allMovies.stream()
                .filter(m -> !watchedIds.contains(m.getTmdbId()))
                .toList();

        // 5️⃣ Build AI request
        AiRecommendationRequest aiRequest =
                buildAiRequest(userId, watchHistory, candidates, safeLimit);

        try {
            // 6️⃣ Call AI service
            AiRecommendationResponse aiResponse =
                    aiClient.getRecommendations(aiRequest);

            if (aiResponse == null ||
                    aiResponse.getRecommendedMovieIds() == null ||
                    aiResponse.getRecommendedMovieIds().isEmpty()) {

                return ruleBasedFallback(allMovies, watchedIds, offset, safeLimit);
            }

            // 7️⃣ Map ranked IDs → movie DTOs
            Map<Long, MovieDto> movieMap = allMovies.stream()
                    .collect(Collectors.toMap(MovieDto::getTmdbId, m -> m));

            List<RecommendationDto> ranked =
                    aiResponse.getRecommendedMovieIds().stream()
                            .map(movieMap::get)
                            .filter(Objects::nonNull)
                            .map(this::toRecommendationDto)
                            .toList();

            return paginate(ranked, offset, safeLimit);

        } catch (Exception ex) {
            // 8️⃣ AI failure fallback
            return ruleBasedFallback(allMovies, watchedIds, offset, safeLimit);
        }
    }

    /* ---------------- HELPER METHODS ---------------- */

    private AiRecommendationRequest buildAiRequest(
            String userId,
            List<WatchHistoryDto> history,
            List<MovieDto> candidates,
            int limit
    ) {
        AiRecommendationRequest req = new AiRecommendationRequest();
        req.setUserId(userId);
        req.setLimit(limit);

        req.setWatchHistory(
                history.stream().map(h -> {
                    AiRecommendationRequest.WatchHistoryItem i =
                            new AiRecommendationRequest.WatchHistoryItem();
                    i.setTmdbMovieId(h.getTmdbMovieId());
                    i.setWatchTime(h.getWatchTime());
                    return i;
                }).toList()
        );

        req.setCandidateMovies(
                candidates.stream().map(m -> {
                    AiRecommendationRequest.CandidateMovie c =
                            new AiRecommendationRequest.CandidateMovie();
                    c.setTmdbId(m.getTmdbId());
                    // 🔥 FIX: ID → NAME
                    c.setGenres(mapGenreIdsToNames(m.getGenres()));
                    return c;
                }).toList()
        );

        return req;
    }

    private List<RecommendationDto> ruleBasedFallback(
            List<MovieDto> allMovies,
            Set<Long> watchedIds,
            int offset,
            int limit
    ) {
        return paginate(
                allMovies.stream()
                        .filter(m -> !watchedIds.contains(m.getTmdbId()))
                        .map(this::toRecommendationDto)
                        .toList(),
                offset,
                limit
        );
    }

    private List<RecommendationDto> paginate(
            List<RecommendationDto> list,
            int offset,
            int limit
    ) {
        return list.stream()
                .skip(offset)
                .limit(limit)
                .toList();
    }

    private RecommendationDto toRecommendationDto(MovieDto movie) {
        RecommendationDto dto = new RecommendationDto();
        dto.setMovieId(movie.getTmdbId());
        dto.setTitle(movie.getTitle());
        // 🔥 FIX: ID → NAME
        dto.setGenres(mapGenreIdsToNames(movie.getGenres()));
        dto.setPosterPath(movie.getPosterPath());
        return dto;
    }

    private List<String> mapGenreIdsToNames(List<String> genreIds) {
        if (genreIds == null) return List.of();

        return genreIds.stream()
                .map(id -> GENRE_MAP.getOrDefault(id, "UNKNOWN"))
                .toList();
    }
}
