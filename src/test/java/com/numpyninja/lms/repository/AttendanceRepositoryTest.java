package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class AttendanceRepositoryTest {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void testFindByuser_userId() {

        var studentId = attendanceRepository.findAll().get(0).getUser().getUserId();

        //when
        List<Attendance> attendance = this.attendanceRepository.findByuser_userId(studentId);

        //then
        assertThat(attendance).isNotNull();
        assertThat(attendance.size()).isGreaterThan(0);
    }

    @Test
    void testFindByobjClass_csId() {

        long classid = attendanceRepository.findAll().get(0).getObjClass().getCsId();

        //when
        List<Attendance> attendance = attendanceRepository.findByobjClass_csId(classid);

        //then
        assertThat(attendance).isNotNull();
        assertThat(attendance.size()).isGreaterThan(0);
    }

    @Test
    void testFindByobjClass_csIdIn() {
        List<Long> csIds = new ArrayList<Long>();
        log.info(csIds.toString());

        //when
        List<Attendance> attendance = attendanceRepository.findByobjClass_csIdIn(csIds);
        log.info(attendance.toString());
        //then
        assertThat(attendance).isNotNull();

    }
}


