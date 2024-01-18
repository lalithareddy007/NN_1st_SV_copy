package com.numpyninja.lms.services;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

import com.numpyninja.lms.exception.InvalidDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.BatchMapper;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.ProgramRepository;

@Service
public class ProgBatchServices {
    @Autowired
    private ProgBatchRepository progBatchRepository;

    @Autowired
    private ProgramRepository programRepository;
    
    @Autowired
    private BatchMapper batchMapper;
    
    // method for All batch
    public List<BatchDTO> getAllBatches() {
    	return batchMapper.toBatchDTOs(progBatchRepository.findAll(Sort.by("batchName")));
    }
    
    public List<BatchDTO> getAllBatches(String searchString) {
    	List<Batch> batches = progBatchRepository.findByBatchNameContainingIgnoreCaseOrderByBatchIdAsc(searchString);
    	return batchMapper.toBatchDTOs( batches );
    }

    //method for get single batch by id
    public  BatchDTO findBatchById(Integer batchId) {
    	Batch batch = progBatchRepository.findById(batchId).orElseThrow(()-> new ResourceNotFoundException("Batch", "Id", batchId));
        return batchMapper.toBatchDTO( batch );
    }

    //method for finding BatchName
   /* public List<BatchDTO> findByProgramBatchName(String name) {
    	List<Batch> batchList = progBatchRepository.findByBatchName(name);
    	return batchMapper.toBatchDTOs( batchList );
    }*/


    //method for finding BatchName
    public List<BatchDTO> findByProgramBatchName(String name) {
        if(!(name.isEmpty())) {
            List<Batch> batchList = progBatchRepository.findByBatchName(name);
            if(batchList.size()<=0) {
                System.out.println("programBatch with " + name+"not found");
                throw new ResourceNotFoundException("programBatch with id"+ name +"not found");
            }
            return batchMapper.toBatchDTOs( batchList );
        }
        else {
            System.out.println("Batch cannot be blank or null");
            throw new IllegalArgumentException();
        }
    }

    // create new  Batch under Program     
    public BatchDTO createBatch(BatchDTO batchDTO ) {
    	Long programId = batchDTO.getProgramId();
    	Batch newBatch = batchMapper.toBatch(batchDTO );
    	Program program = programRepository.findById( programId ).orElseThrow(()-> new ResourceNotFoundException("Program", "Id", programId));

        if(program.getProgramStatus().equalsIgnoreCase("Inactive"))
        {
            throw new InvalidDataException("Batch cannot be created for a Program with Inactive status");
        }
        else {
            newBatch.setProgram(program);
        }
    	//check null values
    	boolean batchstatus = batchDTO.getBatchStatus().equalsIgnoreCase("null");
    	if (batchstatus)
        throw new ResourceNotFoundException("Batch", "Id not found",batchDTO.getBatchStatus());
    	
    	Batch result = progBatchRepository.findByBatchNameAndProgram_ProgramId(newBatch.getBatchName(), programId);
		if ( result != null)
			 throw new DuplicateResourceFoundException ( "Program " +program.getProgramName() + " with Batch-" + newBatch.getBatchName() 
			            + " already exists:" + " ; Please give a different batch Name or Choose a different Program"); 
    	newBatch.setCreationTime(new Timestamp( new Date().getTime()));
    	newBatch.setLastModTime(new Timestamp( new Date().getTime()));
    	Batch batchCreated = progBatchRepository.save(newBatch);
    	return batchMapper.toBatchDTO( batchCreated );
    }

    
    //Update new Batch                    
    public BatchDTO updateBatch(BatchDTO batchDTO, Integer batchId) {

        if(progBatchRepository.existsByBatchNameAndProgramProgramId(batchDTO.getBatchName(),batchDTO.getProgramId())){
            String message = String.format("Batch with the name=%s and programId=%d already exists",
                    batchDTO.getBatchName(), batchDTO.getProgramId());
            throw new DuplicateResourceFoundException(message);
        }

        Batch exisBatch = progBatchRepository.findById(batchId).orElseThrow(()-> new ResourceNotFoundException("Batch", "Id", batchId));
    	Batch batchDetailToUpdt = batchMapper.toBatch(batchDTO );
    	Long programId = batchDTO.getProgramId();
    	Program program = programRepository.findById( programId ).orElseThrow(()-> new ResourceNotFoundException("Program", "Id", programId));
    	batchDetailToUpdt.setProgram(program);
    	batchDetailToUpdt.setCreationTime(exisBatch.getCreationTime() );
    	batchDetailToUpdt.setLastModTime( new Timestamp( new Date().getTime()));
        batchDetailToUpdt.setBatchId(exisBatch.getBatchId()); //setting existing batch id
    	return batchMapper.toBatchDTO( progBatchRepository.save(batchDetailToUpdt) );
    }


    // get Batches by Program ID         
   /* public List<BatchDTO> findBatchByProgramId(Long programid) {
      return batchMapper.toBatchDTOs(progBatchRepository.findByProgramProgramId(programid));
    }*/
    // get Batches by Program ID
    public List<BatchDTO> findBatchByProgramId(Long programid) {
        if (programid != null) {
            List<Batch> result = progBatchRepository.findByProgramProgramId(programid);
            if (!(result.size() <= 0)) {
                return (batchMapper.toBatchDTOs(result));

            } else {
                throw new ResourceNotFoundException("batch with this programId " + programid + "not found");
            }
        } else {
            System.out.println("programId search string cannot be null");
            throw new IllegalArgumentException();
        }
    }

    public void deleteProgramBatch(Integer batchId) {
        if(batchId!=null) {
            Boolean value = progBatchRepository.existsById(batchId);
            if (value) {
                Batch batchEntity = progBatchRepository.findById(batchId).get();
                batchEntity.setBatchStatus("Inactive");
                progBatchRepository.save(batchEntity);

            } else {
                System.out.println("No record found with batchId" + "  " + batchId);
                throw new ResourceNotFoundException("No record found with batchId" + batchId);
            }
        }
        else {
            throw new InvalidDataException("BatchId is mandatory");
        }
    }

}
	

