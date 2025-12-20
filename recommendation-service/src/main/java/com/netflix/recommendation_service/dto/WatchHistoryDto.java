package com.netflix.recommendation_service.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WatchHistoryDto {

    private String userId;
    private Long tmdbMovieId;
    private int watchTime;
    private LocalDateTime watchedAt;
}

