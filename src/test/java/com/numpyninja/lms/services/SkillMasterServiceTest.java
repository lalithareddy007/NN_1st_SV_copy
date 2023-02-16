package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.SkillMasterDto;
import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.SkillMasterMapper;
import com.numpyninja.lms.repository.SkillMasterRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillMasterServiceTest {

    @InjectMocks
    private SkillMasterService skillMasterService;

    @Mock
    private SkillMasterRepository skillMasterRepository;

    @Mock
    private SkillMasterMapper skillMasterMapper;

    private SkillMaster mockSkillMaster, mockSkillMaster2;
    private SkillMasterDto mockSkillMasterDto, mockSkillMasterDto2;
    private List<SkillMaster> skillMasterList;
    private List<SkillMasterDto> skillMasterDtoList;

    @BeforeEach
    public void setUp() {
        setMockSkillMasterAndDto();
    }

    private void setMockSkillMasterAndDto() {
        mockSkillMaster = new SkillMaster(1L, "Java Basics", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        mockSkillMasterDto = new SkillMasterDto(1L, "Java Basics", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

        skillMasterList = new ArrayList<>();
        skillMasterDtoList = new ArrayList<>();
    }

    @Nested
    class GetSkills {

        @BeforeEach
        public void setUp() {
            setMockSkillMasterAndDto2();
        }

        private void setMockSkillMasterAndDto2() {
            mockSkillMaster2 = new SkillMaster(2L, "SQL", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
            mockSkillMasterDto2 = new SkillMasterDto(2L, "SQL", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        }

        @DisplayName("test - GetAllSkills - When List contains Skills")
        @SneakyThrows
        @Test
        public void testGetAllSkillMaster() {
            //given
            skillMasterList.add(mockSkillMaster);
            skillMasterList.add(mockSkillMaster2);

            skillMasterDtoList.add(mockSkillMasterDto);
            skillMasterDtoList.add(mockSkillMasterDto2);

            when(skillMasterRepository.findAll()).thenReturn(skillMasterList);
            when(skillMasterMapper.toSkillMasterDTOList(skillMasterList)).thenReturn(skillMasterDtoList);

            //when
            List<SkillMasterDto> skillMasterDtos = skillMasterService.getAllSkillMaster();

            //then
            assertThat(skillMasterDtos.size()).isGreaterThan(0);

            verify(skillMasterRepository).findAll();
            verify(skillMasterMapper).toSkillMasterDTOList(skillMasterList);
        }

        @DisplayName("test - GetAllSkills - When List is Empty")
        @SneakyThrows
        @Test
        public void testGetAllSkillMasterWhenListIsEmpty() {
            //given
            String message = "skillMaster list is not found";
            given(skillMasterRepository.findAll()).willReturn(Collections.emptyList());

            //when
            Exception e = assertThrows(ResourceNotFoundException.class, () -> skillMasterService.getAllSkillMaster());

            //then
            assertEquals(message, e.getMessage());

            verify(skillMasterRepository).findAll();
        }

        @DisplayName("test - GetSkillByName - When List contains Skill")
        @SneakyThrows
        @Test
        public void testGetAllSkillMasterByName() {
            String skillName = "Java";
            skillMasterList.add(mockSkillMaster);
            skillMasterList.add(mockSkillMaster2);

            skillMasterDtoList.add(mockSkillMasterDto);
            skillMasterDtoList.add(mockSkillMasterDto2);

            //given
            when(skillMasterRepository.findBySkillName(skillName)).thenReturn(skillMasterList);
            when(skillMasterMapper.toSkillMasterDTOList(skillMasterList)).thenReturn(skillMasterDtoList);

            //when
            List<SkillMasterDto> smDtoList = skillMasterService.getSkillMasterByName(skillName);

            //then
            assertThat(smDtoList.size()).isGreaterThan(0);

            verify(skillMasterRepository).findBySkillName(skillName);
            verify(skillMasterMapper).toSkillMasterDTOList(skillMasterList);
        }

        @DisplayName("test - GetSkillByName - When List is Empty")
        @SneakyThrows
        @Test
        public void testGetAllSkillMasterByNameWhenListIsEmpty() {
            //given
            String skillName = "SQL";
            String message = "skill with id" + skillName + "not found";
            given(skillMasterRepository.findBySkillName(skillName)).willReturn(Collections.emptyList());

            //when
            Exception e = assertThrows(ResourceNotFoundException.class, () -> skillMasterService.getSkillMasterByName(skillName));

            //then
            assertEquals(message, e.getMessage());

            verify(skillMasterRepository).findBySkillName(skillName);
        }
    }

    @Nested
    class DeleteSkills {

        @DisplayName("test - DeleteSkillById - When Skill ID is available")
        @SneakyThrows
        @Test
        public void testDeleteSkillById() {
            //given
            Long skillId = 1L;

            when(skillMasterRepository.existsById(skillId)).thenReturn(true);
            when(skillMasterRepository.findById(skillId)).thenReturn(Optional.of(mockSkillMaster));
            willDoNothing().given(skillMasterRepository).delete(mockSkillMaster);

            //when
            Boolean isDeleted = skillMasterService.deleteBySkillId(skillId);

            //then
            assertThat(isDeleted).isEqualTo(true);

            verify(skillMasterRepository).existsById(skillId);
            verify(skillMasterRepository).findById(skillId);
            verify(skillMasterRepository).delete(mockSkillMaster);
        }

        @DisplayName("test - DeleteSkillById - When Skill ID is Not Found")
        @SneakyThrows
        @Test
        public void testDeleteSkillByIdWhenIdIsNotFound() {
            //given
            Long skillId = 3L;
            String message = "no record found with skillId";

            when(skillMasterRepository.existsById(skillId)).thenReturn(false);

            //when
            Exception e = assertThrows(ResourceNotFoundException.class, () -> skillMasterService.deleteBySkillId(skillId));

            //then
            assertEquals(message, e.getMessage());

            verify(skillMasterRepository).existsById(skillId);
        }

        @DisplayName("test - DeleteSkillById - When Skill ID is Null")
        @SneakyThrows
        @Test
        public void testDeleteSkillByIdWhenIdIsNull() {
            //when
            assertThrows(InvalidDataException.class, () -> skillMasterService.deleteBySkillId(null));

            //then
            verifyNoInteractions(skillMasterRepository);
        }

    }

    @Nested
    class CreateSkills {

        @DisplayName("test - CreateAndSaveSkillMaster - When Skill is created")
        @SneakyThrows
        @Test
        public void testCreateAndSaveSkillMaster() {
            //given
            when(skillMasterMapper.toSkillMasterEntity(mockSkillMasterDto)).thenReturn(mockSkillMaster);
            when(skillMasterRepository.findBySkillName(mockSkillMaster.getSkillName())).thenReturn(Collections.emptyList());
            when(skillMasterRepository.save(mockSkillMaster)).thenReturn(mockSkillMaster);

            //when
            SkillMasterDto skillMasterDto = skillMasterService.createAndSaveSkillMaster(mockSkillMasterDto);
         
            //then
            assertThat(skillMasterDto).isNotNull();
            assertEquals(mockSkillMasterDto.getSkillName(), skillMasterDto.getSkillName());
            
            verify(skillMasterMapper).toSkillMasterEntity(mockSkillMasterDto);
            verify(skillMasterRepository).findBySkillName(mockSkillMaster.getSkillName());
            verify(skillMasterRepository).save(mockSkillMaster);
        }

        @DisplayName("test - CreateAndSaveSkillMaster - When Skill already exists")
        @SneakyThrows
        @Test
        public void testCreateAndSaveSkillMasterWhenSkillAlreadyExists() {
            //given
            skillMasterList.add(mockSkillMaster);
            String message = "cannot create skillMaster , since already exists";
            when(skillMasterMapper.toSkillMasterEntity(mockSkillMasterDto)).thenReturn(mockSkillMaster);
            when(skillMasterRepository.findBySkillName(mockSkillMaster.getSkillName())).thenReturn(skillMasterList);

            //when
            Exception e = assertThrows(DuplicateResourceFoundException.class, () -> skillMasterService.createAndSaveSkillMaster(mockSkillMasterDto));

            //then
            assertEquals(message, e.getMessage());

            verify(skillMasterMapper).toSkillMasterEntity(mockSkillMasterDto);
            verify(skillMasterRepository).findBySkillName(mockSkillMaster.getSkillName());
        }
    }

    @Nested
    class UpdateSkills {

        @DisplayName("test - UpdateSkillById - When Skill ID is available")
        @SneakyThrows
        @Test
        public void testUpdateSkillMasterById() {
            //given
            Long skillId = 1L;
            String skillName = "Java/J2EE";
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillName(skillName);
            SkillMaster savedSkillMaster = mockSkillMaster;
            savedSkillMaster.setSkillName(skillName);

            when(skillMasterRepository.existsById(skillId)).thenReturn(true);
            when(skillMasterRepository.findById(skillId)).thenReturn(Optional.of(mockSkillMaster));
            when(skillMasterRepository.save(mockSkillMaster)).thenReturn(savedSkillMaster);

            //when
            SkillMasterDto skillMasterDto = skillMasterService.updateSkillMasterById(skillId, updatedSkillMasterDto);

            //then
            assertThat(skillMasterDto).isNotNull();
            assertThat(skillMasterDto.getSkillName()).isEqualTo(skillName);

            verify(skillMasterRepository).existsById(skillId);
            verify(skillMasterRepository).findById(skillId);
            verify(skillMasterRepository).save(mockSkillMaster);
        }

        @DisplayName("test - UpdateSkillById - When Skill ID is Not Found")
        @SneakyThrows
        @Test
        public void testUpdateSkillMasterByIdWhenIdIsNotFound() {
            //given
            Long skillId = 1L;
            String skillName = "Java/J2EE";
            String message = "skill with id" + skillId + "not found";
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillName(skillName);

            when(skillMasterRepository.existsById(skillId)).thenReturn(false);

            //when
            Exception e = assertThrows(ResourceNotFoundException.class, () -> skillMasterService.updateSkillMasterById(skillId, updatedSkillMasterDto));

            //then
            assertEquals(message, e.getMessage());

            verify(skillMasterRepository).existsById(skillId);
        }

        @DisplayName("test - UpdateSkillById - When Skill ID is Null")
        @SneakyThrows
        @Test
        public void testUpdateSkillMasterByIdWhenIdIsNull() {
            //given
            String skillName = "Java/J2EE";
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillName(skillName);

            //when
            assertThrows(InvalidDataException.class, () -> skillMasterService.updateSkillMasterById(null, updatedSkillMasterDto));

            //then
            verifyNoInteractions(skillMasterRepository);
        }

    }

}
