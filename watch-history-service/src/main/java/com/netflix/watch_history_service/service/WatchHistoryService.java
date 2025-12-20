package com.netflix.watch_history_service.service;

import com.netflix.watch_history_service.model.WatchHistory;
import com.netflix.watch_history_service.repository.WatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository repository;

    public WatchHistoryService(WatchHistoryRepository repository) {
        this.repository = repository;
    }

    public WatchHistory recordWatch(
            String userId,
            Long tmdbMovieId,
            int watchTime
    ) {
        WatchHistory history = new WatchHistory();
        history.setUserId(userId);
        history.setTmdbMovieId(tmdbMovieId);
        history.setWatchTime(watchTime);
        history.setWatchedAt(LocalDateTime.now());

        return repository.save(history);
    }

    public List<WatchHistory> getUserHistory(String userId) {
        return repository.findByUserId(userId);
    }
}