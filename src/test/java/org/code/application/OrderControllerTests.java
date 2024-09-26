package org.code.application;

import org.code.config.SecurityConfig;
import org.code.domain.Order;
import org.code.domain.OrderService;
import org.code.domain.OrderStatus;
import org.code.domain.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = OrderController.class)
@Import(SecurityConfig.class)
public class OrderControllerTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    private Order order;

    private Todo todo;

    @BeforeEach
    void setUp() {
        order = Order.builder().id("1")
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
    @WithMockUser(username = "arpit", roles = {"ADMIN"})
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
    @WithMockUser(username = "arpit", roles = {"USER"})
    void getOrderById() {
        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Order.class)
                .isEqualTo(order);
    }

    @Test
    @WithMockUser(username = "user1", roles = {"TEST"}) // Simulating a user with TEST role
    void getOrderById_Unauthorized() {
        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isForbidden(); // Expecting 401 Unauthorized
    }

    @Test
    void getOrderById_Unauthenticated() {
        webTestClient.get()
                .uri("/api/orders/1")
                .exchange()
                .expectStatus().isUnauthorized(); // Expecting 401 UNAUTHORIZED
    }

    @Test
    @WithMockUser(username = "user2", roles = {"ADMIN"})
    void getTodoById() {
        webTestClient.get()
                .uri("/api/orders/todo/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Todo.class)
                .isEqualTo(todo);
    }

    @Test
    @WithMockUser(username = "users1", roles = {"ADMIN"})
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
