package org.code.application;

import com.mongodb.DuplicateKeyException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.code.domain.ResponseMessage;
import org.code.domain.account.Account;
import org.code.domain.account.AccountNotFoundException;
import org.code.domain.account.AccountService;
import org.code.domain.account.ErrorReason;
import org.code.domain.account.Transaction;
import org.code.domain.account.TransactionEntry;
import org.code.domain.account.TransactionException;
import org.code.domain.account.TransactionService;
import org.code.domain.account.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    private final TransactionService transactionService;

    private static void printLastLineStackTrace(String context) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        log.info("Stack trace's last line: {} from {}",
                stackTraceElements[stackTraceElements.length - 1].toString(), context);
    }

    @PostMapping("/create-account")
    public Mono<Account> createAccount(@RequestBody Account account) {
        printLastLineStackTrace("POST /account");
        return accountService.createAccount(account);
    }

    @GetMapping("/account/{accountNumber}")
    public Mono<Account> getAccount(@PathVariable String accountNumber) {
        printLastLineStackTrace("GET /account/" + accountNumber);
        return accountService.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(new AccountNotFoundException()));
    }

    @PostMapping("/account/{accountNumber}/debit")
    public Mono<Transaction> debitAccount(@PathVariable String accountNumber, @RequestBody Map<String, Object> requestBody) {
        printLastLineStackTrace("POST /account/" + accountNumber + "/debit");
        var transaction = new Transaction();
        double amount = (Double) requestBody.get("amount");
        transaction.addEntry(new TransactionEntry(accountNumber, amount));
        return transactionService.saveTransaction(transaction)
                .flatMap(transactionService::executeTransaction);
    }

    @PostMapping("/account/{accountNumber}/credit")
    public Mono<Transaction> creditAccount(@PathVariable String accountNumber, @RequestBody Map<String, Object> requestBody) {
        printLastLineStackTrace("POST /account/" + accountNumber + "/credit");
        var transaction = new Transaction();
        double amount = (Double) requestBody.get("amount");
        transaction.addEntry(new TransactionEntry(accountNumber, -amount));
        return transactionService.saveTransaction(transaction)
                .flatMap(transactionService::executeTransaction);
    }

    @PostMapping("/account/{from}/transfer")
    public Mono<Transaction> transfer(@PathVariable String from, @RequestBody TransferRequest transferRequest) {
        printLastLineStackTrace("POST /account/" + from + "/transfer");
        var to = transferRequest.getTo();
        var amount = (Double) transferRequest.getAmount();
        var transaction = new Transaction();
        transaction.addEntry(new TransactionEntry(from, -amount));
        transaction.addEntry(new TransactionEntry(to, amount));
        // save pending transaction then execute
        return transactionService.saveTransaction(transaction)
                .flatMap(transactionService::executeTransaction);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<ResponseMessage> accountNotFound(AccountNotFoundException e) {
        return ResponseEntity.badRequest().body(new ResponseMessage(ErrorReason.ACCOUNT_NOT_FOUND.name()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<ResponseMessage> duplicateAccount(DuplicateKeyException e) {
        return ResponseEntity.badRequest().body(new ResponseMessage(ErrorReason.DUPLICATE_ACCOUNT.name()));
    }

    @ExceptionHandler(TransactionException.class)
    ResponseEntity<Mono<Transaction>> insufficientFunds(TransactionException e) {
        return ResponseEntity.unprocessableEntity().body(transactionService.saveTransaction(e.getTransaction()));
    }
}
