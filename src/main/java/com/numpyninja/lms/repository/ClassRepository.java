package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.ClassEntity;
import com.numpyninja.lms.entity.ProgBatchEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, Long>, JpaSpecificationExecutor<ClassEntity> {

    List<ClassEntity> findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(String classTopic);

}
