package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleProgramBatchMapRepository extends JpaRepository<UserRoleProgramBatchMap, Long> {

    Optional<UserRoleProgramBatchMap> findUserRoleProgramBatchMapByUser_UserIdAndProgram_ProgramIdAndBatch_BatchId
            (String userId, Long programId, Integer batchId);

}
