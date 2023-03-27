package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AttendanceMapper;
import com.numpyninja.lms.repository.AttendanceRepository;
import com.numpyninja.lms.repository.ClassRepository;
import com.numpyninja.lms.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServices {
	@Autowired
	private AttendanceRepository attendanceRepository;
	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AttendanceMapper attendanceMapper;

	// get all Attendances
	public List<AttendanceDto> getAllAttendances() {
		return attendanceMapper.toAttendanceDtoList(attendanceRepository.findAll());
	}

	// get Attendance by id
	public AttendanceDto getAttendanceById(Long id) {
		Attendance attendance = this.attendanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance", "Id", id));
		return attendanceMapper.toAttendanceDto(attendance);
	}

	// get Attendance by Student id
	public List<AttendanceDto> getAttendanceForStudent(String studentId) {
		this.userRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student Id " + studentId + " not Found!!"));
		// List<Attendance> attendance = this.attendanceRepository.findByUser(user);
		List<Attendance> attendance = this.attendanceRepository.findByuser_userId(studentId);
		List<AttendanceDto> attendanceDtos = attendanceMapper.toAttendanceDtoList(attendance);
		return attendanceDtos;
	}

	// get Attendance by Class id
	public List<AttendanceDto> getAttendanceByClass(Long csId) {
		this.classRepository.findById(csId)
				.orElseThrow(() -> new ResourceNotFoundException("Class Id " + csId + " not Found!!"));
		// List<Attendance> attendance = this.attendanceRepository.findByUser(user);
		List<Attendance> attendance = this.attendanceRepository.findByobjClass_csId(csId);
		List<AttendanceDto> attendanceDtos = attendanceMapper.toAttendanceDtoList(attendance);
		return attendanceDtos;
	}

	// get Attendance by Batch id
	public List<AttendanceDto> getAttendanceByBatch(Integer batchId) {
		List<Class> Classes = this.classRepository.findByBatchInClass_batchId(batchId);
					
		List<Attendance> attendance = null;
		List<Long> csIds = new ArrayList<Long>();

		for (int i = 0; i <= Classes.size() - 1; i++) {
			csIds.add(i, Classes.get(i).getCsId());
		}

		attendance = this.attendanceRepository.findByobjClass_csIdIn(csIds);
		if (attendance.isEmpty())
			throw new ResourceNotFoundException("Attendance", "Batch Id", batchId);
		List<AttendanceDto> attendanceDtos = attendanceMapper.toAttendanceDtoList(attendance);
		return attendanceDtos;
	}

	// Delete Attendance By Id
	public void deleteAttendance(Long AttId) {
		attendanceRepository.findById(AttId)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance", "Id", AttId));
		attendanceRepository.deleteById(AttId);
	}

	// create new Attendance under class
	public AttendanceDto createAttendance(AttendanceDto attendanceDto) {
		Long classId = attendanceDto.getCsId();
		String studentId = attendanceDto.getStudentId();
		
		Class objClass = classRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("Class", "Id", classId));
		User user = userRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student Id " + studentId + " not found"));

		List<Attendance> attendanceRecord = this.attendanceRepository.findByObjClassAndUser(objClass, user);

		 // Check if an attendance record already exists for the given classId and studentId
		 if(!attendanceRecord.isEmpty() ) {
		  throw new DuplicateResourceFoundException("Attendance record already exists for class " + classId + " and student " + studentId);
		 }
		
		Attendance attendance = attendanceMapper.toAttendance(attendanceDto);
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		attendance.setCreationTime(timestamp);
		attendance.setLastModTime(timestamp);

		Attendance newAttendance = attendanceRepository.save(attendance);
		return attendanceMapper.toAttendanceDto(newAttendance);
	}

	// Update new Attendance under class
	public AttendanceDto updateAttendance(AttendanceDto attendanceDto, Long attendanceId) {
		Long classId = attendanceDto.getCsId();
		String studentId = attendanceDto.getStudentId();
		Class objClass = classRepository.findById(classId)
				.orElseThrow(() -> new ResourceNotFoundException("Class", "Id", classId));
		User user = userRepository.findById(studentId)
				.orElseThrow(() -> new ResourceNotFoundException("Student Id " + studentId + " not found"));
		Attendance attendanceById = this.attendanceRepository.findById(attendanceId)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance", "Id", attendanceId));
        Attendance updateAttendance = attendanceMapper.toAttendance(attendanceDto);

		updateAttendance.setAttId(attendanceId);
		updateAttendance.setObjClass(objClass);
		updateAttendance.setUser(user);
		updateAttendance.setCreationTime(attendanceById.getCreationTime());
		updateAttendance.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
		Attendance updatedAttendance = this.attendanceRepository.save(updateAttendance);
		AttendanceDto updatedAttendanceDto = attendanceMapper.toAttendanceDto(updatedAttendance);
		return updatedAttendanceDto;
	}

}
