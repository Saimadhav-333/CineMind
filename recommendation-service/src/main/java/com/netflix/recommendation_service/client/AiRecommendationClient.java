package com.netflix.recommendation_service.client;


import com.netflix.recommendation_service.dto.ai.AiRecommendationRequest;
import com.netflix.recommendation_service.dto.ai.AiRecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AiRecommendationClient {

    private final RestTemplate restTemplate;

    @Value("${services.ai.base-url}")
    private String aiBaseUrl;

    public AiRecommendationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AiRecommendationResponse getRecommendations(
            AiRecommendationRequest request
    ) {
        String url = aiBaseUrl + "/recommend";
        return restTemplate.postForObject(
                url,
                request,
                AiRecommendationResponse.class
        );
    }
}

