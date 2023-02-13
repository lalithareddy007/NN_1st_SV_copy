package com.numpyninja.lmstests.servicetests;

import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ProgramMapper;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.services.ProgramServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTests {
	@Mock
	private ProgramRepository progRepo; //= mock(programRepository.class);
	
	@Mock
	private ProgramMapper progMap; //=mock(programMapper.INSTANCE.getClass());
	
	@InjectMocks
	
	ProgramServices programService;
	
	/*@BeforeEach
	public void init()
	{
		//programService = new programServiceImpl();
	}*/
	
	@DisplayName("mockito testcase for getProgramsById")
	@Test
	void testGetProgramById() throws ResourceNotFoundException {
		//Given
		Long programId = Long.valueOf(5);
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		ProgramDTO newProgDTO= new ProgramDTO(programId,"DesignPatterns",null,"nonActive",timestamp,timestamp);
		//programDTO newProgDTO = new programDTO();
		
		Program newProg= new Program(programId,"DesignPatterns",null, "nonActive",timestamp, timestamp);
		//programEntity newProg = new programEntity();
				
		//doReturn(Optional.of(newProg)).when(progRepo).findById(programId);
		Mockito.when(progRepo.existsById(programId)).thenReturn(true);
		
		Mockito.when(progRepo.findById(programId)).thenReturn(Optional.of(newProg));
		
		Mockito.when(progMap.toProgramDTO(newProg)).thenReturn(newProgDTO);
		//doReturn(Optional.of(newProgDTO)).when(progMap).toProgramDTO(newProg);
		
		//when or Action
		ProgramDTO resultFromService = programService.getProgramsById(programId);
		
		
		//then or Assert 
		assertThat(resultFromService).isNotNull();
		assertThat(resultFromService.getProgramName()).isEqualTo(newProgDTO.getProgramName());
		assertThat(resultFromService.getProgramDescription()).isEqualTo(newProgDTO.getProgramDescription());
		assertThat(resultFromService).isSameAs(newProgDTO);
		
		//verify(progRepo).findById(programId);
		verify(progMap, times(1)).toProgramDTO(newProg);
		
			
	}
	
	@Test
	void testGetAllPrograms() throws ResourceNotFoundException
	{
		//Given
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		Program newProg= new Program(Long.valueOf(20),"DesignPatterns",null, "nonActive",timestamp, timestamp);
		
		List<Program> ListofEntites =new ArrayList<>();
		
		ListofEntites.add(newProg);
		
		
		ProgramDTO newProgDTO= new ProgramDTO(Long.valueOf(20),"DesignPatterns",null,"nonActive",timestamp,timestamp);
		
		List<ProgramDTO> ListofDTOs = new ArrayList<>();
		
		ListofDTOs.add(newProgDTO);
		
		
		Mockito.when(progRepo.findAll()).thenReturn(ListofEntites);
		
		Mockito.when(progMap.toProgramDTOList(ListofEntites)).thenReturn(ListofDTOs);
		
		
		//when or Action
		List<ProgramDTO> fetchedFromService = programService.getAllPrograms();
		
		//then or Assert
		assertThat(fetchedFromService).isNotNull();
		assertThat(fetchedFromService.size()).isEqualTo(ListofDTOs.size());
	    assertThat(fetchedFromService).isSameAs(ListofDTOs);
	
	    //verify(progRepo).findAll();
	    verify(progMap).toProgramDTOList(ListofEntites);
	}
	
	//createAndSaveProgram 
	@Test
	void testcreateAndSaveProgram() throws ResourceNotFoundException, DuplicateResourceFoundException
	{
		//Given
		Long programId =Long.valueOf(50);
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		ProgramDTO reqDTO = new ProgramDTO(programId,"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		Program newProg= new Program(programId,"SpringTesting",null, "nonActive",timestamp, timestamp);
		
		List<Program> ListofEntites =new ArrayList<>();
		
		//ListofEntites.add(newProg);
		//not adding because ,  there should not be existing records with new program name,
		//so the result of query to db should return list with zero records.
		
		Program savedEntity = new Program(Long.valueOf(50),"SpringTesting",null, "nonActive",timestamp, timestamp);
		
		ProgramDTO newProgDTO= new ProgramDTO(Long.valueOf(50),"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		
		
		Mockito.when(progMap.toProgramEntity(reqDTO)).thenReturn(newProg);
		
		Mockito.when(progRepo.findByProgramName(reqDTO.getProgramName())).thenReturn(ListofEntites);
		
		Mockito.when(progRepo.save(newProg)).thenReturn(savedEntity);
		
		//Mockito.when(progMap.toProgramDTO(savedEntity)).thenReturn(newProgDTO);
		
		
		//when or Action
		ProgramDTO fetchedFromService = programService.createAndSaveProgram(reqDTO);
		
		//then or Assert
		assertThat(fetchedFromService).isNotNull();
		assertThat(fetchedFromService.getProgramDescription()).isEqualTo(newProgDTO.getProgramDescription());
		assertThat(fetchedFromService.getProgramName()).isEqualTo(reqDTO.getProgramName());
	    //assertThat(fetchedFromService).isSameAs(newProgDTO);
		//assertThat(reqDTO).isSameAs(fetchedFromService);
		assertThat(newProgDTO).isNotNull();
		
		verify(progMap).toProgramEntity(reqDTO);
		verify(progRepo).findByProgramName(reqDTO.getProgramName());
		verify(progRepo).save(newProg);
		//verify(progMap).toProgramDTO(savedEntity);
	
	}
	
	//updateProgramById
	@Test
	void testupdateProgramById() throws ResourceNotFoundException, DuplicateResourceFoundException
	{
		//Given
		Long programId =Long.valueOf(50);
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		ProgramDTO reqDTO = new ProgramDTO(programId,"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		Program toBeUpdatedEntity= new Program(programId,"DesignPatterns","new Course", "Active",timestamp, timestamp);
		
		Program savedEntity = new Program(programId,"SpringTesting",null, "nonActive",timestamp, timestamp);
		
		ProgramDTO newProgDTO= new ProgramDTO(programId,"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		
		Mockito.when(progRepo.existsById(programId)).thenReturn(true);
		
		Mockito.when(progRepo.findById(programId)).thenReturn(Optional.of(toBeUpdatedEntity));
		
		Mockito.when(progRepo.save(toBeUpdatedEntity)).thenReturn(savedEntity);
		
		//Mockito.when(progMap.toProgramDTO(savedEntity)).thenReturn(newProgDTO);
		
		
		//when or Action
		ProgramDTO fetchedFromService = programService.updateProgramById(programId,reqDTO);
		
		//then or Assert
		assertThat(fetchedFromService).isNotNull();
		assertThat(fetchedFromService.getProgramDescription()).isEqualTo(reqDTO.getProgramDescription());
		assertThat(fetchedFromService.getProgramName()).isEqualTo(reqDTO.getProgramName());
	    //assertThat(fetchedFromService).isSameAs(newProgDTO);
		//assertThat(reqDTO).isSameAs(fetchedFromService);
		assertThat(newProgDTO).isNotNull();
		
		verify(progRepo).existsById(programId);
		verify(progRepo).findById(programId);
		verify(progRepo).save(toBeUpdatedEntity);
		
		//verify(progMap).toProgramDTO(savedEntity);
		
	
	}
	
	//updateProgramByName
	@Test
	void testUpdateProgramByName() throws ResourceNotFoundException, DuplicateResourceFoundException
	{
		//Given
		String programName ="DesignPatterns";
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		ProgramDTO reqDTO = new ProgramDTO(Long.valueOf(50),"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		Program toBeUpdatedEntity= new Program(Long.valueOf(50),"DesignPatterns","new Course", "Active",timestamp, timestamp);
		
		List<Program> ListOfEntitesWithProgName = new ArrayList<>();
		ListOfEntitesWithProgName.add(toBeUpdatedEntity);
		
		Program savedEntity = new Program(Long.valueOf(50),"SpringTesting",null, "nonActive",timestamp, timestamp);
		
		ProgramDTO newProgDTO= new ProgramDTO(Long.valueOf(50),"SpringTesting",null,"nonActive",timestamp,timestamp);
		
		
		
		Mockito.when(progRepo.findByProgramName(programName)).thenReturn(ListOfEntitesWithProgName);
		
		Mockito.when(progRepo.save(toBeUpdatedEntity)).thenReturn(savedEntity);
		
		////Mockito.when(progMap.toProgramDTO(savedEntity)).thenReturn(newProgDTO);
		
		
		//when or Action
		ProgramDTO fetchedFromService = programService.updateProgramByName(programName,reqDTO);
		
		//then or Assert
		assertThat(fetchedFromService).isNotNull();
		assertThat(fetchedFromService.getProgramDescription()).isEqualTo(reqDTO.getProgramDescription());
		assertThat(fetchedFromService.getProgramName()).isEqualTo(reqDTO.getProgramName());
	    //assertThat(fetchedFromService).isSameAs(newProgDTO);
		//assertThat(reqDTO).isSameAs(fetchedFromService);
		assertThat(newProgDTO).isNotNull();
		
		verify(progRepo).findByProgramName(programName);
		verify(progRepo).save(toBeUpdatedEntity);
		//verify(progMap).toProgramDTO(savedEntity);
	}
	
	//deleteByProgramId
	@Test
	void testDeleteByProgramId() throws ResourceNotFoundException, DuplicateResourceFoundException
	{
		//Given
		Long programId =(long) 5;
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		Program deletingEntity = new Program(programId,"SpringTesting",null, "nonActive",timestamp, timestamp);
		
		//programDTO DeletedDTO= new programDTO(50,"SpringTesting",null,"nonActive",timestamp,timestamp,null);
		
		
		Mockito.when(progRepo.existsById(programId)).thenReturn(true);
		
		Mockito.when(progRepo.findById(programId)).thenReturn(Optional.of(deletingEntity));
		
		//Mockito.when(progRepo.delete(deletingEntity)).then();
		
		//when or Action
		boolean value =programService.deleteByProgramId(programId);
		
		//then or Assert
		assertThat(value).isEqualTo(true);
		verify(progRepo, times(1)).existsById(programId);
		verify(progRepo,times(1)).findById(programId);
		verify(progRepo).delete(deletingEntity);
		//verify(progRepo, times(1)).deleteById(programId);
	
	}
	
	//deleteByProgramName
	@Test
	void testDeleteByProgramName() throws ResourceNotFoundException, DuplicateResourceFoundException
	{
		//Given
		String programName ="Spring MVC";
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		
		Program toBeDeletedEntity= new Program(Long.valueOf(50),"Spring MVC","new Course", "Active",timestamp, timestamp);
		
		List<Program> ListOfEntitesWithProgName = new ArrayList<>();
		ListOfEntitesWithProgName.add(toBeDeletedEntity);
		
		Mockito.when(progRepo.findByProgramName(programName)).thenReturn(ListOfEntitesWithProgName);
		
		//Mockito.when(progRepo.delete(deletingEntity)).then();
		
		//when or Action
		boolean value =programService.deleteByProgramName(programName);
		
		//then or Assert
		assertThat(value).isEqualTo(true);
		verify(progRepo,times(1)).findByProgramName(programName);
		verify(progRepo).delete(toBeDeletedEntity);
		
	}
}
