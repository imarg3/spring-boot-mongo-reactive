package org.code.domain.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document("Transactions")
@AllArgsConstructor
@Data
public class Transaction {

    @Id
    private String id;

    private List<TransactionEntry> entries;

    private TransactionStatus status;

    private LocalDateTime transactionDate;

    private ErrorReason errorReason;

    public Transaction() {
        this.entries = new ArrayList<>();
        this.status = TransactionStatus.PENDING;
        this.transactionDate = LocalDateTime.now();
    }

    public void addEntry(TransactionEntry entry) {
        this.entries.add(entry);
    }
}
