package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.numpyninja.lms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class AssignmentSubmitRepositoryTest {
@Autowired
	private AssignmentSubmitRepository assignmentSubmitRepo;

	private AssignmentSubmit mockAssignmentSubmit;

	@BeforeEach
	public void setUp()
	{
		this.mockAssignmentSubmit = setMockassignmentSubmit();

	}

	private AssignmentSubmit setMockassignmentSubmit() {
		String sDate = "05/25/2022";
		Date dueDate = null;
		try {
			dueDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);

		Program program = new Program(7L, "Django", "new Prog",
				"Active", timestamp, timestamp);

		Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program,
				5, timestamp, timestamp);

		User user = new User("U01", "Steve", "Jobs", "",
				1234567890L, "CA", "PST", "@stevejobs", "",
				"", "", "Citizen", timestamp, timestamp);

		User user1 = new User("U02", "Elon", "Musk", "",
				1234567809L, "CA", "PST", "@elonmusk", "",
				"", "", "Citizen", timestamp, timestamp);

		Assignment assignment = new Assignment(20L, "Test Assignment",
				"Junit test", "practice", dueDate, "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", batch, user, user1, timestamp, timestamp);

		Assignment assignment1 = new Assignment(20L, "Test Assignment",
				"Junit test", "practice", dueDate, "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", batch, user1, user1, timestamp, timestamp);


		AssignmentSubmit assignmentSubmit = new AssignmentSubmit(1L, assignment, user,
				"Assignement Submissions", "Assignment Submit for test", "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", Timestamp.valueOf(LocalDateTime.now()), "U01", Timestamp.valueOf(LocalDateTime.now()), 250, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
		AssignmentSubmit assignmentSubmit1 = new AssignmentSubmit(1L, assignment1, user,
				"Assignement Submissions", "Assignment Submit for test", "Filepath1",
				"Filepath2", "Filepath3", "Filepath4",
				"Filepath5", Timestamp.valueOf(LocalDateTime.now()), "U01", Timestamp.valueOf(LocalDateTime.now()), 250, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));


		return assignmentSubmit;
	}

	@DisplayName("Test for find User by UserID")
	@Test
	public void testFindByUser_userId() {

		//given
		assignmentSubmitRepo.save(mockAssignmentSubmit);

		//when
		List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepo.findByUser_userId(mockAssignmentSubmit.getUser().getUserId());

		//then
		assertThat(assignmentSubmitList).isNotNull();
		assertThat(assignmentSubmitList.size()).isGreaterThan(0);
	}

	@DisplayName("Test for find Assignment submission by Student ID and AssignmentID")
	@Test
	public void testFindByStudentIdAndAssignmentId() {

		//given
		assignmentSubmitRepo.save(mockAssignmentSubmit);

		//when
		Optional<List<AssignmentSubmit>> assignmentSubmitList = assignmentSubmitRepo.findByStudentIdAndAssignmentId(mockAssignmentSubmit.getUser().getUserId(), mockAssignmentSubmit.getAssignment().getAssignmentId());

		//then
		assertThat(assignmentSubmitList).isNotNull();
		assertThat(assignmentSubmitList.stream()).hasSizeGreaterThan(0);

	}

	@Test
	public void testGetGradesByAssignmentId() {
		//given
		assignmentSubmitRepo.save(mockAssignmentSubmit);

		//when
		List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepo.getGradesByAssignmentId(mockAssignmentSubmit.getAssignment().getAssignmentId());

		//then
		assertThat(assignmentSubmitList).isNotNull();
		assertThat(assignmentSubmitList.size()).isGreaterThan(0);

	}

	@DisplayName("Test for get Grades by Student ID")
	@Test
	public void testGetGradesByStudentID() {
		//given
		assignmentSubmitRepo.save(mockAssignmentSubmit);

		//when
		List<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepo.getGradesByStudentID(mockAssignmentSubmit.getUser().getUserId());

		//then
		assertThat(assignmentSubmitList).isNotNull();
		assertThat(assignmentSubmitList.size()).isGreaterThan(0);
	}

	@Test
	@DisplayName("Test for reSubmit Assignment")
	public void testResubmitAssignment() {
		Long submissionId = assignmentSubmitRepo.findAll().get(0).getSubmissionId();

		assignmentSubmitRepo.save(mockAssignmentSubmit);


		Optional<AssignmentSubmit> assignmentSubmitList = assignmentSubmitRepo.findById(submissionId);

		assertThat(assignmentSubmitList).isNotNull();
		assertThat(assignmentSubmitList.stream()).hasSizeGreaterThan(0);
	}


}

