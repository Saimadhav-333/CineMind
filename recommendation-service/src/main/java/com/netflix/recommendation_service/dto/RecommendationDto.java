package com.netflix.recommendation_service.dto;


import lombok.Data;

import java.util.List;

@Data
public class RecommendationDto {

    private Long movieId;
    private String title;
    private List<String> genres;
    private String posterPath;
}

