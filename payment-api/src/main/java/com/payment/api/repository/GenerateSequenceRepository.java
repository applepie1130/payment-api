package com.payment.api.repository;


import com.payment.api.model.entity.DatabaseSequenceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * The type Generate sequence repository.
 */
@Repository
public class GenerateSequenceRepository {

	private MongoOperations mongoOperations;

	/**
	 * Instantiates a new Generate sequence repository.
	 *
	 * @param mongoOperations the mongo operations
	 */
	@Autowired
	public GenerateSequenceRepository(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	/**
	 * Generate sequence string.
	 *
	 * @param seqName the seq name
	 * @return the string
	 */
	public synchronized String generateSequence(String seqName) {
		
		DatabaseSequenceEntity sequence = mongoOperations.findAndModify(
											query(where("_id").is(seqName)),
											new Update().inc("seq", 1), 
											options().returnNew(true).upsert(true),
											DatabaseSequenceEntity.class);
		
		if (sequence.getSeq().compareTo(999999999999L) == 0) {
			mongoOperations.findAndModify(
					query(where("_id").is(seqName)),
					new Update().set("seq", 0), 
					options().returnNew(true).upsert(true),
					DatabaseSequenceEntity.class);
		}
		
		String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		
		return new StringBuffer().append(prefix)
								.append(String.format("%012d", !Objects.isNull(sequence) ? sequence.getSeq() : 1) )
								.toString();
	}
}
