# Reactive Java Spring Boot with MongoDB

## Technologies

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Reactive](https://spring.io/reactive)
- [Spring Data](https://spring.io/projects/spring-data)
- [MongoDB](https://www.mongodb.com/docs/)

## Features

* **Interacting with MongoDB using ReactiveMongoRepositories.**
* **Interacting with MongoDB using ReactiveMongoTemplate.**
* **Wrapping queries in a** [multi-document ACID transaction](https://www.mongodb.com/products/capabilities/transactions).

## Details

- The ReactiveMongoTemplate provides us with more customizable ways to interact with MongoDB and is a thinner layer of abstraction compared to ReactiveMongoRepository.
- The scope of our transactions can be achieved using:
  - **Using TransactionalOperator**
  
    The ReactiveMongoTransactionManager provides us with a TransactionOperator.
    We can then define the scope of a transaction by appending .as(transactionalOperator::transactional) to the method call.

  - **Using @Transactional annotation**
  
    We can also simply define the scope of our transaction by annotating the method with the @Transactional annotation.
