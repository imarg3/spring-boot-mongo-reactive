package org.code.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionEntry {
    private String accountNumber;
    private double amount;
}
