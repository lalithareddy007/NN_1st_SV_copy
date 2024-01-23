package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ProgramMapper;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.ProgramRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {
    @InjectMocks
    private ProgramServices programServices;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private ProgBatchRepository progBatchRepository;
    @Mock
    private ProgramMapper programMapper;
    private static ProgramDTO programDto1;
    private static ProgramDTO programDto2;
    private static Program program1;
    private static Program program2;
    private static List<Program> programList = new ArrayList<>();
    private static List<ProgramDTO> programDTOList = new ArrayList<>();

    @BeforeAll
    public static void setData() {
        program1 = new Program(1L, "Java", "Java Description", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        program2 = new Program(2L, "SQL", "SQL Basic", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        programList.add(program1);
        programList.add(program2);
        programDto1 = new ProgramDTO(1L, "Java", "Java Description", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        programDto2 = new ProgramDTO(2L, "SQL", "SQL Basic", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        programDTOList.add(programDto1);
        programDTOList.add(programDto2);
    }

    @DisplayName("Test for Get All Programs")
    @Test
    @SneakyThrows
    public void testForGetAllPrograms() {
        //given
        when(programRepository.findAll()).thenReturn(programList);
        when(programMapper.toProgramDTOList(programList)).thenReturn(programDTOList);
        //when
        List<ProgramDTO> programDTOS = programServices.getAllPrograms();
        //then
        assertThat(programDTOS.size()).isGreaterThan(0);
        verify(programRepository).findAll();
        verify(programMapper).toProgramDTOList(programList);
    }
    @DisplayName("Test For Get Programs By ProgramId")
    @SneakyThrows
    @Test
    public void testForGettingAllProgramsByProgramId(){
        Long programId=2L;
        //given
        when(programRepository.existsById(programId)).thenReturn(true);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program2));
        //when
        ProgramDTO programDto3 =programServices.getProgramsById(programId);
        //then
        assertThat(programList.size()).isGreaterThan(0);
    }
    @DisplayName("Test for Creating a Program")
    @SneakyThrows
    @Test
    public void testForCreatingProgram(){
    //given
        when(programMapper.toProgramEntity(programDto1)).thenReturn(program1);
    when(programRepository.findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(program1.getProgramName()))
            .thenReturn(Collections.emptyList());
    when(programRepository.save(program1)).thenReturn(program1);
    //when
    ProgramDTO savedProgramDTO=programServices.createAndSaveProgram(programDto1);
    //then
    assertThat(programDto1).isNotNull();
    }

    @DisplayName("Test for Creating a Program- When Program already exist")
    @SneakyThrows
    @Test
    public void testForCreatingProgramWhenProgramAlreadyExist(){
        //given
        String message="cannot create program , since already exists";
        when(programMapper.toProgramEntity(programDto1)).thenReturn(program1);
        when(programRepository.findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(program1.getProgramName()))
                .thenReturn(programList);
        //when
        Exception e = assertThrows(DuplicateResourceFoundException.class, () -> programServices.createAndSaveProgram(programDto1));
        //then
        assertEquals(message, e.getMessage());
        verify(programMapper).toProgramEntity(programDto1);
        verify(programRepository).findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(program1.getProgramName());

    }

   @DisplayName("Test for Updating a Program By ProgramID")
    @SneakyThrows
    @Test
    public void testForUpdatingProgramByProgramId(){
        //given
        Long programId=1L;
        String programName="Oracle";
        ProgramDTO updatedProgramDto = programDto1;
        updatedProgramDto.setProgramName(programName);
        Program savedProgram =program1;
        savedProgram.setProgramName(programName);
        when(programRepository.existsById(programId)).thenReturn(true);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program1));
        when(programRepository.save(program1)).thenReturn(savedProgram);


        //when
        ProgramDTO programDTO = programServices.updateProgramById(programId,updatedProgramDto);
        //then
        assertThat(programDTO).isNotNull();
        assertThat(programDTO.getProgramName()).isEqualTo(programName);
    }

    @DisplayName("Test for Updating a Program By ProgramId - When ProgramId is not found")
    @SneakyThrows
    @Test
    public void testForUpdatingProgramByProgramIdWhenIdIsNotFound(){
        Long programId=1L;
        String programName="Oracle";
        String message = "program with id" + programId + "not found";
        ProgramDTO updatedProgramDto = programDto1;
        updatedProgramDto.setProgramName(programName);
        when(programRepository.existsById(programId)).thenReturn(false);
        Exception e = assertThrows(ResourceNotFoundException.class, () -> programServices.updateProgramById(programId,updatedProgramDto));
        assertEquals(message, e.getMessage());
        verify(programRepository).existsById(programId);
    }
    @DisplayName("Test for Updating a Program By ProgramName")
    @SneakyThrows
    @Test
    public void testForUpdatingProgramByProgramName(){
        //given
        String programName="SQL";
        String programDescription1="Beginners Program";
        ProgramDTO updatedProgramDto = programDto2;
        updatedProgramDto.setProgramDescription(programDescription1);
        Program savedProgram =program2;
        savedProgram.setProgramDescription(programDescription1);
        when(programRepository.findByProgramName(programName)).thenReturn(programList);
        when(programRepository.save(program2)).thenReturn(savedProgram);
        when(programRepository.findById(program2.getProgramId())).thenReturn(Optional.of(program2));


        //when
        ProgramDTO programDTO = programServices.updateProgramByName(programName,updatedProgramDto);
        //then
        assertThat(programDTO).isNotNull();
        assertThat(programDTO.getProgramDescription()).isEqualTo(programDescription1);
    }

    @DisplayName("Test for Deleting a Program By Program Id")
    @SneakyThrows
    @Test
    public void testForDeletingProgramByProgramId(){
        //given
        Long programId=1L;
        when(programRepository.existsById(programId)).thenReturn(true);
        when(programRepository.findById(programId)).thenReturn(Optional.of(program1));
        willDoNothing().given(programRepository).delete(program1);
        //when
        Boolean isDeleted = programServices.deleteByProgramId(programId);
        //then
        assertThat(isDeleted).isEqualTo(true);
    }
    @DisplayName("Test for Deleting a Program By Program Name")
    @SneakyThrows
    @Test
    public void testForDeletingProgramByProgramName() {
        //given
        String programName="SQL";
     when(programRepository.findByProgramName(programName)).thenReturn(programList);
     willDoNothing().given(programRepository).delete(null);
     //when
        Boolean isDeleted =programServices.deleteByProgramName(programName);
        //then
        assertThat(isDeleted).isEqualTo(true);
    }
    @DisplayName("Test for Delete Programs By ProgramId - When Program ID is Not Found")
    @SneakyThrows
    @Test
    public void testDeleteProgramByProgramIdWhenIdIsNotFound() {
        //given
        Long programId = 2L;
        String message = "no record found with programId"+programId;
        when(programRepository.existsById(programId)).thenReturn(false);
        //when
        Exception e = assertThrows(ResourceNotFoundException.class, () -> programServices.deleteByProgramId(programId));
        //then
        assertEquals(message, e.getMessage());
        verify(programRepository).existsById(programId);
    }

}
