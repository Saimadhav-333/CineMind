package com.netflix.watch_history_service.kafka;


import com.netflix.watch_history_service.event.WatchEvent;
import com.netflix.watch_history_service.model.WatchHistory;
import com.netflix.watch_history_service.repository.WatchHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WatchEventConsumer {

    private final WatchHistoryRepository repository;

    public WatchEventConsumer(WatchHistoryRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "watch-events",
            groupId = "watch-history-group"
    )
    public void consumeWatchEvent(WatchEvent event) {

        // Convert event → entity
        WatchHistory history = new WatchHistory();
        history.setUserId(event.getUserId());
        history.setTmdbMovieId(event.getTmdbMovieId());
        history.setWatchTime(event.getWatchTime());
        history.setWatchedAt(event.getTimestamp());

        // Save to DB
        repository.save(history);
    }
}

