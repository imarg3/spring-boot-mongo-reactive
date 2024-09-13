package org.code.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${services.product.baseUrl}") String productServiceBaseUrl) {
        WebClient.Builder builder = WebClient.builder();
        builder.baseUrl(productServiceBaseUrl);
        return builder.build();
    }
}
