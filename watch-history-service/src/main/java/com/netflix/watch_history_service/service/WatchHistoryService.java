package com.netflix.watch_history_service.service;

import com.netflix.watch_history_service.event.WatchEvent;
import com.netflix.watch_history_service.kafka.WatchEventProducer;
import com.netflix.watch_history_service.model.WatchHistory;
import com.netflix.watch_history_service.repository.WatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WatchHistoryService {

    private final WatchHistoryRepository repository;
    private final WatchEventProducer eventProducer;

    public WatchHistoryService(
            WatchHistoryRepository repository,
            WatchEventProducer eventProducer
    ) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    /**
     * FULL ASYNC MODE:
     * - Publish event only
     * - DB write happens in Kafka Consumer
     */
    public WatchHistory recordWatch(
            String userId,
            Long tmdbMovieId,
            int watchTime
    ) {
        WatchEvent event = new WatchEvent();
        event.setEventType("USER_WATCHED_MOVIE");
        event.setUserId(userId);
        event.setTmdbMovieId(tmdbMovieId);
        event.setWatchTime(watchTime);
        event.setTimestamp(LocalDateTime.now());

        // ✅ Publish event
        eventProducer.publishWatchEvent(event);

        // ✅ Return immediate response (NO DB write here)
        WatchHistory response = new WatchHistory();
        response.setUserId(userId);
        response.setTmdbMovieId(tmdbMovieId);
        response.setWatchTime(watchTime);
        response.setWatchedAt(event.getTimestamp());

        return response;
    }

    // This still works because DB is written by consumer
    public List<WatchHistory> getUserHistory(String userId) {
        return repository.findByUserId(userId);
    }
}



//package com.netflix.watch_history_service.service;
//
//
//import com.netflix.watch_history_service.event.WatchEvent;
//import com.netflix.watch_history_service.kafka.WatchEventProducer;
//import com.netflix.watch_history_service.model.WatchHistory;
//import com.netflix.watch_history_service.repository.WatchHistoryRepository;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class WatchHistoryService {
//
//    private final WatchHistoryRepository repository;
//    private final WatchEventProducer eventProducer;
//
//    public WatchHistoryService(
//            WatchHistoryRepository repository,
//            WatchEventProducer eventProducer
//    ) {
//        this.repository = repository;
//        this.eventProducer = eventProducer;
//    }
//
//    public WatchHistory recordWatch(
//            String userId,
//            Long tmdbMovieId,
//            int watchTime
//    ) {
//        // 1️⃣ Publish Kafka Event (ASYNC)
//        WatchEvent event = new WatchEvent();
//        event.setEventType("USER_WATCHED_MOVIE");
//        event.setUserId(userId);
//        event.setTmdbMovieId(tmdbMovieId);
//        event.setWatchTime(watchTime);
//        event.setTimestamp(LocalDateTime.now());
//
//        eventProducer.publishWatchEvent(event);
//
//        // 2️⃣ Save to DB (SYNC - DUAL MODE)
//        WatchHistory history = new WatchHistory();
//        history.setUserId(userId);
//        history.setTmdbMovieId(tmdbMovieId);
//        history.setWatchTime(watchTime);
//        history.setWatchedAt(LocalDateTime.now());
//
//        return repository.save(history);
//    }
//
//    public List<WatchHistory> getUserHistory(String userId) {
//        return repository.findByUserId(userId);
//    }
//}
