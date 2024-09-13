package org.code.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Builder
public class Order {
    private String id;
    private String productId;
    private int quantity;
    private OrderStatus status;
}

