package com.netflix.recommendation_service.dto.ai;



import lombok.Data;

import java.util.List;

@Data
public class AiRecommendationRequest {

    private String userId;
    private List<WatchHistoryItem> watchHistory;
    private List<CandidateMovie> candidateMovies;
    private int limit;

    @Data
    public static class WatchHistoryItem {
        private Long tmdbMovieId;
        private int watchTime;
    }

    @Data
    public static class CandidateMovie {
        private Long tmdbId;
        private List<String> genres;
    }
}

