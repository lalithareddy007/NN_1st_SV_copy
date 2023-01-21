package com.numpyninja.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.numpyninja.lms.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository< User, String>{

        Optional<User> findById(String s);
        Optional<User> findByUserPhoneNumber(Long userPhoneNumber);
}
