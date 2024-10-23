package com.example.product_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class FeignClientConfig {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public FeignClientConfig(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                        .withClientRegistrationId("INVENTORY-SERVICE")
                        .principal(SecurityContextHolder.getContext().getAuthentication()) // burayı güncelle
                        .build();

                OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
                if (authorizedClient != null) {
                    OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
                    requestTemplate.header("Authorization", "Bearer " + accessToken.getTokenValue());
                } else {
                    throw new IllegalStateException("Unable to authorize client.");
                }
            }
        };
    }
}
