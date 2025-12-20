package com.netflix.content_service.dto;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class TmdbMovie {

    private Long id;
    private String title;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;
}



