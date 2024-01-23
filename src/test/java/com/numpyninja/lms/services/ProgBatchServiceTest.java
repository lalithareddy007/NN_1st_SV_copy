package com.numpyninja.lms.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.BatchMapper;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.services.ProgBatchServices;
import com.numpyninja.lms.repository.ProgBatchRepository;


@ExtendWith(MockitoExtension.class)        //Extension that initializes mocks and handles strict stubbings. This extension is the JUnit Jupiter equivalent of our JUnit4 MockitoJUnitRunner. 
public class ProgBatchServiceTest {
	@Mock
	private ProgBatchRepository batchRepository;
	
	@Mock
	ProgramRepository programRepository;
	
	@Mock
	private BatchMapper batchMapper;
	
	@InjectMocks
	ProgBatchServices batchService;
  
	private static Batch batch1, batch2, batch3;
	private static Program program1, program2; 
	private static BatchDTO batchDTO1, batchDTO2, batchDTO3; 
	private static List<Batch> listOfBatches= new ArrayList();
	private static List<BatchDTO> listOfDTOs = new ArrayList();
	
    @BeforeAll
    public static void setData() {

		program1 = new Program(1L,"SDET","SDET Program","Active",Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2022-02-04 22:16:02.713"));
		program2 = new Program(2L,"DataScience","DS Program","Active",Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2022-02-04 22:16:02.713"));

    	batchDTO1 = new BatchDTO(1,"01","SDET BATCH 01","Active",  6, 1L, "SDET" );
    	batchDTO2 = new BatchDTO(2,"01","DatScience 01","Active", 4, 2L, "Data Science");
    	batchDTO3 = new BatchDTO(3,"02","SDET BATCH 03","Active", 6, 1L, "SDET");
    	listOfDTOs.add(batchDTO1);
    	listOfDTOs.add(batchDTO2);
    	listOfDTOs.add(batchDTO3); 

     	batch1 = new Batch(1,"01","SDET BATCH 01","Active", program1, 6, Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2022-02-04 22:16:02.713") );
     	batch2 = new Batch(2,"01","DataScience 01","Active", program2, 4, Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2021-10-04 22:16:02.713") );
     	batch3 = new Batch( 3, "02","SDET 03", "Active", program1,6,Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2021-10-04 22:16:02.713") );
     	listOfBatches.add(batch1); 
     	listOfBatches.add(batch2);
     	listOfBatches.add(batch3);
    }
    
    
    @DisplayName("JUnit test for getAllBatches method")
    @Test
    @Order(1)
    public void givenBatchList_WhenGetAllBatches_ThenReturnBatchDTOList(){
        // given 
        given(batchRepository.findAll(Sort.by("batchName"))).willReturn(listOfBatches);
        given ( batchMapper.toBatchDTOs(listOfBatches) ).willReturn(listOfDTOs);
        // when 
        List<BatchDTO> listOfDTOs = batchService.getAllBatches();

        // then 
        assertThat(listOfDTOs).isNotNull();
        assertThat(listOfDTOs.size()).isEqualTo(3);
    }

    
    @DisplayName("JUnit test for getAllBatches for search string method") /* newly added */
    @Test
    @Order(2)
    public void givenBatchName_WhenGetBatches_ThenReturnBatchDTOList(){
    	String batchName = "01";
    	List<Batch> batch1List= new ArrayList();
    	batch1List.add(batch1);
    	batch1List.add(batch3);
    	
    	List<BatchDTO> batch1_DTOList = new ArrayList();
    	batch1_DTOList.add( batchDTO1);
    	batch1_DTOList.add( batchDTO3);
    	
    	// given 
        given(batchRepository.findByBatchNameContainingIgnoreCaseOrderByBatchIdAsc(batchName)).willReturn(batch1List);
        given ( batchMapper.toBatchDTOs(batch1List) ).willReturn(batch1_DTOList);
        // when 
        List<BatchDTO> listOfDTOs = batchService.getAllBatches( batchName );

        // then 
        assertThat(listOfDTOs).isNotNull();
        assertThat(listOfDTOs.size()).isEqualTo(2);    	
    }
       
    @DisplayName("JUnit test for getBatchById method")
    @Test
    @Order(3)
    public void givenBatchId_WhenGetBatchById_ThenReturnBatchDTO(){
    	// given
    	Integer batchId = 1;
        given(batchRepository.findById(1)).willReturn(Optional.of(batch1));
        given (  batchMapper.toBatchDTO( batch1 )).willReturn(batchDTO1);
        // when
        BatchDTO batchDTO1 = batchService.findBatchById(batchId);

        // then
        assertThat(batchDTO1).isNotNull();
        assertThat(batchDTO1.getBatchId()).isEqualTo(1);
    }
    
    
	@DisplayName("JUnit test for findBatchByProgramId")          
	@Test
	@Order(4)	
	public void givenProgramId_WhenGetBatches_ThenReturnListOfBatches() {
		// given
		Long programId = (long)1; 
    	List<Batch> prog1List= new ArrayList();
    	prog1List.add(batch1);
    	prog1List.add(batch2);
    	
    	List<BatchDTO> prog1_DTOList = new ArrayList();
    	prog1_DTOList.add( batchDTO1);
    	prog1_DTOList.add( batchDTO2);
    	
		given ( batchRepository.findByProgramProgramId(programId)).willReturn( prog1List );
		given ( batchMapper.toBatchDTOs(prog1List) ).willReturn(prog1_DTOList);
		
		// when 
        List<BatchDTO> listOfDTOs = batchService.findBatchByProgramId( programId );
        
        // then 
        assertThat(listOfDTOs).isNotNull();
        assertThat(listOfDTOs.size()).isEqualTo(2);    	
    }
    
	
    @DisplayName("JUnit test to create Batch")
	@Test
	@Order(5)
	public void givenBatchDTO_WhenSave_ThenReturnSavedBatchDTO() throws Exception {
		// given
		Long programId = 1L;
		given(programRepository.findById(programId)).willReturn( Optional.of(program1));
		given (  batchMapper.toBatch( batchDTO3 )).willReturn(batch3);
		given( batchRepository.save( batch3)).willReturn(batch3);
		given (  batchMapper.toBatchDTO( batch3 )).willReturn(batchDTO3);
		
		// when
		BatchDTO savedBatchDTO = batchService.createBatch(batchDTO3);

		// Then
		assertThat(savedBatchDTO).isNotNull();  
		assertThat(savedBatchDTO.getBatchId()).isEqualTo(3);
	}
    
    
    @DisplayName("JUnit test to create a duplicate Batch")                  
	@Test                                                                       
	@Order(6)                         
	public void givenExistingBatch_WhenSave_ThenThrowsException() throws Exception {
		// given
		Long programId = (long)1;
		given(programRepository.findById(programId)).willReturn( Optional.of(program1) );
		given (  batchMapper.toBatch( batchDTO3 )).willReturn(batch3);
		given (batchRepository.findByBatchNameAndProgram_ProgramId(batch3.getBatchName(), programId)).willReturn(batch3);
		
		// when && then
		Assertions.assertThrows(DuplicateResourceFoundException.class, ()->batchService.createBatch(batchDTO3));
	}
    
    
    
	@DisplayName("JUnit test for update Batch")
	@Test
	@Order(7)
	public void givenUpdatedBatch_WhenUpdateBatch_ThenReturnUpdateBatchObject()  throws Exception {
		// given
		Integer batchId = 1; Long programIdToUpdate = (long)2 ;
		BatchDTO updateDetailDTO = new BatchDTO(1,"01","Datascience BATCH 01 Updation","Active", 6, (long)2, "Datascience");
		Batch updatedBatch = new Batch(1,"01","Datascience BATCH 01 Updation","Active", program2, 6, Timestamp.valueOf("2022-10-04 22:16:02.713"), Timestamp.valueOf("2022-02-04 22:16:02.713") );
		
		given(batchRepository.findById(batchId)).willReturn(Optional.of(batch1));
	    given ( batchMapper.toBatch( updateDetailDTO )).willReturn(updatedBatch);
		given( programRepository.findById( programIdToUpdate )).willReturn(Optional.of( program2 ));
		
		given( batchRepository.save(updatedBatch)).willReturn(updatedBatch);
		// when
		updateDetailDTO = batchService.updateBatch(updateDetailDTO,batchId);
		// Then
		assertThat(updatedBatch).isNotNull();
		assertThat(updatedBatch.getProgram().getProgramId()).isEqualTo(2);
	}
	
	
	@DisplayName("JUnit test for delete Batch")
	@Test
	@Order(8)
	public void givenBatchId_WhenDeleteBatch_ThenDeleteBatchInDB() throws Exception{
		//given
		Integer batchId = 2;
		given(batchRepository.findById(batchId)).willReturn(Optional.of(batch2));
		willDoNothing().given(batchRepository).deleteById(batchId);
		//when
		batchService.deleteProgramBatch(batchId);
		//then
		verify(batchRepository).deleteById(batchId);
	}
}
