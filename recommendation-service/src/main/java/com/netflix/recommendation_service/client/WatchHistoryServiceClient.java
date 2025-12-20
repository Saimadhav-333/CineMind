package com.netflix.recommendation_service.client;



import com.netflix.recommendation_service.dto.WatchHistoryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class WatchHistoryServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.watch-history.base-url}")
    private String watchHistoryServiceBaseUrl;

    public WatchHistoryServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<WatchHistoryDto> getUserHistory(String userId) {

        String url = watchHistoryServiceBaseUrl + "/watch/" + userId;

        WatchHistoryDto[] response =
                restTemplate.getForObject(url, WatchHistoryDto[].class);

        return Arrays.asList(response);
    }
}

