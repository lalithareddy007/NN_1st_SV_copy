package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentSubmitMapper;
import com.numpyninja.lms.repository.AssignmentRepository;
import com.numpyninja.lms.repository.AssignmentSubmitRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AssignmentSubmitService {

    //@Autowired
    public AssignmentSubmitRepository assignmentSubmitRepository;

    //@Autowired
    public UserRepository userRepository;

    //@Autowired
    public AssignmentRepository assignmentRepository;

    //@Autowired
    public AssignmentSubmitMapper assignmentSubmitMapper;
    
   // @Autowired
	private ProgBatchRepository batchRepository;

    public AssignmentSubmitService(AssignmentSubmitRepository assignmentSubmitRepository,
                                   AssignmentRepository assignmentRepository,
                                   UserRepository userRepository,
                                   AssignmentSubmitMapper assignmentSubmitMapper,
                                  ProgBatchRepository batchRepository
                                  ){
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.assignmentSubmitMapper = assignmentSubmitMapper;
       this.batchRepository = batchRepository;
    }

    public List<AssignmentSubmitDTO> getSubmissionsByUserID(String userId){

        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User","UserID",userId);

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository.findByUser_userId(userId);
        List<AssignmentSubmitDTO> assignmentSubmitDTOs = assignmentSubmitMapper
                .toAssignmentSubmitDTOList(assignmentSubmitList);
        return assignmentSubmitDTOs;
    }

    public AssignmentSubmitDTO createSubmissions(AssignmentSubmitDTO assignmentSubmitDTO) {
        validateAssignmentSubmitDTO(assignmentSubmitDTO);

        String studentId = assignmentSubmitDTO.getUserId();
        if(!userRepository.existsById(studentId))
                throw (new ResourceNotFoundException("Student","ID",studentId));

        Long assignmentId = assignmentSubmitDTO.getAssignmentId();
        if(!assignmentRepository.existsById(assignmentId))
                throw(new ResourceNotFoundException("Assignment", "ID", assignmentId));

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository
                                    .findByStudentIdAndAssignmentId(studentId,assignmentId);
        if(!assignmentSubmitList.isEmpty()) {
            throw (new DuplicateResourceFoundException("Assignment with ID " + assignmentId + " already submitted by student " + studentId
                    + ". Please visit 'Submissions' to resubmit assignment!"));
        }

        AssignmentSubmit assignmentSubmit = assignmentSubmitMapper.toAssignmentSubmit(assignmentSubmitDTO);
        LocalDateTime presentDateTime = LocalDateTime.now();
        assignmentSubmit.setSubDateTime(Timestamp.valueOf(presentDateTime));
        assignmentSubmit.setCreationTime(Timestamp.valueOf(presentDateTime));
        assignmentSubmit.setLastModTime(Timestamp.valueOf(presentDateTime));
        AssignmentSubmit createdAssignmentSubmit = assignmentSubmitRepository.save(assignmentSubmit);
        AssignmentSubmitDTO createdAssignmentSubmitDTO = assignmentSubmitMapper.toAssignmentSubmitDTO(createdAssignmentSubmit);
        return createdAssignmentSubmitDTO;
    }

    private void validateAssignmentSubmitDTO(AssignmentSubmitDTO assignmentSubmitDTO){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<AssignmentSubmitDTO>> violationSet = validator.validate(assignmentSubmitDTO);
        StringBuffer sb = new StringBuffer();
        violationSet.forEach(a->{
            sb.append(a.getMessage());
            sb.append("\n ");
        });
        if(StringUtils.hasLength(sb)){
            throw new InvalidDataException(sb.toString());
        }
    }
    
    public List<AssignmentSubmitDTO> getAllSubmissions(){
    	
    	List<AssignmentSubmit> assignmentSubmit =	this.assignmentSubmitRepository.findAll();
	
    	List<AssignmentSubmitDTO> assignmentSubmitDto = assignmentSubmitMapper.toAssignmentSubmitDTOList(assignmentSubmit);
    	return assignmentSubmitDto;
    	
    }
    
//get submissions by batchid
    public List<AssignmentSubmitDTO> getSubmissionsByBatch(Integer batchid){
    	
    List<AssignmentSubmit> assignmentsubmitList = new ArrayList<AssignmentSubmit>();
    	
    List<AssignmentSubmit> assignmentsubmits =	this.assignmentSubmitRepository.findAll();
    	
    assignmentsubmits.forEach((as)->{
    Assignment 	assignment2 =   as.getAssignment();
    Integer bid = assignment2.getBatch().getBatchId(); 	
    if(bid == batchid )
    assignmentsubmitList.add(as);	
    	});
    	
    List<AssignmentSubmitDTO> assignmentSubmitDTOs = assignmentSubmitMapper
    .toAssignmentSubmitDTOList(assignmentsubmitList);
	return assignmentSubmitDTOs;
    	
    } 
    
}
