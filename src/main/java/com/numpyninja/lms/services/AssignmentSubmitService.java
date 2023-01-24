package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.exception.DuplicateResourceFound;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentSubmitMapper;
import com.numpyninja.lms.repository.AssignmentRepository;
import com.numpyninja.lms.repository.AssignmentSubmitRepository;
import com.numpyninja.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentSubmitService {

    @Autowired
    public AssignmentSubmitRepository assignmentSubmitRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public AssignmentRepository assignmentRepository;

    @Autowired
    public AssignmentSubmitMapper assignmentSubmitMapper;

    public List<AssignmentSubmitDTO> getSubmissionsByUserID(String userId){

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository.findByUser_userId(userId);
        System.out.println("Submissions list:"+assignmentSubmitList.size());
        System.out.println("Submissions list:"+assignmentSubmitList.get(0));
        List<AssignmentSubmitDTO> assignmentSubmitDTOS = assignmentSubmitMapper
                .toAssignmentSubmitDTOList(assignmentSubmitList);
        System.out.println("Submissions DTO list:"+assignmentSubmitDTOS.size());
        System.out.println("Submissions DTO list:"+assignmentSubmitDTOS.get(0));
        return assignmentSubmitDTOS;
    }

    public AssignmentSubmitDTO createSubmissions(AssignmentSubmitDTO assignmentSubmitDTO) {
        String studentId = assignmentSubmitDTO.getUserId();
        if(studentId==null || !studentId.isEmpty() && !userRepository.existsById(studentId))
                throw (new ResourceNotFoundException("Student","ID",studentId));

        Long assignmentId = assignmentSubmitDTO.getAssignmentId();
        if(assignmentId==null || !assignmentRepository.existsById(assignmentId))
                throw(new ResourceNotFoundException("Assignment", "ID", assignmentId));

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository
                                    .findByStudentIdAndAssignmentId(studentId,assignmentId);
        if(assignmentSubmitList.size()>0) {
            throw (new DuplicateResourceFoundException("Assignment with ID " + assignmentId + " already submitted by student " + studentId
                    + ". Please visit 'Submissions' to resubmit assignment!"));
        }

        AssignmentSubmit assignmentSubmit = assignmentSubmitMapper.toAssignmentSubmit(assignmentSubmitDTO);
        LocalDateTime presentDateTime = LocalDateTime.now();
        assignmentSubmit.setCreationTime(Timestamp.valueOf(presentDateTime));
        assignmentSubmit.setLastModTime(Timestamp.valueOf(presentDateTime));
        AssignmentSubmit createdAssignmentSubmit = assignmentSubmitRepository.save(assignmentSubmit);
        AssignmentSubmitDTO createdAssignmentSubmitDTO = assignmentSubmitMapper.toAssignmentSubmitDTO(createdAssignmentSubmit);
        return createdAssignmentSubmitDTO;
    }
}
