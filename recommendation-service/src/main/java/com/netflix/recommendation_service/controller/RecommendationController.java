package com.netflix.recommendation_service.controller;


import com.netflix.recommendation_service.dto.RecommendationDto;
import com.netflix.recommendation_service.service.RecommendationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public List<RecommendationDto> getRecommendations(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return recommendationService.recommendMovies(userId, page, limit);
    }
}
