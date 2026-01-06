package com.netflix.recommendation_service.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class AiRecommendationResponse {
    private List<Long> recommendedMovieIds;
}

