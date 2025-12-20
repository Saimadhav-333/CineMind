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

    private final WatchHistoryServiceClient watchHistoryClient;
    private final ContentServiceClient contentServiceClient;

    public RecommendationService(
            WatchHistoryServiceClient watchHistoryClient,
            ContentServiceClient contentServiceClient
    ) {
        this.watchHistoryClient = watchHistoryClient;
        this.contentServiceClient = contentServiceClient;
    }

    public List<RecommendationDto> recommendMovies(String userId) {

        // 1️⃣ Fetch watch history
        List<WatchHistoryDto> watchHistory =
                watchHistoryClient.getUserHistory(userId);

        if (watchHistory.isEmpty()) {
            return Collections.emptyList();
        }

        // 2️⃣ Collect watched TMDB IDs (GLOBAL IDENTIFIER)
        Set<Long> watchedTmdbIds = watchHistory.stream()
                .map(WatchHistoryDto::getTmdbMovieId)
                .collect(Collectors.toSet());

        // 3️⃣ Fetch all movies
        List<MovieDto> allMovies =
                contentServiceClient.getAllMovies();

        // 4️⃣ Build genre frequency map (ONLY watched movies)
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
            return Collections.emptyList();
        }

        // 5️⃣ Find top 2 genres
        List<String> topGenres = genreCount.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();

        // 6️⃣ Recommend unwatched movies matching top genres
        return allMovies.stream()
                .filter(movie -> !watchedTmdbIds.contains(movie.getTmdbId()))
                .filter(movie ->
                        movie.getGenres().stream()
                                .anyMatch(topGenres::contains)
                )
                .limit(10)
                .map(this::toRecommendationDto)
                .collect(Collectors.toList());
    }

    private RecommendationDto toRecommendationDto(MovieDto movie) {

        RecommendationDto dto = new RecommendationDto();
        dto.setMovieId(movie.getTmdbId()); // expose TMDB ID
        dto.setTitle(movie.getTitle());
        dto.setGenres(movie.getGenres());
        dto.setPosterPath(movie.getPosterPath());

        return dto;
    }
}
