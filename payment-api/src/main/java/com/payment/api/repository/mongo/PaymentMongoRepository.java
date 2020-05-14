package com.payment.api.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.payment.api.model.entity.PaymentEntity;

@Repository
public interface PaymentMongoRepository extends MongoRepository<PaymentEntity, String> {
}

