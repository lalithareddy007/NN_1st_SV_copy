package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.AttendanceEntity;
import com.numpyninja.lms.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long>, JpaSpecificationExecutor<AttendanceEntity> {

    List<AttendanceEntity> findByClassId(Integer classTopic);

}
