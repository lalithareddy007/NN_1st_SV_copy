package com.numpyninja.lms.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.repository.*;
import com.numpyninja.lms.util.AssignmentCreatedUpdatedEvent;
import com.numpyninja.lms.util.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentMapper;
import org.springframework.util.StringUtils;

@Service
public class AssignmentService {
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private AssignmentRepository assignmentRepository;

	@Autowired
	private ProgBatchRepository batchRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleMapRepository userRoleMapRepository;

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private AssignmentMapper assignmentMapper;

	@Autowired
	NotificationService notificationService;

	//create an assignment 
	public AssignmentDto createAssignment(AssignmentDto assignmentDto) {
		//Check whether assignment is already present or not
		Optional<Assignment> savedAssignment = assignmentRepository
				.findByAssignmentName(assignmentDto.getAssignmentName());
		if(savedAssignment.isPresent())
			throw new DuplicateResourceFoundException("Assignment", "Name", assignmentDto.getAssignmentName());

		//Check assignment is created by Admin or Staff user
		if (userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase
						(assignmentDto.getCreatedBy(), "R03", "Active")
				.isEmpty()) //No Active Admin or Staff role available for user
			throw new ResourceNotFoundException("User", "Role(Admin/Staff)", assignmentDto.getCreatedBy());

		// Check whether graderId exists as User(Admin/Staff/User)
		if (!userRepository.existsById(assignmentDto.getGraderId()))
			throw new ResourceNotFoundException("User", "ID", assignmentDto.getGraderId());

		Long classId = assignmentDto.getCsId();
		Class aClass= classRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("Class", "Id", classId));

		Assignment assignment = assignmentMapper.toAssignment(assignmentDto);
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		assignment.setCreationTime(timestamp);
		assignment.setLastModTime(timestamp);
		assignment.setAclass(aClass);
		Assignment newAssignment = this.assignmentRepository.save(assignment);

		//When a new assignment is created, an AssignmentCreatedEvent is triggered and published using Spring's ApplicationEventPublisher
		AssignmentCreatedUpdatedEvent assignmentCreatedEvent = new AssignmentCreatedUpdatedEvent(newAssignment);
		eventPublisher.publishEvent(assignmentCreatedEvent);
		notificationService.handleAssignmentCreatedUpdatedEvent(assignmentCreatedEvent);

		return assignmentMapper.toAssignmentDto(newAssignment);
	}


	//update an assignment
	public AssignmentDto updateAssignment(AssignmentDto assignmentDto, Long assignmentId) {
		Assignment savedAssignment = this.assignmentRepository.findById(assignmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment", "Id", assignmentId));

		//Check assignment is edited by Admin or Staff user
		if (userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase
						(assignmentDto.getCreatedBy(), "R03", "Active")
				.isEmpty()) //No Active Admin or Staff role available for user
			throw new ResourceNotFoundException("User", "Role(Admin/Staff)", assignmentDto.getCreatedBy());

		// Check whether graderId exists as User(Admin/Staff/User)
		if (!userRepository.existsById(assignmentDto.getGraderId()))
			throw new ResourceNotFoundException("User", "ID", assignmentDto.getGraderId());

		Assignment updateAssignment = assignmentMapper.toAssignment(assignmentDto);
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);

		if(StringUtils.hasLength(assignmentDto.getComments()))
			updateAssignment.setComments(assignmentDto.getComments());
		else
			updateAssignment.setComments(savedAssignment.getComments());

		if(StringUtils.hasLength(assignmentDto.getPathAttachment1()))
			updateAssignment.setPathAttachment1(assignmentDto.getPathAttachment1());
		else
			updateAssignment.setPathAttachment1(savedAssignment.getPathAttachment1());

		if(StringUtils.hasLength(assignmentDto.getPathAttachment2()))
			updateAssignment.setPathAttachment2(assignmentDto.getPathAttachment2());
		else
			updateAssignment.setPathAttachment2(savedAssignment.getPathAttachment2());

		if(StringUtils.hasLength(assignmentDto.getPathAttachment3()))
			updateAssignment.setPathAttachment3(assignmentDto.getPathAttachment3());
		else
			updateAssignment.setPathAttachment3(savedAssignment.getPathAttachment3());

		if(StringUtils.hasLength(assignmentDto.getPathAttachment4()))
			updateAssignment.setPathAttachment4(assignmentDto.getPathAttachment4());
		else
			updateAssignment.setPathAttachment4(savedAssignment.getPathAttachment4());

		if(StringUtils.hasLength(assignmentDto.getPathAttachment5()))
			updateAssignment.setPathAttachment5(assignmentDto.getPathAttachment5());
		else
			updateAssignment.setPathAttachment5(savedAssignment.getPathAttachment5());

		updateAssignment.setAssignmentId(assignmentId);
		updateAssignment.setCreationTime(savedAssignment.getCreationTime());
		updateAssignment.setLastModTime(timestamp);

		Assignment updatedAssignment = this.assignmentRepository.save(updateAssignment);
		AssignmentDto updatedAssignmentDto = assignmentMapper.toAssignmentDto(updatedAssignment);

		//Notify users in batch
		AssignmentCreatedUpdatedEvent assignmentUpdatedEvent = new AssignmentCreatedUpdatedEvent(updatedAssignment);
		eventPublisher.publishEvent(assignmentUpdatedEvent);
		notificationService.handleAssignmentCreatedUpdatedEvent(assignmentUpdatedEvent);

		return updatedAssignmentDto;
	}

	//delete an assignment
	public void deleteAssignment(Long id) {
		Assignment assignment = this.assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment", "Id", id));
		this.assignmentRepository.deleteById(id);
	}

	// get all assignments
	public List<AssignmentDto> getAllAssignments() {
		List<Assignment> assignments =  this.assignmentRepository.findAll();
		//List<AssignmentDto> assignmentDtos = assignments.stream()
		//		.map(assignment -> assignmentMapper.toAssignmentDto(assignment)).collect(Collectors.toList());
		List<AssignmentDto> assignmentDtos = assignmentMapper.toAssignmentDtoList(assignments);
		return assignmentDtos;
	}

	//get assignment by id
	public AssignmentDto getAssignmentById(Long id) {
		Assignment assignment = this.assignmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment", "Id", id));
		return assignmentMapper.toAssignmentDto(assignment);
	}

	//get assignments for a batch
	public List<AssignmentDto> getAssignmentsForBatch(Integer batchId) {
		Batch batch = this.batchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "Id", batchId));
		List<Assignment> assignments = this.assignmentRepository.findByBatch(batch);
		if (assignments.isEmpty())
			throw new ResourceNotFoundException("Assignments", "BatchId", batchId);
		return assignmentMapper.toAssignmentDtoList(assignments);
	}
}
