package org.code.domain.account;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionTemplate transactionTemplate;
    private final AccountRepository accountRepository;
    /**
     * Using Transactional Operator to manage transactions
     */
    private final TransactionalOperator transactionalOperator;

    public Mono<Transaction> saveTransaction(Transaction transaction) {
        return transactionTemplate.createTransaction(transaction);
    }

    /**
     * Using @Transactional annotation to manage transactions
     */
    //@Transactional
    public Mono<Transaction> executeTransaction(Transaction transaction) {
        return updateBalances(transaction).onErrorResume(DataIntegrityViolationException.class, e -> {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorReason(ErrorReason.INSUFFICIENT_BALANCE);
            return Mono.error(new TransactionException(transaction));
        }).onErrorResume(AccountNotFoundException.class, e -> {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setErrorReason(ErrorReason.ACCOUNT_NOT_FOUND);
            return Mono.error(new TransactionException(transaction));
        }).then(transactionTemplate.findAndUpdateStatusById(transaction.getId(), TransactionStatus.SUCCESS))
                .as(transactionalOperator::transactional);
    }

    public Flux<Long> updateBalances(Transaction transaction) {
        // read entries to update balances, concatMap maintains the sequence
        Flux<Long> updatedCounts = Flux.fromIterable(transaction.getEntries())
                                    .concatMap(transactionEntry -> accountRepository.findAndIncrementBalanceByAccountNumber(
                                            transactionEntry.getAccountNumber(), transactionEntry.getAmount()
                                    ));

        return updatedCounts.handle((updatedCount, sink) -> {
           if (updatedCount < 1) {
               sink.error(new AccountNotFoundException());
           } else {
               sink.next(updatedCount);
           }
        });
    }
}
