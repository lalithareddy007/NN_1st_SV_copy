package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.entity.User;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AssignmentRepositoryTest {

	@Autowired
	private AssignmentRepository repo;
	
	private Assignment mockAssignment;
	
	@BeforeEach
	public void setup() {
		mockAssignment = setMockAssignment();
	}

	@DisplayName("test for getting assignments by name")
	@Test
	public void testFindByAssignmentName() {
		
		//given
		repo.save(mockAssignment);
		
		//when
		Optional<Assignment> assignment = repo.findByAssignmentName(mockAssignment.getAssignmentName());
		
		//then
		assertThat(assignment).isNotNull();
		
	}
	
	@DisplayName("test for getting assignments by batch")
	@Test
	public void testFindByBatch() {
		
		//given
		repo.save(mockAssignment);
		
		//when
		List<Assignment> assignments = repo.findByBatch(mockAssignment.getBatch());
		
		//then
		assertThat(assignments).isNotNull();
		assertThat(assignments.size()).isGreaterThan(0);
		
	}
	
	private Assignment setMockAssignment() {
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

		Program program = new Program(7L,"Django","new Prog",
				"Active", timestamp, timestamp);

		Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program,
                5, timestamp, timestamp);

		User user = new User("U01", "Steve", "Jobs", "",
				1234567890L, "CA", "PST", "@stevejobs", "",
				"", "", "Citizen", timestamp, timestamp);

        User user1 = new User("U02", "Elon", "Musk", "",
				1234567809L, "CA", "PST", "@elonmusk", "",
				"", "", "Citizen", timestamp, timestamp);

		Assignment assignment = new Assignment(1L, "Test Assignment",
				"Junit test", "practice", dueDate, "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", batch, user, user1, timestamp, timestamp);

		return assignment;
	}

}
