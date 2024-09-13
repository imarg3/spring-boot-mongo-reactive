package org.code.domain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private final WebClient webClient;

    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;
    }

    public Mono<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public Mono<Order> createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Mono<Todo> getTodoById(String productId) {
        return webClient.get()
                .uri("/todos/{productId}", productId)
                .retrieve()
                /*.onStatus(HttpStatus::is4xxClientError, clientResponse ->
                    Mono.error(new RuntimeException("Client error!"))
                )
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                    Mono.error(new RuntimeException("Server error!"))
                )*/
                .bodyToMono(Todo.class);
    }

    public Flux<Todo> getAllTodos() {
        return webClient.get()
                .uri("/todos")
                .retrieve()
                .bodyToFlux(Todo.class); // Returns a reactive stream of todos
    }
}
