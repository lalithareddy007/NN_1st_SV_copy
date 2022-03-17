package com.numpyninja.lms.services;

import com.numpyninja.lms.entity.AttendanceEntity;
import com.numpyninja.lms.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class AttendanceServices {
    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<AttendanceEntity> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public List<AttendanceEntity> getAllAttendances(String searchString) {
        return attendanceRepository.findByClassId(Integer.parseInt(searchString));
    }

    public Optional<AttendanceEntity> findAttendance(Long id) {
        return attendanceRepository.findById(id);
    }

    public AttendanceEntity createAttendance(AttendanceEntity newAttendance) {
        return attendanceRepository.saveAndFlush(newAttendance);
    }

    public AttendanceEntity updateAttendance(AttendanceEntity updatedAttendance) {
        return attendanceRepository.save(updatedAttendance);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

}
