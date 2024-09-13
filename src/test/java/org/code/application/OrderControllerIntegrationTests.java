package org.code.application;

import org.code.domain.Order;
import org.code.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTests {

    @Autowired
    private WebTestClient webClient;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateOrder() {
        var order = Order.builder().id(null)
                .productId("prod-1")
                .quantity(2)
                .status(OrderStatus.PENDING)
                .build();

        webTestClient.post()
                .uri("/api/orders")
                .bodyValue(order)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .value(createdOrder -> assertNotNull(createdOrder.getId()));
    }
}
