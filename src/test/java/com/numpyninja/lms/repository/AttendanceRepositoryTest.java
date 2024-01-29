package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AttendanceRepositoryTest {

	
	@Autowired
	private AttendanceRepository attendanceRepository;
	
	private Attendance mockAttendance;
	
	@BeforeEach
	public void setup() {
		mockAttendance = setMockAttendance();
	}
	
	
	private Attendance setMockAttendance() {
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
		User user = new User("U02", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
				"", "", "", "Citizen", timestamp, timestamp);
		Class class1 = new Class ((long) 7, batch, 4, dueDate,
                "Selenium","Active", user, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/Recordings", timestamp, timestamp); 
		Attendance attendance = new Attendance(6L, class1, user, "Present", timestamp, timestamp, java.time.LocalDate.now());
		return attendance;
	}
	
	
	private Class setMockClass() {
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		Date classDate = null ;;
		User user = new User("U01", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
				"", "", "", "Citizen", timestamp, timestamp);
		
		 Program program = new Program((long) 7, "Django", "new Prog", "nonActive", timestamp, timestamp);
		Batch batchInClass = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program, 5, timestamp, timestamp);
		Class class2 =  new Class((long) 7, batchInClass, 1, classDate,
                "Selenium","Active", user, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath", timestamp, timestamp);
        return class2;
	}
	
	
	
	
	@Test
	void testFindByuser_userId() {
		String studentId= "U02";
		//given
		attendanceRepository.save(mockAttendance);
				
		//when
	    List<Attendance> attendance = this.attendanceRepository.findByuser_userId(studentId);				
				
	    //then
				assertThat(attendance).isNotNull();
				assertThat(attendance.size()).isGreaterThan(0);
	}

	@Test
	void testFindByobjClass_csId() {
		//given
		attendanceRepository.save(mockAttendance);
		long classid=7L;
				
		//when
		List<Attendance> attendance = attendanceRepository.findByobjClass_csId(classid);
				
		//then
				assertThat(attendance).isNotNull();
				assertThat(attendance.size()).isGreaterThan(0);
	}

	@Test
	void testFindByobjClass_csIdIn() {
		List<Long> csIds = new ArrayList<Long>();
		
		//given
		attendanceRepository.save(mockAttendance);
		
		//when
		List<Attendance> attendance = attendanceRepository.findByobjClass_csIdIn(csIds);
		
		//then
		assertThat(attendance).isNotNull();
		
	}
}


