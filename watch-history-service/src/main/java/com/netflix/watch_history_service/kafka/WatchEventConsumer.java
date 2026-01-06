package com.netflix.watch_history_service.kafka;



import com.netflix.watch_history_service.event.WatchEvent;
import com.netflix.watch_history_service.model.ProcessedEvent;
import com.netflix.watch_history_service.model.WatchHistory;
import com.netflix.watch_history_service.repository.ProcessedEventRepository;
import com.netflix.watch_history_service.repository.WatchHistoryRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class WatchEventConsumer {

    private final WatchHistoryRepository historyRepository;
    private final ProcessedEventRepository processedEventRepository;

    public WatchEventConsumer(
            WatchHistoryRepository historyRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.historyRepository = historyRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(
            topics = "watch-events",
            groupId = "watch-history-group"
    )
    public void consumeWatchEvent(WatchEvent event) {

        // 1️⃣ Idempotency check
        if (processedEventRepository.existsById(event.getEventId())) {
            return; // already processed
        }

        // 2️⃣ Save watch history
        WatchHistory history = new WatchHistory();
        history.setUserId(event.getUserId());
        history.setTmdbMovieId(event.getTmdbMovieId());
        history.setWatchTime(event.getWatchTime());
        history.setWatchedAt(event.getTimestamp());

        historyRepository.save(history);

        // 3️⃣ Mark event as processed
        ProcessedEvent processed = new ProcessedEvent();
        processed.setEventId(event.getEventId());

        processedEventRepository.save(processed);
    }
}

