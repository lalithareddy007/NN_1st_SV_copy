package com.numpyninja.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.numpyninja.lms.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository< User, String>{

        //Optional<User> findById(String s);
        Optional<User> findByUserPhoneNumber(Long userPhoneNumber);
        
       @Query(value="select u.user_first_name, u.user_id  from tbl_lms_user as u ,"+
        		"tbl_lms_userrole_map m, "+
        		" tbl_lms_role r  "+
        		"where m.role_id=r.role_id" +
        		" and r.role_name='ROLE_STAFF' and m.user_role_status='Active' and u.user_id = m.user_id", nativeQuery=true)
        
         List<Object> getAllStaffList();
    @Query("SELECT u FROM User u WHERE u.userId IN :userId")
    List<User> findByUserId(List<String> userId);
}
