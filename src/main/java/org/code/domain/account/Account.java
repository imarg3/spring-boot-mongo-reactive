package org.code.domain.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("accounts")
@AllArgsConstructor
@Data
@Builder
public class Account {

    private String accountNumber;
    private double balance;
}
