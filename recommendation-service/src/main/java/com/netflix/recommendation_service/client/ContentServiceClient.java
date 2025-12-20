package com.netflix.recommendation_service.client;



import com.netflix.recommendation_service.dto.MovieDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ContentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.content.base-url}")
    private String contentServiceBaseUrl;

    public ContentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<MovieDto> getAllMovies() {

        String url = contentServiceBaseUrl + "/movies";

        MovieDto[] response =
                restTemplate.getForObject(url, MovieDto[].class);

        return Arrays.asList(response);
    }
}

