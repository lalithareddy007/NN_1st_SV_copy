package com.numpyninja.lms.controller;


import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.services.ProgramServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping
public class ProgramController{
    @Autowired
    private ProgramServices programServices;

    @Autowired
    private ProgramRepository programRepository;
    
  //get list of programs
  	@GetMapping(value = "/allPrograms")
  	private ResponseEntity<?> getPrograms()  throws ResourceNotFoundException 
  	{ 
  		System.out.println("in getall programs");
  		List<ProgramDTO> programList = programServices.getAllPrograms();
  		return ResponseEntity.ok(programList);  
  	}  
  	
  	//retrieves the details of a specific program
  	@GetMapping(path="programs/{programId}")  
  	//@ResponseBody
  	private ResponseEntity <ProgramDTO> getOneProgramById(@PathVariable("programId") @NotBlank @Positive Long programId)throws ResourceNotFoundException
  	{  
  	return ResponseEntity.ok().body(programServices.getProgramsById(programId));
  	}  
  			
  	//post mapping that creates the program detail in the database  
  	@PostMapping(path="/saveprogram",consumes = "application/json", produces = "application/json")  
  	//@ResponseBody
  	private ResponseEntity<?> createAndSaveProgram(@Valid @RequestBody ProgramDTO newProgram)throws  DuplicateResourceFoundException
  	{  
  	ProgramDTO savedProgramedDTO = programServices.createAndSaveProgram(newProgram);
  	return ResponseEntity.status(HttpStatus.CREATED).body(savedProgramedDTO);  
  	} 
  				
  	//put mapping that updates the program detail by programId  
  	@PutMapping(path="/putprogram/{programId}", consumes = "application/json", produces = "application/json")  
  	//@ResponseBody
  	private ResponseEntity <ProgramDTO> updateProgramById(@PathVariable("programId")@NotBlank @Positive Long programId ,@Valid @RequestBody ProgramDTO modifyProgram) throws ResourceNotFoundException
  	{  
  	return ResponseEntity.ok(programServices.updateProgramById(programId,modifyProgram));
  	} 
  			
  	//creating put mapping that updates the program detail  by programName 
  	@PutMapping(path="/program/{programName}", consumes = "application/json", produces = "application/json")  
  	//@ResponseBody
  	private ResponseEntity <ProgramDTO> updateProgramByName(@Valid @PathVariable("programName") String programName ,@Valid @RequestBody ProgramDTO modifyProgram)throws ResourceNotFoundException  
  	{  
  	return ResponseEntity.ok(programServices.updateProgramByName(programName,modifyProgram));
  	} 
  			 
  	//delete mapping that deletes a specified program  
  	@DeleteMapping(path="/deletebyprogid/{programId}")  
  	@ResponseBody
  	private ResponseEntity<String>  deleteByProgramId(@PathVariable("programId")@NotBlank @Positive Long programId) throws ResourceNotFoundException
  	{
  	System.out.println("in delete by programID controller");
  	boolean deleted = programServices.deleteByProgramId(programId);
  	if(deleted){
		String message = "Message:" + " Program Id-" + programId + " is deleted Successfully!";
  		return ResponseEntity.ok(message);}
  			else
  		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  	}
  			 
  	//delete mapping that deletes a specified program by ProgramName  
  	@DeleteMapping(path="/deletebyprogname/{programName}")  
  	//@ResponseBody
  	private ResponseEntity<?>  deleteByProgramName(@PathVariable("programName")@NotBlank @NotNull String programName) throws ResourceNotFoundException  
  	{  
  	System.out.println("in delete by programName controller");
  	boolean deleted =programServices.deleteByProgramName(programName);
  	if(deleted){
		String message = "Message:" + " Program Name -" + programName + " is deleted Successfully!";
  		return ResponseEntity.ok(message);}
  			else
  		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  	}
}
