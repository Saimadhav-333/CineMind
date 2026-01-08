package com.netflix.content_service.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "movies")
@Data
@NoArgsConstructor
public class Movie {

    @Id
    private String id;   // Mongo ObjectId (String)

    private Long tmdbId;
    private String title;
    private String language;
    private String posterPath;
    private List<String> genres;
}
