package com.numpyninja.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class Encrypted_Key {
	@Id
	@Column
	private Integer id;

	@Column
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] EncryptedKey;
}
