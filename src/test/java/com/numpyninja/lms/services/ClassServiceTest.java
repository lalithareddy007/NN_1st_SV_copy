package com.numpyninja.lms.services;


import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ClassScheduleMapper;
import com.numpyninja.lms.repository.ClassRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgBatchRepository batchRepository;

    @InjectMocks
    private ClassService classService;

    @Mock
    private ClassScheduleMapper classScheduleMapper;
    
    @Mock
     private UserRoleMapRepository userRoleMapRepository;
    
    
   

    private Class mockClass, mockClass2;

    private ClassDto mockClassDto, mockClassDto2;

    private List<Class> classList;

    private List<ClassDto> classDtoList;

    private List<UserRoleMap> mockUserRoleMaps;


    @BeforeEach
    public void setup() {
        mockClass = setMockClassAndDto();
    }

   

    private Class setMockClassAndDto() {
        String sDate = "09/21/2021";
        Date classDate = null;
        try {
            classDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        User staffInClass = new User("U02", "Steve", "Jobs", "",(long) 1234567890, "CA", "PST", "@stevejobs",
                "", "", "", "Citizen", timestamp, timestamp);
        Program program = new Program((long) 7, "Django", "new Prog", "nonActive", timestamp, timestamp);
        Batch batchInClass = new Batch(3, "SDET 1", "SDET Batch 1", "Active", program, 5, timestamp, timestamp);

        mockClass = new Class((long) 1, batchInClass, 1, classDate,"Active",
                "Selenium",staffInClass, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath", timestamp, timestamp);

        mockClassDto = new ClassDto((long) 1, 3, 1, classDate,
                "Selenium", "Active","U02", "Selenium Class" ,"OK",
                "c:/ClassNotes",
                "c:/RecordingPath");

        mockClass2 = new Class((long) 2, batchInClass, 2, classDate,
                "Selenium1", "Active",staffInClass, "Selenium Class1", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath", timestamp, timestamp);

        mockClassDto2= new ClassDto((long) 2, 3, 2, classDate,
                "Selenium1","Active", "U02", "Selenium Class1", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath");
        
       User user = new User("U02", "Steve", "Jobs", "Martin",
				1234567890L, "CA", "PST", "@stevejobs", "",
				"", "", "Citizen", timestamp, timestamp);
        
        
        ClassRecordingDTO classRecordingDTO = new ClassRecordingDTO(14L, "C://");
        
        
    	mockUserRoleMaps = new ArrayList<UserRoleMap>();

		Role role = new Role("R02", "Staff", "LMS_Staff", timestamp, timestamp);

		UserRoleMap userRoleMap = new UserRoleMap();
		userRoleMap.setUserRoleId(1L);
		userRoleMap.setUserRoleStatus("Active");
		userRoleMap.setUser(staffInClass);
		userRoleMap.setRole(role);
		userRoleMap.setCreationTime(timestamp);
		userRoleMap.setLastModTime(timestamp);
		mockUserRoleMaps.add(userRoleMap);
        classList = new ArrayList<>();
        classDtoList = new ArrayList<>();
        return mockClass;



    }

    private Batch setMockBatch() {
        LocalDateTime now= LocalDateTime.now();
        Timestamp timestamp= Timestamp.valueOf(now);
        Program program = new Program((long) 7,"Django","new Prog", "nonActive",timestamp, timestamp);
        Batch batch = new Batch(3, "SDET 1", "SDET Batch 1", "Active", program, 5, timestamp, timestamp);

        return batch;
    }

    @Nested
    class GetSClass {
        @BeforeEach
        public void setup() {
            setMockClassAndDto1();
        }

        private User setMockClassAndDto1() {

            User user1 = new User("U02", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
                    "", "", "", "Citizen", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        return user1;
        }


        @DisplayName("test - Get AlL Class ")
        @SneakyThrows
        @Test
        public void testGetAllClass() throws ResourceNotFoundException {
            //given

            classList.add(mockClass);
            classList.add(mockClass2);

            classDtoList.add(mockClassDto);
            classDtoList.add(mockClassDto2);
            when(classRepository.findAll()).thenReturn(classList);
            when(classScheduleMapper.toClassScheduleDTOList(classList)).thenReturn(classDtoList);

            //when
            List<ClassDto> classDtos = classService.getAllClasses();


            //then
            assertThat(classDtos.size()).isGreaterThan(0);

            verify(classRepository).findAll();
            verify(classScheduleMapper).toClassScheduleDTOList(classList);
        }

        @DisplayName("test - Get All ClassSchedule  When List is Empty")
        @SneakyThrows
        @Test
        public void testGetAllClassScheduleWhenListIsEmpty() throws ResourceNotFoundException {
            //given

            given(classRepository.findAll()).willReturn(Collections.emptyList());

            //when
            assertThrows(ResourceNotFoundException.class, () -> classService.getAllClasses());


            //then
            Mockito.verify(classRepository).findAll();

        }

        @DisplayName("test - get Class By Class Topic")
        @SneakyThrows
        @Test
        public void testGetClassesByClassTopic() throws ResourceNotFoundException {
            String classTopic = "Selenium";
            classList.add(mockClass);
            classList.add(mockClass2);

            classDtoList.add(mockClassDto);
            classDtoList.add(mockClassDto2);

            //given
            when(classRepository.findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(classTopic)).thenReturn(classList);
            when(classScheduleMapper.toClassScheduleDTOList(classList)).thenReturn(classDtoList);

            //when
            List<ClassDto> cDtoList = classService.getClassesByClassTopic(classTopic);


            //then
            assertThat(cDtoList.size()).isGreaterThan(0);

            verify(classRepository).findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(classTopic);
            verify(classScheduleMapper).toClassScheduleDTOList(classList);
        }

        @DisplayName("test - get Class By ClassTopic When List is Empty")
        @SneakyThrows
        @Test
        public void testGetClassesByClassTopicWhenListIsEmpty() throws ResourceNotFoundException {
            //given
            String classTopic = "xbcjhsdbcj";
            given(classRepository.findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(classTopic)).willReturn(Collections.emptyList());


            //when
            assertThrows(ResourceNotFoundException.class, () -> classService.getClassesByClassTopic(classTopic));


            //then

            Mockito.verify(classRepository).findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(classTopic);

        }

        @DisplayName("test - get Class By Class Id")
        @SneakyThrows
        @Test
        public void testGetClassByClassId() throws ResourceNotFoundException {
            //given
            given(classRepository.findById(mockClass.getCsId())).willReturn(Optional.of(mockClass));
            given(classScheduleMapper.toClassSchdDTO(mockClass)).willReturn(mockClassDto);


            //when
            ClassDto classDto = classService.getClassByClassId(mockClass.getCsId());


            //then
            assertThat(classDto).isNotNull();


        }

      /* @DisplayName("test -when class Id  for class is not found")
       @SneakyThrows
        @Test
        public void testGetClassByClassIdNotFound() throws ResourceNotFoundException {

            //given

           given(classRepository.findById(mockClass.getCsId())).willReturn(Optional.empty());

           //when
           Assertions.assertThrows(NoSuchElementException.class,
                   () -> classService.getClassByClassId(mockClass.getCsId()));

           //then

            Mockito.verify(classScheduleMapper, never()).toClassSchdDTO(any(Class.class));


        }*/

        @DisplayName("test for creating a new class")
        @SneakyThrows
        @Test
        void testCreateClass() throws DuplicateResourceFoundException {
        	
//given
          Batch batch = setMockBatch();
          User user = setMockClassAndDto1();
            
          when(classScheduleMapper.toClassScheduleEntity(mockClassDto)).thenReturn(mockClass);
          when(batchRepository.findById(mockClass.getBatchInClass().getBatchId())).thenReturn(Optional.of(batch));
          when(userRepository.findById(mockClass.getStaffInClass().getUserId())).thenReturn(Optional.of(user));
          when(classRepository.save(mockClass)).thenReturn(mockClass);
          when(classScheduleMapper.toClassSchdDTO(mockClass)).thenReturn(mockClassDto);

       //when
            ClassDto classDto = classService.createClass(mockClassDto);

       //then
            AssertionsForClassTypes.assertThat(classDto).isNotNull();
   
        	
  }



        @DisplayName("test for deleting class by Id")
        @SneakyThrows
        @Test
        void testDeleteClass() {
            //given
            Long classId = 1L;
            when(classRepository.existsById(classId)).thenReturn(true);
            willDoNothing().given(classRepository).deleteById(classId);

            //when
            Boolean isDeleted = classService.deleteByClassId(classId);


            //then
            AssertionsForClassTypes.assertThat(isDeleted).isEqualTo(true);

            verify(classRepository).existsById(classId);
            verify(classRepository).deleteById(classId);

        }

        @DisplayName("test for deleting class when id is not found")
        @SneakyThrows
        @Test
        public void testDeleteClassIdNotFound() {
            //given
            Long classId = 4L;
            when(classRepository.existsById(classId)).thenReturn(false);

            //when
            assertThrows(ResourceNotFoundException.class, () -> classService.deleteByClassId(classId));

            //then

            Mockito.verify(classRepository).existsById(classId);

        }
        


        @DisplayName("test - When class ID is Null")
        @SneakyThrows
        @Test
        public void testDeleteSClassByIdWhenIdIsNull() {
            //when
            assertThrows(InvalidDataException.class, () -> classService.deleteByClassId(null));

            //then
            verifyNoInteractions(classRepository);
        }


        @DisplayName("test -get class by batchId")
        @SneakyThrows
        @Test
        void testGetClassesForBatch() {
            //given

            Integer batchId = 1;

            classList.add(mockClass);
            classList.add(mockClass2);
            classDtoList.add(mockClassDto);
            classDtoList.add(mockClassDto2);

            given(classRepository.findByBatchInClass_batchId(batchId)).willReturn(classList);
            given(classScheduleMapper.toClassScheduleDTOList(classList)).willReturn(classDtoList);


            //when
            List<ClassDto> classDtos = classService.getClassesByBatchId(batchId);


            //then
            assertThat(classDtos).isNotNull();
            assertThat(classDtos.size()).isGreaterThan(0);
        }
       /* @DisplayName("test -When  batchId for class is not found")
        @SneakyThrows
        @Test
        public void testGetClassesForBatchIdNotFound() {
            //given
            Integer batchId =4;
            given(classRepository.findByBatchInClass_batchId(batchId)).willReturn(Collections.emptyList());


            //when
            assertThrows(ResourceNotFoundException.class, () -> classService.getClassesByBatchId(batchId));

            //then


            Mockito.verify(classRepository).findByBatchInClass_batchId(batchId);
        }*/

        @DisplayName("test - When Batch ID for class is Null")
        @SneakyThrows
        @Test
        public void testGetClassesForBatchIdWhenIdIsNull()  throws ResourceNotFoundException,InvalidDataException{
            //when
            assertThrows(InvalidDataException.class, () -> classService.getClassesByBatchId(null));

            //then
            verifyNoInteractions(classRepository);
        }

        @DisplayName("test - get class by staff Id")
        @SneakyThrows
        @Test
        void testGetClassesByStaffId() {
            //given

            String staffId = "U02";

            classList.add(mockClass);
            classList.add(mockClass2);
            classDtoList.add(mockClassDto);
            classDtoList.add(mockClassDto2);

            given(classRepository.findBystaffInClass_userId(staffId)).willReturn(classList);
            given(classScheduleMapper.toClassScheduleDTOList(classList)).willReturn(classDtoList);


            //when
            List<ClassDto> classDtos = classService.getClassesByStaffId(staffId);


            //then
            assertThat(classDtos).isNotNull();
            assertThat(classDtos.size()).isGreaterThan(0);
        }

       /*  @DisplayName("test -when  staff id  for class is  not found")
         @SneakyThrows
        @Test
       void testGetClassesByStaffIdNotFound() {

            //given

            String staffId = "U05";

            given(classRepository.findBystaffInClass_userId(staffId)).willReturn(Collections.emptyList());

            //when
             assertThrows(ResourceNotFoundException.class, () -> classService.getClassesByStaffId(staffId));

            //then
             Mockito.verify(classRepository).findBystaffInClass_userId(staffId);


        }*/


        @DisplayName("test - When staff ID for class is Null")
        @SneakyThrows
        @Test
        public void testGetClassesByStaffIdWhenIdIsNull() {
            //when.
            assertThrows(InvalidDataException.class, () -> classService.getClassesByStaffId(null));

            //then
            verifyNoInteractions(classRepository);
        }


  
        
        
        
        
        
        
      /* @DisplayName("test - to update class by class Id")
        @SneakyThrows
        @Test
        void testUpdateClassByClassId()  {
            //given
            Long classId = 7L;

            String classComments = "Selenium classes is Good";
            ClassDto updatedClassDto = mockClassDto;
            updatedClassDto.setClassComments(classComments);
            Class savedClass = mockClass;
            savedClass.setClassComments(classComments);

            given(classScheduleMapper.toClassScheduleEntity(mockClassDto)).willReturn(mockClass);
            given(classRepository.findById(classId)).willReturn(Optional.of(mockClass));
            given(userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase(
            		mockClassDto.getClassStaffId(), "R02", "Active"))
    				.willReturn(mockUserRoleMaps);
          
           given(batchRepository.getById(mockClassDto.getBatchId())).willReturn(mockClass.getBatchInClass());
            given(userRepository.getById(mockClassDto.getClassStaffId())).willReturn(mockClass.getStaffInClass());
            given(classRepository.save(mockClass)).willReturn(savedClass);
            given(classScheduleMapper.toClassSchdDTO(mockClass)).willReturn(updatedClassDto);


            //when
            ClassDto classDto = classService.updateClassByClassId(classId, updatedClassDto);

            //then
            assertThat(classDto).isNotNull();
            assertThat(classDto.getClassComments()).isEqualTo("Selenium classes is Good");

            //verify
            verify(classRepository).findById(classId);
            verify(classRepository).save(mockClass);


        }*/
       
       
       

      /*  @DisplayName("test - Update class when Id is not found")
        @SneakyThrows
        @Test
        void testUpdateClassWhoseIdIsNotFound() {
            //given
            Long classId = 4L;
            when(classRepository.findById(classId)).thenReturn(Optional.empty());

            //when
            assertThrows(ResourceNotFoundException.class,
                    () -> classService.updateClassByClassId(classId, mockClassDto));

            //then
            Mockito.verify(classRepository, never()).save(any(Class.class));

        } */


        @DisplayName("test -get classrecording by batchId")
        @SneakyThrows
        @Test
        void testGetClassRecordingForBatch() {
            //given

            Integer batchId = 1;

            classList.add(mockClass);
            classList.add(mockClass2);
            classDtoList.add(mockClassDto);
            classDtoList.add(mockClassDto2);
           
            given(classRepository.findByBatchInClass_batchId(batchId)).willReturn(classList);
           
          //when
            List<ClassRecordingDTO> classDtos = classService.getClassesRecordingByBatchId(batchId);


            //then
            assertThat(classDtos).isNotNull();
            assertThat(classDtos.size()).isGreaterThan(0);
        }
        
        
        
        @DisplayName("test -get classrecording by classid")
        @SneakyThrows
        @Test
        void testgetClassRecordingByClassId() {
            Class class3 = setMockClassAndDto();
    		String pathString  = class3.getClassRecordingPath();
    		
    		given(classRepository.findById(class3.getCsId()))
    		.willReturn(Optional.of(class3));
    		
    		ClassRecordingDTO classRecordingDTO = setMockClassRecordingDto();
    	
    	     //when
    		ClassRecordingDTO class4=  classService.getClassRecordingByClassId(class3.getCsId());
    		//then
    		assertThat(class4).isNotNull();
    		
        }

		private ClassRecordingDTO setMockClassRecordingDto() {
			ClassRecordingDTO mockclassRecordingDTO = new ClassRecordingDTO(14L, "C://");
	        return mockclassRecordingDTO;
		}
        
		
		@DisplayName("test -update classrecording by ClassId")
        @SneakyThrows
        @Test
        void testupdateClassRecordingByClassId() {
			
			Long classId = 7L;

            Class updateClassscheduleClass = mockClass;
            ClassDto updatedClassDto = mockClassDto;
            ClassRecordingDTO classRecordingDTO = setMockClassRecordingDto();
            String classrecordingpath = "c:/RecordingPath";
          
            Class savedClass = mockClass;
      
            given(classRepository.getById(classId)).willReturn(mockClass);
            given(classRepository.findById(classId)).willReturn(Optional.of(mockClass));
            savedClass.setClassRecordingPath(classRecordingDTO.getClassRecordingPath());
            
          
            given(classRepository.save(mockClass)).willReturn(savedClass);
            given(classScheduleMapper.toClassSchdDTO(mockClass)).willReturn(updatedClassDto);


            //when
            ClassDto classDto = classService.updateClassRecordingByClassId(classId, classRecordingDTO);

            //then
            assertThat(classDto).isNotNull();
            assertThat(classDto.getClassRecordingPath()).isEqualTo("c:/RecordingPath");

            //verify
            verify(classRepository).findById(classId);
            verify(classRepository).save(mockClass);
		
		}
		
		
	
		
		 @DisplayName("test - to update class by class Id")
	      @SneakyThrows
	       @Test
	        void testUpdateClassByClassId()   {
			 given(classRepository.findById(mockClassDto.getCsId())).willReturn(Optional.of(mockClass));
			 mockClassDto.setClassComments("new class");
			
			 given(userRepository.findById(mockClassDto.getClassStaffId())).willReturn(Optional.of(mockClass.getStaffInClass()));
			 given(batchRepository.findById(mockClassDto.getBatchId())).willReturn(Optional.of(mockClass.getBatchInClass()));
		        
			 given(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
					(mockClassDto.getClassStaffId(), "R02", "Active"))
					             .willReturn(true);
		     given(classRepository.findByCsIdAndBatchInClass_BatchId(mockClassDto.getCsId(),mockClassDto.getBatchId())).willReturn(Optional.of(mockClass));
			 given(classRepository.save(mockClass)).willReturn(mockClass);
			 given(classScheduleMapper.toClassSchdDTO(mockClass)).willReturn(mockClassDto);

			 //when
			 ClassDto classDto = classService.updateClassByClassId(mockClassDto.getCsId(), mockClassDto);

			 //then
			 assertThat(classDto).isNotNull();
			 assertThat(classDto.getClassComments()).isEqualTo("new class");
			
	         
	         
	   }
    }
}






