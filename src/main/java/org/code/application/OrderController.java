package org.code.application;

import lombok.AllArgsConstructor;
import org.code.domain.Order;
import org.code.domain.OrderService;
import org.code.domain.Todo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable("id") String id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/todo/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Todo>> getTodo(@PathVariable("id") String id) {
        return orderService.getTodoById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/all-todos")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<Todo> getAllTodos() {
        return orderService.getAllTodos();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Order> createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
}
