package org.code.domain.account;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

@Service
@AllArgsConstructor
public class TransactionTemplate {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<Transaction> createTransaction(Transaction transaction) {
        return reactiveMongoTemplate.save(transaction);
    }

    public Mono<Transaction> findAndUpdateStatusById(String id, TransactionStatus status) {
        Query query = query(where("_id").is(id));
        Update update = update("status", status);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return reactiveMongoTemplate.findAndModify(query, update, options, Transaction.class);
    }

    public Mono<Transaction> findAndUpdateStatusById(String id, TransactionStatus status, ErrorReason errorReason) {
        Query query = query(where("_id").is(id));
        Update update = update("status", status).set("errorReason", errorReason);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return reactiveMongoTemplate.findAndModify(query, update, options, Transaction.class);
    }
}
