package com.numpyninja.lms.controller;

import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.services.AssignmentSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/assignmentsubmission")
public class AssignmentSubmitController {

    @Autowired
    private AssignmentSubmitService assignmentSubmitService;

    @GetMapping("/student/{userID}")
    public ResponseEntity<List<AssignmentSubmit>> getSubmissionsByUserID(@PathVariable String userID) {
        List<AssignmentSubmit> submissionsList = assignmentSubmitService.getSubmissionsByUserID(userID);
        return ResponseEntity.ok(submissionsList);
    }
}
