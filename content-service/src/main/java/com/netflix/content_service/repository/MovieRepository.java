package com.netflix.content_service.repository;

import com.netflix.content_service.model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {

    boolean existsByTmdbId(Long tmdbId);
}
