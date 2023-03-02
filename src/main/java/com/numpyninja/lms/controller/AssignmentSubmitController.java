package com.numpyninja.lms.controller;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.services.AssignmentSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/assignmentsubmission")
public class AssignmentSubmitController {

    //@Autowired
    private AssignmentSubmitService assignmentSubmitService;

    public AssignmentSubmitController(AssignmentSubmitService assignmentSubmitService) {
        this.assignmentSubmitService = assignmentSubmitService;
    }

    @GetMapping("/student/{userID}")
    public ResponseEntity<List<AssignmentSubmitDTO>> getSubmissionsByUserID(@PathVariable String userID) {
        System.out.println(userID);
        List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getSubmissionsByUserID(userID);
        return ResponseEntity.ok(submissionsListDTO);
    }

    @PostMapping(path="", consumes="application/json", produces="application/json")
    public ResponseEntity<AssignmentSubmitDTO> createSubmissions( @RequestBody AssignmentSubmitDTO assignmentSubmitDTO)
    {
        AssignmentSubmitDTO createdAssignSubmitDTO = assignmentSubmitService.createSubmissions(assignmentSubmitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignSubmitDTO);
    }
//<<<<<<< HEAD
    
   @GetMapping("/studentbatch/{batchid}")
    public ResponseEntity<List<AssignmentSubmitDTO>> getSubmissionsByBatch(@PathVariable Integer batchid) {
       
    	List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getSubmissionsByBatch(batchid);
        return ResponseEntity.ok(submissionsListDTO);
    }
    
    @GetMapping("/student")
    public ResponseEntity<List<AssignmentSubmitDTO>> getAllSubmissions() {
       
    	List<AssignmentSubmitDTO> submissionsListDTO = assignmentSubmitService.getAllSubmissions();
        return ResponseEntity.ok(submissionsListDTO);
    }
    
//=======

    @PutMapping(path="/{id}", consumes="application/json", produces="application/json")
    public ResponseEntity<AssignmentSubmitDTO> updateSubmissions( @RequestBody AssignmentSubmitDTO assignmentSubmitDTO,
                                                                  @PathVariable Long id)
    {
        AssignmentSubmitDTO updatedAssignSubmitDTO = assignmentSubmitService.updateSubmissions(assignmentSubmitDTO,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssignSubmitDTO);
    }
//>>>>>>> 1dc3168166591ef2fc65f4444209a622ec0900a0
    
    @GetMapping("/getGrades/{assignmentId}")
    public ResponseEntity<List<AssignmentSubmitDTO>> getGradesByAssignmentId(@PathVariable Long assignmentId)
    {
    List<AssignmentSubmitDTO> getListGradesAssignmentDTO = assignmentSubmitService.getGradesByAssinmentId((assignmentId));
    return ResponseEntity.ok(getListGradesAssignmentDTO);
    }

}
