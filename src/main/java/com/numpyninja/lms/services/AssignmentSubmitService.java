package com.numpyninja.lms.services;

import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.repository.AssignmentSubmitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentSubmitService {

    @Autowired
    public AssignmentSubmitRepository assignmentSubmitRepository;

    public List<AssignmentSubmit> getSubmissionsByUserID(String userId){

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository.findBySubUserID(userId);
        return assignmentSubmitList;
    }
}
