package com.numpyninja.lms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.numpyninja.lms.entity.Batch;

@Repository
public interface ProgBatchRepository extends JpaRepository<Batch, Integer>  {
 
	List<Batch> findByBatchName(String batchName);

    List<Batch> findByBatchNameContainingIgnoreCaseOrderByBatchIdAsc(String batchName);

    List<Batch> findByProgramProgramId ( Long programId); 
    
	Batch findByBatchNameAndProgram_ProgramId ( String batchName, Long programId);
    
}
