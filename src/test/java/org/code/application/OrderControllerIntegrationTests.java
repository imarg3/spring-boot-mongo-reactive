package org.code.application;

import org.code.domain.Order;
import org.code.domain.OrderService;
import org.code.domain.OrderStatus;
import org.code.domain.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    private Order order;

    private Todo todo;

    @BeforeEach
    void setUp() {
        order = Order.builder().id(null)
                .productId("prod-1")
                .quantity(2)
                .status(OrderStatus.PENDING)
                .build();
        todo = Todo.builder()
                .userId("101")
                .title("Todo-1")
                .completed(true)
                .build();

        when(orderService.createOrder(order)).thenReturn(Mono.just(order));
        when(orderService.getOrderById("1")).thenReturn(Mono.just(order));
        when(orderService.getTodoById("1")).thenReturn(Mono.just(todo));
        when(orderService.getAllTodos()).thenReturn(Flux.just(todo));
    }

    @Test
    void createOrder() {

        webTestClient.post()
                .uri("/api/orders")
                .body(Mono.just(order), Order.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(order);
    }

    @Test
    void getOrderById() {
        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(order);
    }

    @Test
    void getTodoById() {
        webTestClient.get()
                .uri("/api/orders/todo/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .isEqualTo(todo);
    }

    @Test
    void getAllTodos() {
        webTestClient.get()
                .uri("/api/orders/all-todos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Todo.class)
                .hasSize(1)
                .contains(todo);
    }
}
