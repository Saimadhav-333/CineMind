package com.netflix.content_service.service;

import com.netflix.content_service.dto.TmdbGenreResponse;
import com.netflix.content_service.dto.TmdbMovie;
import com.netflix.content_service.dto.TmdbMovieResponse;
import com.netflix.content_service.model.Movie;
import com.netflix.content_service.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TmdbSyncService {

    private final RestTemplate restTemplate;
    private final MovieRepository movieRepository;

    @Value("${tmdb.api-key}")
    private String apiKey;

    @Value("${tmdb.base-url}")
    private String baseUrl;

    public TmdbSyncService(RestTemplate restTemplate, MovieRepository movieRepository) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
    }

    public void syncPopularMovies() {

        String movieUrl = baseUrl + "/movie/popular?api_key=" + apiKey;
        String genreUrl = baseUrl + "/genre/movie/list?api_key=" + apiKey;


        TmdbGenreResponse genreResponse =
                restTemplate.getForObject(genreUrl, TmdbGenreResponse.class);

        Map<Integer, String> genreMap = new HashMap<>();

        if (genreResponse != null && genreResponse.getGenres() != null) {
            genreResponse.getGenres()
                    .forEach(g -> genreMap.put(g.getId(), g.getName()));
        }

        // 2️⃣ Fetch popular movies
        TmdbMovieResponse movieResponse =
                restTemplate.getForObject(movieUrl, TmdbMovieResponse.class);

        if (movieResponse == null || movieResponse.getResults() == null) return;

        movieResponse.getResults().forEach(tmdbMovie -> {

            List<String> genres = tmdbMovie.getGenreIds()
                    .stream()
                    .map(id -> genreMap.getOrDefault(id, "UNKNOWN"))
                    .toList();

            Movie movie = new Movie();
            movie.setTmdbId(tmdbMovie.getId());
            movie.setTitle(tmdbMovie.getTitle());
            movie.setLanguage(tmdbMovie.getOriginalLanguage());
            movie.setPosterPath(tmdbMovie.getPosterPath());
            movie.setGenres(genres);

            movieRepository.save(movie);
        });
    }

    private Map<Integer, String> fetchGenreMap() {

        String url = baseUrl + "/genre/movie/list?api_key=" + apiKey;

        TmdbGenreResponse response =
                restTemplate.getForObject(url, TmdbGenreResponse.class);

        Map<Integer, String> genreMap = new HashMap<>();

        if (response != null && response.getGenres() != null) {
            response.getGenres()
                    .forEach(g -> genreMap.put(g.getId(), g.getName()));
        }

        return genreMap;
    }

}
