package com.payment.api.model.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "databaseSequences")
public class DatabaseSequenceEntity implements Serializable {
	
	private static final long serialVersionUID = 345982941498258105L;
	
	@Id
	private String id;
	private Long mid;
}