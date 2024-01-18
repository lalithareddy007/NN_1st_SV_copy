package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.entity.Class;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ClassRepositoryTest {

    @Autowired
    private ClassRepository classRepository;

    private Class mockClass;

    @BeforeEach
    public void setup() {
        mockClass =setMockClass();
    }

    private Class setMockClass() {
        String sDate = "05/25/2022";
        Date classDate = null;
        try {
            classDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        User staffInClass = new User("U01", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
                "", "", "", "Citizen", timestamp, timestamp);

        Program program = new Program((long) 7, "Django", "new Prog", "nonActive", timestamp, timestamp);
        Batch batchInClass = new Batch(35, "SDET 1", "SDET Batch 1", "Active", program, 5, timestamp, timestamp);


        Class aClass = new Class((long) 1, batchInClass, 1, classDate,
                "Selenium","Active", staffInClass, "Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath", timestamp, timestamp);


        return aClass;

    }
    @DisplayName("test -  get class by classTopic")
    @Test
    public void testFindByClassTopic() {

        // given

        classRepository.save(mockClass);

        //When
        List<Class> lists = classRepository.findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(mockClass.getClassTopic());

        //then

        assertThat(lists).isNotNull();
        assertThat(lists.
                size()).isGreaterThan(0);
    }


    @DisplayName("test - get class by Class Id and Batch Id ")
    @Test
    public void testFindByClassIdAndBatchId(){

        // given
        classRepository.save(mockClass);
        //When
        List<Class> lists = classRepository.findByClassIdAndBatchId(mockClass.getCsId(),mockClass.getBatchInClass().getBatchId());

        //then
        assertThat(lists).isNotNull();



    }

    @DisplayName("test - get class by Batch Id ")
    @Test
    public void testFindByBatchInClass_batchId(){
        //given

        classRepository.save(mockClass);
        //when
        List<Class> lists = classRepository.findByBatchInClass_batchId(mockClass.getBatchInClass().getBatchId());
        //then
        assertThat(lists).isNotNull();
        assertThat(lists.size()).
                isGreaterThan(0);

    }
    @DisplayName("test - get staff in class by User Id ")
    @Test
    public void testFindByStaffInClass_userId(){
// given
        classRepository.save(mockClass);
        //when
        List<Class> lists = classRepository.findBystaffInClass_userId(mockClass.getStaffInClass().getUserId());

        //then
        assertThat(lists).isNotNull();
        assertThat(lists.size()).isGreaterThan(0);

    }


}

