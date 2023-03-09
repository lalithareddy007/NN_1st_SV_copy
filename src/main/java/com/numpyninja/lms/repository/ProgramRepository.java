package com.numpyninja.lms.repository;


import com.numpyninja.lms.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {

    List<Program> findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(String programName);
	
	List<Program> findByProgramName ( String programName);

}