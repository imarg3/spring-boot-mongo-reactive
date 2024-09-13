package org.code.domain.account;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveMongoRepository<Account, String> {
    @Query("{accountNumber: '?0'}")
    Mono<Account> findByAccountNumber(String accountNumber);

    @Update("{'$inc': {'balance': ?1}}")
    Mono<Long> findAndIncrementBalanceByAccountNumber(String accountNumber, double increment);
}
