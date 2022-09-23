package com.numpyninja.lms.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.entity.Attendance;



@Mapper(componentModel = "spring", uses={ClassScheduleMapper.class, UserMapper.class})
public interface AttendanceMapper {

	AttendanceMapper INSTANCE = Mappers.getMapper(AttendanceMapper.class);

	@Mapping(source="attendance.objClass.csId", target="csId")
	@Mapping(source="attendance.user.userId", target="studentId")
	AttendanceDto toAttendanceDto(Attendance attendance);
	
	@Mapping ( source = "attendanceDto.csId", target = "objClass.csId" )
	@Mapping ( source = "attendanceDto.studentId", target = "user.userId" )
	Attendance toAttendance(AttendanceDto attendanceDto);
	
	List<AttendanceDto> toAttendanceDtoList(List<Attendance> attendances);
	 
	List<Attendance> toAttendanceList(List<AttendanceDto> AttendanceDtos);
}
