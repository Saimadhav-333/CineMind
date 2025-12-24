package com.netflix.recommendation_service.service;

import com.netflix.recommendation_service.client.ContentServiceClient;
import com.netflix.recommendation_service.client.WatchHistoryServiceClient;
import com.netflix.recommendation_service.dto.MovieDto;
import com.netflix.recommendation_service.dto.RecommendationDto;
import com.netflix.recommendation_service.dto.WatchHistoryDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final int DEFAULT_LIMIT = 10;

    private final WatchHistoryServiceClient watchHistoryClient;
    private final ContentServiceClient contentServiceClient;

    public RecommendationService(
            WatchHistoryServiceClient watchHistoryClient,
            ContentServiceClient contentServiceClient
    ) {
        this.watchHistoryClient = watchHistoryClient;
        this.contentServiceClient = contentServiceClient;
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

        // 2️⃣ Cold-start
        if (watchHistory == null || watchHistory.isEmpty()) {
            return coldStartRecommendations(offset, safeLimit);
        }

        // 3️⃣ Collect watched TMDB IDs
        Set<Long> watchedTmdbIds = watchHistory.stream()
                .map(WatchHistoryDto::getTmdbMovieId)
                .collect(Collectors.toSet());

        // 4️⃣ Fetch all movies
        List<MovieDto> allMovies =
                contentServiceClient.getAllMovies();

        // 5️⃣ Build genre frequency map
        Map<String, Integer> genreCount = new HashMap<>();

        allMovies.stream()
                .filter(movie -> watchedTmdbIds.contains(movie.getTmdbId()))
                .forEach(movie -> {
                    for (String genre : movie.getGenres()) {
                        genreCount.put(
                                genre,
                                genreCount.getOrDefault(genre, 0) + 1
                        );
                    }
                });

        if (genreCount.isEmpty()) {
            return coldStartRecommendations(offset, safeLimit);
        }

        // 6️⃣ Pick top 2 genres
        List<String> topGenres = genreCount.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        // 7️⃣ Filter → paginate → map
        return allMovies.stream()
                .filter(movie -> !watchedTmdbIds.contains(movie.getTmdbId()))
                .filter(movie ->
                        movie.getGenres().stream()
                                .anyMatch(topGenres::contains)
                )
                .skip(offset)
                .limit(safeLimit)
                .map(this::toRecommendationDto)
                .collect(Collectors.toList());
    }

    // 🔹 Cold-start with pagination
    private List<RecommendationDto> coldStartRecommendations(
            int offset,
            int limit
    ) {
        List<MovieDto> allMovies = contentServiceClient.getAllMovies();

        return allMovies.stream()
                .skip(offset)
                .limit(limit)
                .map(this::toRecommendationDto)
                .collect(Collectors.toList());
    }

    private RecommendationDto toRecommendationDto(MovieDto movie) {

        RecommendationDto dto = new RecommendationDto();
        dto.setMovieId(movie.getTmdbId());
        dto.setTitle(movie.getTitle());
        dto.setGenres(movie.getGenres());
        dto.setPosterPath(movie.getPosterPath());

        return dto;
    }
}
