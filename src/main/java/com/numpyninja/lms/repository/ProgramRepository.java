package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.ProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<ProgramEntity, Long> {

    List<ProgramEntity> findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(String programName);

}