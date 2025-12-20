package com.netflix.content_service.dto;



import java.util.List;

public class TmdbMovieResponse {
    private List<TmdbMovie> results;

    public List<TmdbMovie> getResults() {
        return results;
    }

    public void setResults(List<TmdbMovie> results) {
        this.results = results;
    }
}

