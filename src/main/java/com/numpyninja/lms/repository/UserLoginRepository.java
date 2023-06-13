package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, String> {

    public Optional<UserLogin> findByUserLoginEmailIgnoreCase(String userLoginEmail);

    Optional<UserLogin> findByUserUserId(String userId);

    @Modifying(clearAutomatically = true)
    @Query("update UserLogin u set u.userLoginEmail = :userEmailToUpdate,u.loginStatus = :userLoginStatusToUpdate , u.lastModTime= CURRENT_TIMESTAMP where u.userId = :userId")
    void updateUserLogin(@Param(value = "userId") String userId, @Param(value = "userEmailToUpdate")String userEmailToUpdate,@Param(value = "userLoginStatusToUpdate") String userLoginStatusToUpdate);

}