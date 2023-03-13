package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Program;


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
		String programName = "Automation";
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

	
	
	
}


