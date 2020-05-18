package com.payment.api.repository.mongo;

import com.payment.api.model.entity.PaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * The type Payment mongodb repository.
 */
@Repository
public interface PaymentMongoRepository extends MongoRepository<PaymentEntity, String> {
}

