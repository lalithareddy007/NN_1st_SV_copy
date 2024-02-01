package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.mappers.AttendanceMapper;
import com.numpyninja.lms.mappers.ClassScheduleMapper;
import com.numpyninja.lms.repository.AttendanceRepository;
import com.numpyninja.lms.repository.ClassRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.UserRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;




@ExtendWith(MockitoExtension.class)
class AttendanceServicesTest {

	
	@Mock
	private AttendanceRepository attendanceRepository;
	
	@Mock
	private ClassRepository classRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private ProgBatchRepository batchRepository;
	
	@InjectMocks
	private AttendanceServices attendanceService;
	
	@Mock
	private AttendanceMapper attendanceMapper;
	
	@Mock
	private ClassScheduleMapper classMapper;
	
	@Mock
	private Attendance mockAttendance ,mockAttendance1;
	
	@Mock
	private AttendanceDto mockAttendanceDto,mockAttendanceDto1;
	
	private static Class class4;

	private static User user4;
	
	
	@BeforeEach
	public void setup() {
		mockAttendance = setMockAttendanceAndDto();
		//mockAttendance1=setMockAttendanceAndDto1();
	}
	
	
	private Attendance setMockAttendanceAndDto() {	
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
		Program program = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		Batch batch = new Batch(3, "02", "SDET BATCH 02", "Active", program, 6, timestamp, timestamp);
		User user = new User("U03", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
				"", "", "", "Citizen", timestamp, timestamp);
		Class class2 = new Class ((long) 7, batch, 4, dueDate,
                "Selenium", "Active",user, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/Recordings", timestamp, timestamp);
		 mockAttendance = new Attendance(7L, class2, user, "Present", timestamp, timestamp, java.time.LocalDate.now());
		 mockAttendanceDto = new AttendanceDto (7L,7L,"U03","Present", timestamp, timestamp, java.time.LocalDate.now());
		return mockAttendance;		
	}
	
	private Class setMockClass() {
		Date dueDate = null;
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Program program = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		Batch batch = new Batch(3, "02", "SDET BATCH 02", "Active", program, 6, timestamp, timestamp);
		
		User user = new User("U03", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
				"", "", "", "Citizen", timestamp, timestamp);
		Class class2 = new Class ((long) 7, batch, 4, dueDate,
                "Selenium7","Active", user, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/Recordings", timestamp, timestamp);
        return class2;
	}

	
	
	@DisplayName("test for getting all the attendance")
	@Test
	void testGetAllAttendances() {
		//given
				Attendance mockAttendance2 = setMockAttendanceAndDto();
				mockAttendance2.setAttId(7L);
				mockAttendance2.setAttendance("Present");
				AttendanceDto mockAttendancetDto2 = mockAttendanceDto;
				mockAttendancetDto2.setAttId(7L);
				mockAttendancetDto2.setAttendance("Present");
				List<Attendance> attendanceList = new ArrayList<Attendance>();
				attendanceList.add(mockAttendance);
				attendanceList.add(mockAttendance2);
				List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
				attendanceDtoList.add(mockAttendanceDto);
				attendanceDtoList.add(mockAttendancetDto2);
				given(attendanceRepository.findAll()).willReturn(attendanceList);
				
				given(attendanceMapper.toAttendanceDtoList(attendanceList)).willReturn(attendanceDtoList);
				
				
				//when
				List<AttendanceDto> attendanceDtos = attendanceService.getAllAttendances();
						
				//then
				assertThat(attendanceDtos).isNotNull();
				assertThat(attendanceDtos.size()).isEqualTo(2);
	}

	
	
	
	@DisplayName("test for getting attendance by Id")
	@Test
	void testGetAttendanceById() {
		//given
				given(attendanceRepository.findById(mockAttendance.getAttId())).willReturn(Optional.of(mockAttendance));
				given(attendanceMapper.toAttendanceDto(mockAttendance)).willReturn(mockAttendanceDto);
				
		//when
				AttendanceDto	attendanceDto =	attendanceService.getAttendanceById(mockAttendance.getAttId());
		//then
				assertThat(attendanceDto).isNotNull();
	}
    
	
	
	
	@DisplayName("test for getting attendance by StudentId")
	@Test
	void testGetAttendanceForStudent() {
		
		User user = setMockUser();
		given(userRepository.findById(user.getUserId())).willReturn(Optional.of(user));
		Attendance mockAttendance2 = setMockAttendanceAndDto();
		mockAttendance2.setUser(user);
		mockAttendance2.setAttendance("Present");
		AttendanceDto mockAttendancetDto2 = mockAttendanceDto;
		mockAttendancetDto2.setStudentId("U02");
		mockAttendancetDto2.setAttendance("Present");
		List<Attendance> attendanceList = new ArrayList<Attendance>();
		attendanceList.add(mockAttendance);
		attendanceList.add(mockAttendance2);
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		attendanceDtoList.add(mockAttendanceDto);
		attendanceDtoList.add(mockAttendancetDto2);
		
		given(attendanceRepository.findByuser_userId(user.getUserId())).willReturn(attendanceList);
		
		given(attendanceMapper.toAttendanceDtoList(attendanceList)).willReturn(attendanceDtoList);
		
		
		//when
		List<AttendanceDto> attendanceDtos = attendanceService.getAttendanceForStudent(user.getUserId());
				
		//then
		assertThat(attendanceDtos).isNotNull();
		assertThat(attendanceDtos.size()).isEqualTo(2);
		
	}

	private User setMockUser() {
		Date dueDate= null;
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Program program = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		Batch batch = new Batch(3, "02", "SDET BATCH 02", "Active", program, 6, timestamp, timestamp);
		User user = new User("U03", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
				"", "", "", "Citizen", timestamp, timestamp);
		Class class2 = new Class ((long) 7, batch, 4, dueDate,
                "Selenium","Active", user, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/Recordings", timestamp, timestamp);
        return user;
	}

	
	@DisplayName("test for getting attendance by ClassId")
	@Test
	void testGetAttendanceByClass() {
		
		Class class3 = setMockClass();
		given(classRepository.findById(class3.getCsId())).willReturn(Optional.of(class3));
		
		Attendance mockAttendance2 = setMockAttendanceAndDto();
		mockAttendance2.setObjClass(class3);
		mockAttendance2.setAttendance("Present");
		AttendanceDto mockAttendancetDto2 = mockAttendanceDto;
		mockAttendancetDto2.setCsId(class3.getCsId());
		mockAttendancetDto2.setAttendance("Present");
		List<Attendance> attendanceList = new ArrayList<Attendance>();
		attendanceList.add(mockAttendance);
		attendanceList.add(mockAttendance2);
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		attendanceDtoList.add(mockAttendanceDto);
		attendanceDtoList.add(mockAttendancetDto2);
		
		
		given(attendanceRepository.findByobjClass_csId(class3.getCsId())).willReturn(attendanceList);
		
		given(attendanceMapper.toAttendanceDtoList(attendanceList)).willReturn(attendanceDtoList);
		
		//when
		
		List<AttendanceDto> attendanceDtos = attendanceService.getAttendanceByClass(class3.getCsId());
		//then
		assertThat(attendanceDtos).isNotNull();
		assertThat(attendanceDtos.size()).isEqualTo(2);
		
	}
		
		
	
	@DisplayName("test for getting attendance by BatchId")
	@Test
	
	void testGetAttendanceByBatch() {

		List<Long> csIds = new ArrayList<Long>();
		Batch batch = setMockBatch();
		Attendance mockAttendance2 = setMockAttendanceAndDto();
		mockAttendance2.setAttId(7L);
		mockAttendance2.setAttendance("Present");
		AttendanceDto mockAttendancetDto2 = mockAttendanceDto;
		mockAttendancetDto2.setAttId(7L);;
		mockAttendancetDto2.setAttendance("Present");
		List<Attendance> attendanceList = new ArrayList<Attendance>();
		attendanceList.add(mockAttendance);
		attendanceList.add(mockAttendance2);
		List<AttendanceDto> attendanceDtoList = new ArrayList<AttendanceDto>();
		attendanceDtoList.add(mockAttendanceDto);
		attendanceDtoList.add(mockAttendancetDto2);
		
		given(attendanceRepository.findByobjClass_csIdIn(csIds)).willReturn(attendanceList);
		given(attendanceMapper.toAttendanceDtoList(attendanceList)).willReturn(attendanceDtoList);
		
		//when
		List<AttendanceDto> attendanceDtos = attendanceService.getAttendanceByBatch(batch.getBatchId());
		
		//then
		assertThat(attendanceDtos).isNotNull();
		assertThat(attendanceDtos.size()).isEqualTo(2);	
		
	}

	
	
	private Batch setMockBatch() {
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Program program = new Program((long) 1,"SDET"," ", "Active",timestamp, timestamp);
		Batch batch = new Batch(3, "02", "SDET BATCH 02", "Active", program, 6, timestamp, timestamp);
		return batch;
	}

	
	@DisplayName("Delete attendance by AttendanceId")
	@Test
	void testDeleteAttendance() {
		
		        //given
				given(attendanceRepository.findById(mockAttendance.getAttId())).willReturn(Optional.of(mockAttendance));
				willDoNothing().given(attendanceRepository).deleteById(mockAttendance.getAttId());
				
				//when
				attendanceService.deleteAttendance(mockAttendance.getAttId());
				
				//then
				verify(attendanceRepository, times(1)).deleteById(mockAttendance.getAttId());
		
	}
     
	
	@DisplayName("Create new attendance under class ")
	@Test
	void testCreateAttendance() throws Exception {
			
       Class class2= setMockClass();
        User user= setMockUser();
        when(attendanceMapper.toAttendance(mockAttendanceDto)).thenReturn(mockAttendance);
        
        when(classRepository.findById(mockAttendance.getObjClass().getCsId())).thenReturn(Optional.of(class2));
        when(userRepository.findById(mockAttendance.getUser().getUserId())).thenReturn(Optional.of(user));
        
       
        when(attendanceRepository.save(mockAttendance)).thenReturn(mockAttendance);
        when(attendanceMapper.toAttendanceDto(mockAttendance)).thenReturn(mockAttendanceDto);

        //when
        AttendanceDto attendanceDto = attendanceService.createAttendance(mockAttendanceDto);

        //then
        AssertionsForClassTypes.assertThat(attendanceDto).isNotNull();
		
		
	}
	
	
	
	@DisplayName("Update attendance under class ")
	@Test
	public void testUpdateAttendance() throws Exception{
		Class class2= setMockClass();
        User user= setMockUser();
        when(attendanceMapper.toAttendance(mockAttendanceDto)).thenReturn(mockAttendance);
        
        when(classRepository.findById(mockAttendance.getObjClass().getCsId())).thenReturn(Optional.of(class2));
        when(userRepository.findById(mockAttendance.getUser().getUserId())).thenReturn(Optional.of(user));
        
        given(attendanceRepository.findById(mockAttendance.getAttId())).willReturn(Optional.of(mockAttendance));
		mockAttendanceDto.setAttendance("Present");
       
		//when
        when(attendanceRepository.save(mockAttendance)).thenReturn(mockAttendance);
        when(attendanceMapper.toAttendanceDto(mockAttendance)).thenReturn(mockAttendanceDto);

        AttendanceDto attendanceDto = attendanceService.updateAttendance(mockAttendanceDto, mockAttendanceDto.getAttId());
		
        //then
        assertThat(attendanceDto).isNotNull();
        assertThat(attendanceDto.getAttendance()).isEqualTo("Present");		
		
	}


}
