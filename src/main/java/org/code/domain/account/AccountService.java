package org.code.domain.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<Account> findByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Mono<Long> findAndIncrementBalanceByAccountNumber(String accountNumber, double increment) {
        return accountRepository.findAndIncrementBalanceByAccountNumber(accountNumber, increment);
    }

    public Mono<Account> createAccount(Account account) {
        return accountRepository.save(account);
    }
}
