package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.AssignmentSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentSubmitRepository extends JpaRepository<AssignmentSubmit, Long> {

    List<AssignmentSubmit> findBySubUserID(String userId);
}
