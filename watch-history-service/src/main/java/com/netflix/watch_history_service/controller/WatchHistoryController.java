package com.netflix.watch_history_service.controller;



import com.netflix.watch_history_service.model.WatchHistory;
import com.netflix.watch_history_service.service.WatchHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/watch")
public class WatchHistoryController {

    private final WatchHistoryService service;

    public WatchHistoryController(WatchHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public WatchHistory recordWatch(
            @RequestParam String userId,
            @RequestParam Long movieId,
            @RequestParam int watchTime
    ) {
        return service.recordWatch(userId, movieId, watchTime);
    }

    @GetMapping("/{userId}")
    public List<WatchHistory> getUserHistory(
            @PathVariable String userId
    ) {
        return service.getUserHistory(userId);
    }
}

