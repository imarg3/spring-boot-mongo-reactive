package org.code.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static reactor.core.publisher.Mono.when;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testGetOrderById() {
        String id = "123";
        var order = new Order(id, "prod-1", 2, OrderStatus.PENDING);
        when(orderRepository.findById(id)).thenReturn(Mono.just(order));

        Mono<Order> orderMono = orderService.getOrderById(id);
        StepVerifier.create(orderMono)
                .expectNext(order)
                .verifyComplete();
    }
}