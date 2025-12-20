package com.netflix.recommendation_service.dto;



import lombok.Data;

import java.util.List;

@Data
public class MovieDto {

    private Long id;
    private String title;
    private Long tmdbId;
    private List<String> genres;
    private String language;
    private String posterPath;
}

