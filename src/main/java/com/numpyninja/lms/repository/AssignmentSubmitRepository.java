package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.AssignmentSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmitRepository extends JpaRepository<AssignmentSubmit, Long> {

    List<AssignmentSubmit> findByUser_userId(String userId);

    @Query(value="select * from tbl_lms_submissions where sub_student_id=?1 and sub_a_id=?2", nativeQuery = true)
    List<AssignmentSubmit> findByStudentIdAndAssignmentId(String stdId, Long assignId);

}
