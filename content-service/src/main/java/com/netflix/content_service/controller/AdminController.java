package com.netflix.content_service.controller;



import com.netflix.content_service.service.TmdbSyncService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final TmdbSyncService tmdbSyncService;

    public AdminController(TmdbSyncService tmdbSyncService) {
        this.tmdbSyncService = tmdbSyncService;
    }

    @PostMapping("/sync-movies")
    public String syncMovies() {
        tmdbSyncService.syncPopularMovies();
        return "Movies synced from TMDB";
    }
}

