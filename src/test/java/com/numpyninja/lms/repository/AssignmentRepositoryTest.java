package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.User;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest

public class AssignmentRepositoryTest {

	@MockBean
	private AssignmentRepository mockAssignRepository;

	@Mock
	TestEntityManager entManger;



	@BeforeEach
	public void setUp() {

	}


	@DisplayName("test for getting assignments by name")
	@Test
	public void testFindByAssignmentName() {

		String assignmentName="Selenium Assignment1";

		//expected Assignment Array
		Assignment expectedAssignment = createAssignment();

		entManger.persistAndFlush(expectedAssignment);

		when(mockAssignRepository.findByAssignmentName(assignmentName)).thenReturn(Optional.of(expectedAssignment));

		// Act
		Optional<Assignment> foundAssignmnet = mockAssignRepository.findByAssignmentName(assignmentName);
		// Optional<Assignment> foundAssignmnet = assignRepo.findByAssignmentName(assignmentName1);

		//then
		System.out.println(foundAssignmnet.isPresent()?"true":"false");
		assertThat(foundAssignmnet).isNotNull();



	}

	@DisplayName("test for getting assignments by batch")
	@Test
	public void testFindByBatch() {

		//Create batch
		Batch batch = new Batch();
		batch.setBatchId(10);
		batch.setBatchName("SDET");

		//expected Assignment Array
		List<Assignment> expectedAssignment = createAssignmentwithBatch();

		entManger.persistAndFlush(expectedAssignment);

		when(mockAssignRepository.findByBatch(batch)).thenReturn(expectedAssignment);

		// Act
		List<Assignment>  foundAssignmnet = mockAssignRepository.findByBatch(batch);
		// Optional<Assignment> foundAssignmnet = assignRepo.findByAssignmentName(assignmentName1);

		//then
		//System.out.println(foundAssignmnet.isPresent()?"true":"false");
		assertThat(foundAssignmnet).isNotNull();


		//then
		assertThat(foundAssignmnet).isNotNull();
		assertThat(foundAssignmnet.size()).isGreaterThan(0);

	}


	private Assignment createAssignment(){

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
		Assignment mockAssignment =new Assignment();
		mockAssignment.setUser(new User());
		mockAssignment.setUser1(new User());
		mockAssignment.setAssignmentDescription("Selenium Assignment1");
		mockAssignment.setAssignmentId(1L);
		mockAssignment.setAssignmentName("Test Assignment");
		mockAssignment.setBatch(new Batch());
		mockAssignment.setPathAttachment1("Path 1");
		mockAssignment.setPathAttachment2("Path 2");
		mockAssignment.setPathAttachment3("Path 3");
		mockAssignment.setPathAttachment4("Path 4");
		mockAssignment.setPathAttachment5("Path 5");
		mockAssignment.setCreationTime(timestamp);
		mockAssignment.setDueDate(dueDate);
		return mockAssignment;
	}

	private  List<Assignment> createAssignmentwithBatch(){

		List listOfAssignment= new ArrayList<Assignment>();

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
		Assignment mockAssignment =new Assignment();
		mockAssignment.setUser(new User());
		mockAssignment.setUser1(new User());
		mockAssignment.setAssignmentDescription("Selenium Assignment1");
		mockAssignment.setAssignmentId(1L);
		mockAssignment.setAssignmentName("Test Assignment");
		Batch batch = new Batch();
		batch.setBatchId(10);
		batch.setBatchName("SDET");
		batch.setBatchNoOfClasses(5);
		mockAssignment.setBatch(batch);
		mockAssignment.setPathAttachment1("Path 1");
		mockAssignment.setPathAttachment2("Path 2");
		mockAssignment.setPathAttachment3("Path 3");
		mockAssignment.setPathAttachment4("Path 4");
		mockAssignment.setPathAttachment5("Path 5");
		mockAssignment.setCreationTime(timestamp);
		mockAssignment.setDueDate(dueDate);
		listOfAssignment.add(mockAssignment);
		return listOfAssignment;

	}




}
