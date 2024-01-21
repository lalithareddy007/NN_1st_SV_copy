package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentSubmitMapper;
import com.numpyninja.lms.repository.*;
import com.numpyninja.lms.util.AssignmentGradedEvent;
import com.numpyninja.lms.util.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmitService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    NotificationService notificationService;


    public AssignmentSubmitRepository assignmentSubmitRepository;

    public UserRepository userRepository;

    public UserRoleMapRepository userRoleMapRepository;

    public AssignmentRepository assignmentRepository;

    public AssignmentSubmitMapper assignmentSubmitMapper;

    private final int DEFAULT_GRADE = -1;
    
   // @Autowired
	private ProgBatchRepository batchRepository;

    public AssignmentSubmitService(AssignmentSubmitRepository assignmentSubmitRepository,
                                   AssignmentRepository assignmentRepository,
                                   UserRepository userRepository,
                                   AssignmentSubmitMapper assignmentSubmitMapper,
                                  ProgBatchRepository batchRepository,
                                   UserRoleMapRepository userRoleMapRepository
                                  ){
        this.assignmentSubmitRepository = assignmentSubmitRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.assignmentSubmitMapper = assignmentSubmitMapper;
       this.batchRepository = batchRepository;
       this.userRoleMapRepository = userRoleMapRepository;
    }

    public List<AssignmentSubmitDTO> getSubmissionsByUserID(String userId){

        if (!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User","UserID",userId);

        List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepository.findByUser_userId(userId);
        List<AssignmentSubmitDTO> assignmentSubmitDTOs = assignmentSubmitMapper
                .toAssignmentSubmitDTOList(assignmentSubmitList);
        return assignmentSubmitDTOs;
    }

    public AssignmentSubmitDTO submitAssignment(AssignmentSubmitDTO assignmentSubmitDTO) {
        validateAssignmentSubmitDTO(assignmentSubmitDTO);

        String studentId = assignmentSubmitDTO.getUserId();
        if(!userRepository.existsById(studentId))
                throw (new ResourceNotFoundException("Student","ID",studentId));

        Long assignmentId = assignmentSubmitDTO.getAssignmentId();

        /**
         * Assignment cannot be submitted post assignment due date
         */
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "ID", assignmentId));
        Timestamp presentDateTime = Timestamp.valueOf(LocalDateTime.now());
        if(presentDateTime.after(assignment.getDueDate())){
            throw (new InvalidDataException("Cannot submit assignment post due date"));
        }

        Optional<List<AssignmentSubmit>> assignmentSubmitList = Optional.ofNullable(assignmentSubmitRepository
                                    .findByStudentIdAndAssignmentId(studentId,assignmentId)).get();

        if(!assignmentSubmitList.get().isEmpty()) {
            throw (new DuplicateResourceFoundException("Assignment with ID " + assignmentId + " already submitted by student " + studentId
                    + ". Please visit 'Submissions' to resubmit assignment!"));
        }

        AssignmentSubmit assignmentSubmit = assignmentSubmitMapper.toAssignmentSubmit(assignmentSubmitDTO);
        //LocalDateTime presentDateTime = LocalDateTime.now();
        assignmentSubmit.setSubDateTime(presentDateTime);
        assignmentSubmit.setCreationTime(presentDateTime);
        assignmentSubmit.setLastModTime(presentDateTime);
        /**
         * Grading options shouldn't be passed from front-end in create/update flow of submissions.
         * Hence grade values will be set to null
         */
        assignmentSubmit.setGrade(DEFAULT_GRADE);
        assignmentSubmit.setGradedBy(null);
        assignmentSubmit.setGradedDateTime(null);

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
    	Batch batch= this.batchRepository.findById(batchid)
    			.orElseThrow(() -> new ResourceNotFoundException("Batch", "ID", batchid));
    	
    	List<Assignment> assignments = this.assignmentRepository.findByBatch(batch);
    	
    	if(assignments.isEmpty())
    		throw new ResourceNotFoundException("Assignment does not exist for Batch ID : "+batchid);
    		
    	List<AssignmentSubmit> assignmentsubmitList = new ArrayList<AssignmentSubmit>();
    	
    	List<Long> assignmentIds = new ArrayList<Long>();
    	
    	for(int i =0;i<assignments.size();i++)
    	{
    		assignmentIds.add(i, assignments.get(i).getAssignmentId());
    	}
       
    	assignmentsubmitList = this.assignmentSubmitRepository.findByAssignment_AssignmentIdIn(assignmentIds);
    	
    	if(assignmentsubmitList.isEmpty())
    	  throw new ResourceNotFoundException("Assignmentsubmissions does not exist for Batch ID : "+batchid);
        
    	List<AssignmentSubmitDTO> assignmentSubmitDTOs = assignmentSubmitMapper
        .toAssignmentSubmitDTOList(assignmentsubmitList);
    	return assignmentSubmitDTOs;
        	
        } 
    
    
    
  public AssignmentSubmitDTO resubmitAssignment(AssignmentSubmitDTO assignmentSubmitDTO, Long submissionId) {
        AssignmentSubmit savedAssignmentSubmit = this.assignmentSubmitRepository.findById(submissionId)
                        .orElseThrow(() ->new ResourceNotFoundException("Submission","ID",submissionId));

        validateAssignmentSubmitDTO(assignmentSubmitDTO);

        String studentId = assignmentSubmitDTO.getUserId();
        /**
        Assignment should only be resubmitted by the same student
         */
        if(!studentId.equals(savedAssignmentSubmit.getUser().getUserId()))
            throw new InvalidDataException("Student with given ID "+studentId+ " cannot submit this assignment");

        Long assignmentId = assignmentSubmitDTO.getAssignmentId();

        /**
        Assignment ID should not change during resubmission
         */
        if(assignmentId!=savedAssignmentSubmit.getAssignment().getAssignmentId())
            throw new InvalidDataException("Assignment with ID " +assignmentId+" is not part of this submission");

        /**
         * Assignment cannot be resubmitted post assignment due date
         */
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "ID", assignmentId));
        Timestamp presentDateTime = Timestamp.valueOf(LocalDateTime.now());
        if(presentDateTime.after(assignment.getDueDate())){
            throw (new InvalidDataException("Cannot submit assignment post due date"));
        }


        /**
        Submission description should not change during resubmission

        String submissionDesc = assignmentSubmitDTO.getSubDesc();
        if(!submissionDesc.equals(savedAssignmentSubmit.getSubDesc()))
            throw new InvalidDataException("Submission Descrition does not match!");
        */
        AssignmentSubmit assignmentSubmit = assignmentSubmitMapper.toAssignmentSubmit(assignmentSubmitDTO);
        //LocalDateTime presentDateTime = LocalDateTime.now();
        assignmentSubmit.setSubDateTime(presentDateTime);
        assignmentSubmit.setCreationTime(savedAssignmentSubmit.getCreationTime());
        assignmentSubmit.setLastModTime(presentDateTime);
        assignmentSubmit.setSubmissionId(submissionId);
        /**
         * Grading options shouldn't be passed from front-end in create/update flow of submissions.
         * Hence grade values will be set to null
         */
        assignmentSubmit.setGrade(DEFAULT_GRADE);
        assignmentSubmit.setGradedBy(null);
        assignmentSubmit.setGradedDateTime(null);

        AssignmentSubmit updatedAssignmentSubmit = assignmentSubmitRepository.save(assignmentSubmit);
        AssignmentSubmitDTO updatedAssignmentSubmitDTO = assignmentSubmitMapper.toAssignmentSubmitDTO(updatedAssignmentSubmit);
        return updatedAssignmentSubmitDTO;
    }

    public void deleteSubmissions(Long submissionID){
        if(assignmentSubmitRepository.existsById(submissionID))
                assignmentSubmitRepository.deleteById(submissionID);
        else throw new ResourceNotFoundException("Submission", "ID",submissionID);
    }
    
    public List<AssignmentSubmitDTO> getGradesByAssinmentId(Long assignmentId){
        Assignment assSub = this.assignmentRepository.findById(assignmentId)
    				.orElseThrow(() -> new ResourceNotFoundException("Assignment", "Id", assignmentId));
        List<AssignmentSubmit> assSubListForGrades = assignmentSubmitRepository.getGradesByAssignmentId(assignmentId);
    	  List<AssignmentSubmitDTO> assSubmDtoListForGrades = assignmentSubmitMapper.toAssignmentSubmitDTOList(assSubListForGrades);
    	  return assSubmDtoListForGrades;
    }
    
    public List<AssignmentSubmitDTO> getGradesByStudentId(String studentId){
       	 if (!userRepository.existsById(studentId))
             throw new ResourceNotFoundException("Student","Student ID: ",studentId);
       	 
       	 List<AssignmentSubmit> assignmentSubmissionsListByStudentID = assignmentSubmitRepository.getGradesByStudentID(studentId);
         	 List<AssignmentSubmitDTO>  assignmentSubmissionListDTO = assignmentSubmitMapper.toAssignmentSubmitDTOList(assignmentSubmissionsListByStudentID);
      
        	 return assignmentSubmissionListDTO;
     }

    public AssignmentSubmitDTO gradeAssignmentSubmission(AssignmentSubmitDTO assignmentSubmitDTO, Long submissionId){
        AssignmentSubmit savedAssignmentSubmit = this.assignmentSubmitRepository.findById(submissionId)
                        .orElseThrow(() ->new ResourceNotFoundException("Submission", "ID", submissionId));

        int gradeValue = assignmentSubmitDTO.getGrade();
        if( gradeValue < 0 )
            throw new InvalidDataException("Valid Grade value is mandatory!");

        String gradedBy = assignmentSubmitDTO.getGradedBy();
        if(gradedBy==null || gradedBy.isEmpty())
            throw new InvalidDataException("Grader information mandatory!");
        else if(!userRepository.existsById(gradedBy))
            throw new ResourceNotFoundException("Grader", "ID", gradedBy);
        else if(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase(
                gradedBy,"R03","Active"))//.isEmpty())
            throw new InvalidDataException("User "+gradedBy+" is not allowed to grade the submission");
        //check for grading should not happen before due date.
        Optional<Assignment> assSub = this.assignmentRepository.findById(assignmentSubmitDTO.getAssignmentId());
        Date Assignment_Due_Date = assSub.get().getDueDate();
        Timestamp Submission_Date=assignmentSubmitDTO.getSubDateTime();
        if(Submission_Date.after(Assignment_Due_Date))
            throw new InvalidDataException("Cannot grade assignment before due date");





        /**
         * Front-end should not allow grading user to change the following info
         * assignment,user/student Id, submission desc or the submitted assignments
         */


        /**
         *  if grader provides comments, save them or else, keep the user comments.
         */
        String subComments = assignmentSubmitDTO.getSubComments();
        if(subComments!=null && !subComments.trim().isEmpty())
            savedAssignmentSubmit.setSubComments(subComments);

        savedAssignmentSubmit.setGrade(gradeValue);
        savedAssignmentSubmit.setGradedBy(gradedBy);

        Timestamp presentDateTime = Timestamp.valueOf(LocalDateTime.now());
        savedAssignmentSubmit.setLastModTime(presentDateTime);
        savedAssignmentSubmit.setGradedDateTime(presentDateTime);

        AssignmentSubmit updatedAssignmentSubmit = assignmentSubmitRepository.save(savedAssignmentSubmit);

        AssignmentGradedEvent assignmentGradedEvent = new AssignmentGradedEvent(savedAssignmentSubmit);
        eventPublisher.publishEvent(assignmentGradedEvent);
        notificationService.handleAssignmentGradeEvent(assignmentGradedEvent);

        AssignmentSubmitDTO gradedSubmissionDTO = assignmentSubmitMapper.toAssignmentSubmitDTO(updatedAssignmentSubmit);
        return gradedSubmissionDTO;
    }


    public List<AssignmentSubmitDTO> getGradesByBatchId(Integer batchId) {
        List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findByAssignment_Batch_BatchId(batchId);
        if (assignmentSubmits.isEmpty()) {
            throw new ResourceNotFoundException("Assignments with grades does not exist for Batch ID : "+batchId);
        }
        List<AssignmentSubmitDTO> assignmentSubmitDTOs = assignmentSubmitMapper.toAssignmentSubmitDTOList(assignmentSubmits);
        return assignmentSubmitDTOs;
    }

    public List<AssignmentSubmitDTO> getSubmissionsByAssignment(Long assignmentId) {
    	
   	 Assignment savedAssignment = this.assignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment", "Id", assignmentId));

   	 List<AssignmentSubmit> assignmentSubmitList= assignmentSubmitRepository.findByAssignment_assignmentId(savedAssignment.getAssignmentId());
   	 List<AssignmentSubmitDTO> assignmentSubmitDTOs = new ArrayList<>();

        for (AssignmentSubmit assignmentSubmit : assignmentSubmitList) {
            AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
            assignmentSubmitDTO.setSubmissionId(assignmentSubmit.getSubmissionId());
            assignmentSubmitDTO.setAssignmentId(assignmentSubmit.getAssignment().getAssignmentId());
            assignmentSubmitDTO.setUserId(assignmentSubmit.getUser().getUserId());
            assignmentSubmitDTO.setGrade(assignmentSubmit.getGrade());
            assignmentSubmitDTO.setGradedDateTime(assignmentSubmit.getGradedDateTime());
            assignmentSubmitDTO.setSubComments(assignmentSubmit.getSubComments());
            assignmentSubmitDTO.setSubDesc(assignmentSubmit.getSubDesc());
            assignmentSubmitDTO.setSubDateTime(assignmentSubmit.getSubDateTime());
            assignmentSubmitDTO.setGradedBy(assignmentSubmit.getGradedBy());

           assignmentSubmitDTOs.add(assignmentSubmitDTO);
        }

        return assignmentSubmitDTOs;

   }
    
    public List<AssignmentSubmitDTO> getGradesByProgramId(Long programId)
    {
    	List <AssignmentSubmit> subAssignment = assignmentSubmitRepository.getGradesByProgramId(programId);
    		
    	if(!(subAssignment.size()<=0))
        {
        	List <AssignmentSubmitDTO> listOfAssignmentSub = assignmentSubmitMapper.toAssignmentSubmitDTOList(subAssignment);
        	return listOfAssignmentSub;
        }
        else
        {
        	throw new ResourceNotFoundException("Submissions for Program Id: "+programId+ " Not found");
        }
    }


    public double getGradesMeanByBatchId(Integer batchId) {
    	
		List<Integer> gradeList = new ArrayList<>();			
		
		List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findByAssignment_Batch_BatchId(batchId);
        if (assignmentSubmits.isEmpty()) {
            throw new ResourceNotFoundException("Assignments with grades does not exist for Batch ID : "+batchId);
        }
        try {
			for (AssignmentSubmit asgnmnt :  assignmentSubmits) {			
				gradeList.add(asgnmnt.getGrade());
			}	
			if (gradeList.size()== 1) 
				return gradeList.get(0);
			else
				return gradeList.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
		}catch(Exception e){
			throw new ResourceNotFoundException("can't find mean of batch: "+batchId);
		}
    }

	public double getGradesMedianByBatchId(Integer batchId) {
		List<Integer> gradeList = new ArrayList<>();
		
		List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findByAssignment_Batch_BatchId(batchId);
       if (assignmentSubmits.isEmpty()) {
           throw new ResourceNotFoundException("Assignments with grades does not exist for Batch ID : "+batchId);
       }	
       try {
    	   for (AssignmentSubmit asgnmnt :  assignmentSubmits) {			
   				gradeList.add(asgnmnt.getGrade());
   		   }
    	   if (gradeList.size()== 1) 
				return gradeList.get(0);
		   else{			
				Collections.sort(gradeList);
				int length = gradeList.size();
				int middle = length/2;
				if (length%2 == 1) { 
			        return gradeList.get(middle);
			    } else {
			        return (gradeList.get(middle-1) + gradeList.get(middle)) / 2.0;
			    }
		   }
       }catch (Exception e) {
    	   throw new ResourceNotFoundException("can't find median of batch: "+batchId);
       }
	}


    public double getGradeMeanByClassId(Long csId) {

        List<AssignmentSubmit> assignmentSubmits = assignmentSubmitRepository.findByAssignment_Aclass_CsId(csId);
        if(assignmentSubmits.isEmpty()){
            throw new ResourceNotFoundException("Assignments with grades does not exist for class Id: "+ csId);
        }

        List<Integer> classGradesList = assignmentSubmits.stream()
                .map(AssignmentSubmit::getGrade)
                .collect(Collectors.toList());

        return classGradesList.size() == 1 ? classGradesList.get(0) : classGradesList.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
    }
}