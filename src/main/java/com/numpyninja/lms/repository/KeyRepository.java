package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Encrypted_Key;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyRepository extends JpaRepository<Encrypted_Key, Integer>{

}
