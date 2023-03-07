package com.numpyninja.lms.controller;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.services.AssignmentSubmitService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/assignmentsubmission")
@Api(tags="Assignment Submission Controller", description="Assignment Submission CRUD Operations")
public class AssignmentSubmitController {

    //@Autowired
    private AssignmentSubmitService assignmentSubmitService;

    public AssignmentSubmitController(AssignmentSubmitService assignmentSubmitService) {
        this.assignmentSubmitService = assignmentSubmitService;
    }

    @GetMapping("/student/{userID}")
    @ApiOperation("Get Submissions by User ID")
    public ResponseEntity<List<AssignmentSubmitDTO>> getSubmissionsByUserID(@PathVariable String userID) {
        System.out.println(userID);
        List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getSubmissionsByUserID(userID);
        return ResponseEntity.ok(submissionsListDTO);
    }

    @PostMapping(path="", consumes="application/json", produces="application/json")
    @ApiOperation("Create New Submission")
    public ResponseEntity<AssignmentSubmitDTO> createSubmissions( @RequestBody AssignmentSubmitDTO assignmentSubmitDTO)
    {
        AssignmentSubmitDTO createdAssignSubmitDTO = assignmentSubmitService.createSubmissions(assignmentSubmitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignSubmitDTO);
    }
    
   @GetMapping("/studentbatch/{batchid}")
   @ApiOperation("Get Submissions By Batch")
    public ResponseEntity<List<AssignmentSubmitDTO>> getSubmissionsByBatch(@PathVariable Integer batchid) {
       
    	List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getSubmissionsByBatch(batchid);
        return ResponseEntity.ok(submissionsListDTO);
    }
    
    @GetMapping("/student")
    @ApiOperation("Get All Submissions")
    public ResponseEntity<List<AssignmentSubmitDTO>> getAllSubmissions() {
       
    	List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getAllSubmissions();
        return ResponseEntity.ok(submissionsListDTO);
    }
    

    @PutMapping(path="/{id}", consumes="application/json", produces="application/json")
    @ApiOperation("Update details of Submission")
    public ResponseEntity<AssignmentSubmitDTO> updateSubmissions( @RequestBody AssignmentSubmitDTO assignmentSubmitDTO,
                                                                  @PathVariable Long id)
    {
        AssignmentSubmitDTO updatedAssignSubmitDTO = assignmentSubmitService.updateSubmissions(assignmentSubmitDTO,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssignSubmitDTO);
    }

}
