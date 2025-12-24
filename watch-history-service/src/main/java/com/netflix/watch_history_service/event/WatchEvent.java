package com.netflix.watch_history_service.event;



import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WatchEvent {

    private String eventType;      // USER_WATCHED_MOVIE
    private String userId;
    private Long tmdbMovieId;
    private int watchTime;
    private LocalDateTime timestamp;
}

