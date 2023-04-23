package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, String> {
    public Optional<UserLogin> findByUserLoginEmailIgnoreCase(String username);
}