package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.ProgBatchEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgBatchRepository extends JpaRepository<ProgBatchEntity, Integer>, JpaSpecificationExecutor<ProgBatchEntity> {
    static Specification<ProgBatchEntity> hasProgramId(Long programId) {
        return (programBatchEntity, cq, cb) -> cb.equal(programBatchEntity.get("batchProgramId"), programId);
    }

    Optional<ProgBatchEntity> findByBatchName(String programName);

    List<ProgBatchEntity> findAll(Specification<ProgBatchEntity> hasProgramId);

    List<ProgBatchEntity> findByBatchNameContainingIgnoreCaseOrderByBatchIdAsc(String batchName);


}
