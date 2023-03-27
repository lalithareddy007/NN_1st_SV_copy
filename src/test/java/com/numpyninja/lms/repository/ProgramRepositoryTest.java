package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Program;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class ProgramRepositoryTest {
	
	@Autowired
	ProgramRepository programRepository;

	Program mockProgram;

	@BeforeEach
	public void setUp() {
		setMockProgramAndSave();
	}

	private void setMockProgramAndSave() {
		mockProgram = new Program(1L,"SDET"," ", "Active",
				Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
		programRepository.save(mockProgram);
	}

	@DisplayName("JUnit test for get Programs by ProgramName ") 
	@Test
	void givenProgramName_WhenFindPrograms_ReturnProgramObjects() {
		//given
		String programName = "SDET";

		//when
		List<Program> programList= programRepository.findByProgramName(programName);
		
		//then
		assertThat(programList).isNotEmpty();
	}

	@DisplayName("JUnit test for get Programs by ProgramName ignoring case ")
	@Test
	void givenProgramName_WhenFindPrograms_ReturnProgramsList() {
		//given
		String programName = "sdet";

		//when
		List<Program> programList= programRepository.findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(programName);

		//then
		assertThat(programList).isNotEmpty();
	}

	@DisplayName("test to get program by Id and Status")
	@Test
	public void testFindProgramByProgramIdAndAndProgramStatusEqualsIgnoreCase() {
		Optional<Program> optionalProgram = programRepository
				.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(1L, "active");

		assertThat(optionalProgram).isNotEmpty();
	}
}


