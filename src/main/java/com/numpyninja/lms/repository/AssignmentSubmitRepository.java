package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.entity.Batch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmitRepository extends JpaRepository<AssignmentSubmit, Long> {

    List<AssignmentSubmit> findByUser_userId(String userId);

    @Query(value="select * from tbl_lms_submissions where sub_student_id=?1 and sub_a_id=?2", nativeQuery = true)
    Optional<List<AssignmentSubmit>> findByStudentIdAndAssignmentId(String stdId, Long assignId);

    @Query(value="select * from tbl_lms_submissions where sub_a_id=?", nativeQuery=true)
    List<AssignmentSubmit> getGradesByAssignmentId(Long assingmentId);

    @Query(value="select * from tbl_lms_submissions where sub_student_id=? and grade<>-1;", nativeQuery=true)
    List<AssignmentSubmit> getGradesByStudentID(String studentID);

    List<AssignmentSubmit> findByAssignment_Batch_BatchId(Integer batchId);
}
