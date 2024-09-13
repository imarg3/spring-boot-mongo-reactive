package org.code.domain.account;

public class TransactionException extends RuntimeException {

    private final Transaction transaction;

    public TransactionException(Transaction transaction) {
        super("Transaction " + transaction.getId() + " failed due to " + transaction.getErrorReason());
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }
}
