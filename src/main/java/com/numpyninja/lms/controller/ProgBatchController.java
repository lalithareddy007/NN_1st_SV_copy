package com.numpyninja.lms.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.services.ProgBatchServices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags="Program Batch Controller", description="All Program Batch CRUD Operations")
public class ProgBatchController  {

    @Autowired
    private ProgBatchServices batchService;
    
    @GetMapping("/batches")
    @ApiOperation("Get All Batches")
    public List<BatchDTO> getAll(String searchString) {
        if (StringUtils.isNotBlank(searchString)) {
        	return  batchService.getAllBatches(searchString);           
        } else {
            return  batchService.getAllBatches();
        }
    }
    
	@GetMapping ( path = "/batches/batchId/{batchId}", produces = "application/json")
	@ApiOperation("Get Batch details by Batch Id")
	public ResponseEntity<BatchDTO> getBatchById(@PathVariable(value="batchId") @Positive Integer batchId) {
		return ResponseEntity.ok(  batchService.findBatchById(batchId) );
	}
	
	
	//Get Batch by Name
	@GetMapping ( path = "/batches/batchName/{batchName}", produces = "application/json")
	@ApiOperation("Get Batch details by Batch Name")
	public ResponseEntity<List<BatchDTO>> getBatchByName(@PathVariable(value="batchName") String batchName) {
		return ResponseEntity.ok(  batchService.findByProgramBatchName(batchName) );
	}
	
	
	//Get Batch List by Program
	@GetMapping ( path = "/batches/program/{programId}", produces = "application/json")
	@ApiOperation("Get Batch details by Program Id")
	public ResponseEntity<List<BatchDTO>> getBatchByProgram( @PathVariable(value="programId") @Positive Long programId) {
		return ResponseEntity.ok(  batchService.findBatchByProgramId(programId) );
	}
	
	
    @PostMapping(path = "/batches", consumes = "application/json", produces = "application/json")
    @ApiOperation("Create New Batch")
    public ResponseEntity<BatchDTO> createBatch( @Valid @RequestBody BatchDTO batchDTO ) {
        BatchDTO batchDTOCreatd = batchService.createBatch(batchDTO );
        return ResponseEntity.status(HttpStatus.CREATED).body(batchDTOCreatd);
    }
    
    
    //Update program Information
    @PutMapping(path = "/batches/{batchId}", consumes = "application/json", produces = "application/json")
    @ApiOperation("Update Batch details")
    public ResponseEntity<BatchDTO> updateBatch( @Valid @RequestBody BatchDTO batchDTO,  @PathVariable Integer batchId ) {
    	BatchDTO batchDTOUpd = batchService.updateBatch( batchDTO, batchId);
    	return ResponseEntity.ok( batchDTOUpd );
    }

    
    @DeleteMapping(path = "/batches/{id}" , produces = "application/json" )
    @ApiOperation("Delete existing Batch")
    public String deleteBatch( @PathVariable Integer id) {
        batchService.deleteProgramBatch(id);
        String message = "Message:" + " Batch with Id-" + id + " deleted Successfully!";
        return message;
    }
   // @DeleteMapping(path = "/batches/{id}" , produces = "application/json" )
   // public ResponseEntity<ApiResponse>deleteBatch( @PathVariable Integer id) {
     //    this.batchService.deleteProgramBatch(id);
       //  return new ResponseEntity<ApiResponse>(new ApiResponse("Batch deleted successfully", true), HttpStatus.OK);

}
