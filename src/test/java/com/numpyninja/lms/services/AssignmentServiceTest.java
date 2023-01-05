package com.numpyninja.lms.services;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.BDDMockito.willDoNothing;

import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentMapper;
import com.numpyninja.lms.repository.AssignmentRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

	@Mock
	private AssignmentRepository assignmentRepository;

	@Mock
	private ProgBatchRepository batchRepository;

	@Mock
	private UserRoleMapRepository userRoleMapRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private AssignmentService assignmentService;

	@Mock
	private AssignmentMapper assignmentMapper;

	private Assignment mockAssignment;

	private AssignmentDto mockAssignmentDto;

	private List<UserRoleMap> mockUserRoleMaps;

	@BeforeEach
	public void setup() {
		mockAssignment = setMockAssignmentAndDto();
	}

	private Assignment setMockAssignmentAndDto() {
		String sDate = "05/25/2022";
		Date dueDate = null;
		try {
			dueDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);

		User user = new User("U01", "Steve", "Jobs", "Martin",
				1234567890L, "CA", "PST", "@stevejobs", "",
				"", "", "Citizen", timestamp, timestamp);

		User user1 = new User("U02", "Elon", "Musk", "Steve",
				1234567809L, "CA", "PST", "@elonmusk", "",
				"", "", "Citizen", timestamp, timestamp);

		Batch batch = setMockBatch();

		mockAssignment = new Assignment(1L, "Test Assignment", "Junit test",
				"practice", dueDate, "Filepath1", "Filepath2",
				"Filepath3", "Filepath4", "Filepath5", batch, user, user1,
				timestamp, timestamp);

		mockAssignmentDto = new AssignmentDto(1L, "Test Assignment",
				"Junit test", "practice", dueDate, "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", 1, "U01", "U02");

		mockUserRoleMaps = new ArrayList<UserRoleMap>();

		Role role = new Role("R01", "Admin", "LMS_Admin", timestamp, timestamp);

		UserRoleMap userRoleMap = new UserRoleMap();
		userRoleMap.setUserRoleId(1L);
		userRoleMap.setUserRoleStatus("Active");
		userRoleMap.setUser(user);
		userRoleMap.setRole(role);
		userRoleMap.setCreationTime(timestamp);
		userRoleMap.setLastModTime(timestamp);
		mockUserRoleMaps.add(userRoleMap);

		return mockAssignment;
	}

	private Batch setMockBatch() {
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);

		Program program = new Program(7L,"Django","new Prog",
				"Active", timestamp, timestamp);

		Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program,
				5, timestamp, timestamp);

		return batch;
	}

	@DisplayName("test for creating a new assignment")
	@Test
	void testCreateAssignment() {

		//given
		given(assignmentRepository.findByAssignmentName(mockAssignment.getAssignmentName()))
				.willReturn(Optional.empty());
		given(userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase(
				mockAssignmentDto.getCreatedBy(), "R03", "Active"))
				.willReturn(mockUserRoleMaps);
		given(userRepository.existsById(mockAssignmentDto.getGraderId())).willReturn(true);
		given(assignmentMapper.toAssignment(mockAssignmentDto)).willReturn(mockAssignment);
		given(assignmentRepository.save(mockAssignment)).willReturn(mockAssignment);
		given(assignmentMapper.toAssignmentDto(mockAssignment)).willReturn(mockAssignmentDto);

		//when
		AssignmentDto assignmentDto = assignmentService.createAssignment(mockAssignmentDto);

		//then
		assertThat(assignmentDto).isNotNull();

	}

	@DisplayName("test for creating a new assignment with duplicate name")
	@Test
	void testCreateAssignmentWithDuplicateName() {
		//given
		given(assignmentRepository.findByAssignmentName(mockAssignment.getAssignmentName()))
				.willReturn(Optional.of(mockAssignment));

		//when
		Assertions.assertThrows(DuplicateResourceFoundException.class,
				() -> assignmentService.createAssignment(mockAssignmentDto));

		//then
		Mockito.verify(assignmentMapper, never()).toAssignment(any(AssignmentDto.class));
		Mockito.verify(assignmentRepository, never()).save(any(Assignment.class));
		Mockito.verify(assignmentMapper, never()).toAssignmentDto(any(Assignment.class));

	}

	@DisplayName("test for updating an assignment")
	@Test
	void testUpdateAssignment() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.of(mockAssignment));
		mockAssignmentDto.setAssignmentDescription("New Description");

		given(userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase(
				mockAssignmentDto.getCreatedBy(), "R03", "Active"))
				.willReturn(mockUserRoleMaps);
		given(userRepository.existsById(mockAssignmentDto.getGraderId())).willReturn(true);

		given(assignmentMapper.toAssignment(mockAssignmentDto)).willReturn(mockAssignment);
		given(assignmentRepository.save(mockAssignment)).willReturn(mockAssignment);
		given(assignmentMapper.toAssignmentDto(mockAssignment)).willReturn(mockAssignmentDto);

		//when
		AssignmentDto assignmentDto = assignmentService.updateAssignment(mockAssignmentDto, mockAssignmentDto.getAssignmentId());

		//then
		assertThat(assignmentDto).isNotNull();
		assertThat(assignmentDto.getAssignmentDescription()).isEqualTo("New Description");
	}

	@DisplayName("test for updating an assignment whose Id is not found")
	@Test
	void testUpdateAssignmentWhoseIdIsNotFound() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.empty());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> assignmentService.updateAssignment(mockAssignmentDto, mockAssignmentDto.getAssignmentId()));

		//then
		Mockito.verify(assignmentMapper, never()).toAssignment(any(AssignmentDto.class));
		Mockito.verify(assignmentRepository, never()).save(any(Assignment.class));
		Mockito.verify(assignmentMapper, never()).toAssignmentDto(any(Assignment.class));
	}

	@DisplayName("test for deleting an assignment by id")
	@Test
	void testDeleteAssignment() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.of(mockAssignment));
		willDoNothing().given(assignmentRepository).deleteById(mockAssignment.getAssignmentId());

		//when
		assignmentService.deleteAssignment(mockAssignment.getAssignmentId());

		//then
		verify(assignmentRepository, times(1)).deleteById(mockAssignment.getAssignmentId());
	}

	@DisplayName("test for deleting an assignment whose id is not found")
	@Test
	void testDeleteAssignmentIdNotFound() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.empty());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> assignmentService.deleteAssignment(mockAssignment.getAssignmentId()));

		//then
		Mockito.verify(assignmentRepository, never()).deleteById(any(Long.class));
	}

	@DisplayName("test for getting all the available assignments")
	@Test
	void testGetAllAssignments() {
		//given
		Assignment mockAssignment2 = setMockAssignmentAndDto();
		mockAssignment2.setAssignmentId(2L);
		mockAssignment2.setAssignmentName("Test Assignment 2");
		AssignmentDto mockAssignmentDto2 = mockAssignmentDto;
		mockAssignmentDto2.setAssignmentId(2L);
		mockAssignmentDto2.setAssignmentName("Test Assignment 2");
		List<Assignment> assignmentList = new ArrayList<Assignment>();
		assignmentList.add(mockAssignment);
		assignmentList.add(mockAssignment2);
		List<AssignmentDto> assignmentDtoList = new ArrayList<AssignmentDto>();
		assignmentDtoList.add(mockAssignmentDto);
		assignmentDtoList.add(mockAssignmentDto2);
		given(assignmentRepository.findAll()).willReturn(assignmentList);
		//given(assignmentRepository.findByBatch(batch)).willReturn(assignmentList);
		given(assignmentMapper.toAssignmentDtoList(assignmentList)).willReturn(assignmentDtoList);
		//given(assignmentMapper.toAssignmentDto(mockAssignment2)).willReturn(mockAssignmentDto2);

		//when
		List<AssignmentDto> assignmentDtos = assignmentService.getAllAssignments();

		//then
		assertThat(assignmentDtos).isNotNull();
		assertThat(assignmentDtos.size()).isEqualTo(2);
	}

	@DisplayName("test for getting all assignments when no assignments are available")
	@Test
	void testGetAllAssignmentsWhenListIsEmpty() {
		//given
		given(assignmentRepository.findAll()).willReturn(Collections.emptyList());

		//when
		List<AssignmentDto> assignmentDtos = assignmentService.getAllAssignments();

		//then
		assertThat(assignmentDtos).isEmpty();
		assertThat(assignmentDtos.size()).isEqualTo(0);
	}

	@DisplayName("test for getting an assignment by Id")
	@Test
	void testGetAssignmentById() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.of(mockAssignment));
		given(assignmentMapper.toAssignmentDto(mockAssignment)).willReturn(mockAssignmentDto);

		//when
		AssignmentDto assignmentDto = assignmentService.getAssignmentById(mockAssignment.getAssignmentId());

		//then
		assertThat(assignmentDto).isNotNull();
	}

	@DisplayName("test for getting an assignment by an Id which is not available")
	@Test
	void testGetAssignmentByIdNotFound() {
		//given
		given(assignmentRepository.findById(mockAssignment.getAssignmentId())).willReturn(Optional.empty());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> assignmentService.getAssignmentById(mockAssignment.getAssignmentId()));

		//then
		Mockito.verify(assignmentMapper, never()).toAssignmentDto(any(Assignment.class));
	}

	@DisplayName("test for getting assignments for a batch")
	@Test
	void testGetAssignmentsForBatch() {
		//given
		Batch batch = setMockBatch();
		given(batchRepository.findById(batch.getBatchId())).willReturn(Optional.of(batch));
		Assignment mockAssignment2 = mockAssignment;
		mockAssignment2.setAssignmentId(2L);
		mockAssignment2.setAssignmentName("Test Assignment 2");
		AssignmentDto mockAssignmentDto2 = mockAssignmentDto;
		mockAssignmentDto2.setAssignmentId(2L);
		mockAssignmentDto2.setAssignmentName("Test Assignment 2");
		List<Assignment> assignmentList = new ArrayList<Assignment>();
		assignmentList.add(mockAssignment);
		assignmentList.add(mockAssignment2);
		List<AssignmentDto> assignmentDtoList = new ArrayList<AssignmentDto>();
		assignmentDtoList.add(mockAssignmentDto);
		assignmentDtoList.add(mockAssignmentDto2);
		given(assignmentRepository.findByBatch(batch)).willReturn(assignmentList);
		given(assignmentMapper.toAssignmentDtoList(assignmentList)).willReturn(assignmentDtoList);

		//when
		List<AssignmentDto> assignmentDtos = assignmentService.getAssignmentsForBatch(batch.getBatchId());

		//then
		assertThat(assignmentDtos).isNotNull();
		assertThat(assignmentDtos.size()).isGreaterThan(0);
	}

	@DisplayName("test for getting assignments for a batch id that invalid")
	@Test
	void testGetAssignmentsForInvalidBatchId() {
		//given
		Batch batch = setMockBatch();
		given(batchRepository.findById(batch.getBatchId())).willReturn(Optional.empty());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> assignmentService.getAssignmentsForBatch(batch.getBatchId()));

		//then
		Mockito.verify(assignmentRepository, never()).findByBatch(any(Batch.class));
		Mockito.verify(assignmentMapper, never()).toAssignmentDtoList(any(List.class));
	}

	@DisplayName("test for getting assignments for a batch id that is not mapped ")
	@Test
	void testGetAssignmentsForBatchNotFound() {
		//given
		Batch batch = setMockBatch();
		given(batchRepository.findById(batch.getBatchId())).willReturn(Optional.of(batch));
		given(assignmentRepository.findByBatch(batch)).willReturn(Collections.emptyList());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> assignmentService.getAssignmentsForBatch(batch.getBatchId()));

		//then
		Mockito.verify(assignmentMapper, never()).toAssignmentDtoList(any(List.class));
	}



}
