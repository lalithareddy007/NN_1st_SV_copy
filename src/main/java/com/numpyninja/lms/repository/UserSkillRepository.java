package com.numpyninja.lms.repository;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill,String> {

    @Query(value = "Select * from tbl_lms_userskill_map WHERE user_id=?", nativeQuery = true)
    List<UserSkill> findByUserId(String userId);


    List<UserSkill> findByUser(User user);

    @Query(value = "Select * from tbl_lms_userskill_map WHERE user_id=?", nativeQuery = true)
    List<UserSkill> getByUserId(String id);

    //void deleteByUser(User user);
    @Query(value = "select * from tbl_lms_userskill_map WHERE user_id=?",nativeQuery = true)
    List<UserSkill> existsByUserId(String id);
}
