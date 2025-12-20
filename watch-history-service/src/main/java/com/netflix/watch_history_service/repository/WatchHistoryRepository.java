package com.netflix.watch_history_service.repository;

import com.netflix.watch_history_service.model.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WatchHistoryRepository
        extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUserId(String userId);
}
