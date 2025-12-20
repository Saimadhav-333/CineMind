package com.netflix.content_service.dto;


import lombok.Data;
import java.util.List;

@Data
public class TmdbGenreResponse {
    private List<TmdbGenre> genres;
}

