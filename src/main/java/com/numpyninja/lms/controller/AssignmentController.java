package com.numpyninja.lms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.services.AssignmentService;

import javax.validation.Valid;

@RestController
@RequestMapping("/assignments")
public class AssignmentController {
	
	@Autowired
	private AssignmentService assignmentService;
	
	//create an assignment
	@PostMapping("")
	public ResponseEntity<AssignmentDto> createAssignment(@Valid @RequestBody AssignmentDto assignmentDto) {
		AssignmentDto createdAssignmentDto =  this.assignmentService.createAssignment(assignmentDto);
		return new ResponseEntity<>(createdAssignmentDto, HttpStatus.CREATED);
	}
	
	//update an assignment
	@PutMapping("/{id}")
	public ResponseEntity<AssignmentDto> updateAssignment(@Valid @RequestBody AssignmentDto assignmentDto, @PathVariable Long id) {
		AssignmentDto updatedAssignmentDto =  this.assignmentService.updateAssignment(assignmentDto, id);
		return ResponseEntity.ok(updatedAssignmentDto);
	}
	
	//delete an assignment
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse> deleteAssignment(@PathVariable Long id) {
		this.assignmentService.deleteAssignment(id);
		return new ResponseEntity<ApiResponse>(new ApiResponse("Assignment deleted successfully", true), HttpStatus.OK);
	}
	
	//get all assignments
    @GetMapping("")
    public ResponseEntity<List<AssignmentDto>> getAllAssignments() {
        return ResponseEntity.ok(this.assignmentService.getAllAssignments());  
    }
    
    //get assignment by id
  	@GetMapping("/{id}")
  	public ResponseEntity<AssignmentDto> getAssignmentById(@PathVariable(value="id") Long id) {
  		return ResponseEntity.ok(this.assignmentService.getAssignmentById(id));
  	}
  	
  	//get all batches of a program
  	@GetMapping("/batch/{batchId}")
  	public ResponseEntity<List<AssignmentDto>> getAssignmentsForBatch(@PathVariable(value="batchId") Integer batchId) {
  		return ResponseEntity.ok(this.assignmentService.getAssignmentsForBatch(batchId));
  	}
	
}
