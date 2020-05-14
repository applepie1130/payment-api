package com.payment.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMongoRepository extends MongoRepository<Object, String> {
}
