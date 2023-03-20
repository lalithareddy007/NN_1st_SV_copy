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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProgramRepositoryTest {
	
	@Autowired
	ProgramRepository programRepository;
    
	@DisplayName("JUnit test for get Programs by ProgramName ") 
	@Test
	void givenProgramName_WhenFindPrograms_ReturnProgramObjects() {
	//given
		String programName = "SDET";
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Program program = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		programRepository.save(program);
		
	//when
		List<Program> programList= programRepository.findByProgramName(programName);
		
	//then
		
		assertThat(programList).isNotNull();
		assertThat(programList.size()).isGreaterThan(0);
	}

	
	
	@DisplayName("JUnit test to create Program")
	@Test
	@Order(1)
	public void givenProgramObject_WhenSave_ThenReturnSavedProgram() {
		// given
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Program program1 = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		programRepository.save(program1);
		
		assertThat(program1).isNotNull();
		assertThat(program1.getProgramId()).isEqualTo(1);
		
		Program program2 = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		programRepository.save(program2);
	//then	
		assertThat(program1).isNotNull();
		assertThat(program1.getProgramId()).isEqualTo(1);
		
		
	}

	@DisplayName("test to get program by Id and Status")
	@Test
	public void testFindProgramByProgramIdAndAndProgramStatusEqualsIgnoreCase() {
		Program program = new Program(1L, "SDET", "SDET Training",
				"Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
		programRepository.save(program);

		Optional<Program> optionalProgram = programRepository
				.findProgramByProgramIdAndAndProgramStatusEqualsIgnoreCase(1L, "active");

		assertThat(optionalProgram).isNotEmpty();
	}
}


