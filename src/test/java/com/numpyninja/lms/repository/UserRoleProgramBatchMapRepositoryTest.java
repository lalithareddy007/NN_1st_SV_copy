package com.numpyninja.lms.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRoleProgramBatchMapRepositoryTest {

    @Autowired
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;


    @DisplayName("test to get UserRoleProgramBatchMap by userId, roleId, programId, batchId")
    @Test
    void testFindByUser_UserIdAndRoleRoleIdAndAndProgram_ProgramIdAndBatch_BatchId() {

        var userRoleProgramBatchMap = userRoleProgramBatchMapRepository.findAll().get(0);
        var userId = userRoleProgramBatchMap.getUser().getUserId();
        var roleId = userRoleProgramBatchMap.getRole().getRoleId();
        var programId = userRoleProgramBatchMap.getProgram().getProgramId();
        var batchId = userRoleProgramBatchMap.getBatch().getBatchId();
        var optionalMap = userRoleProgramBatchMapRepository
                .findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId
                        (userId, roleId, programId, batchId);

        assertThat(optionalMap).isNotEmpty();
    }

    @DisplayName("test to get UserRoleProgramBatchMap by userId, roleId and status")
    @Test
    void testFindByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase() {

        var userRoleProgramBatchMap = userRoleProgramBatchMapRepository.findAll().get(0);
        var userId = userRoleProgramBatchMap.getUser().getUserId();
        var roleId = userRoleProgramBatchMap.getRole().getRoleId();
        var status = userRoleProgramBatchMap.getUserRoleProgramBatchStatus();

        var optionalMap = userRoleProgramBatchMapRepository
                .findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
                        (userId, roleId, status);

        assertThat(optionalMap).isNotEmpty();
    }

}
