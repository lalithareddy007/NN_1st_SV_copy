package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.*;
import lombok.SneakyThrows;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserRoleProgramBatchMapRepositoryTest {

    @Autowired
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

    private UserRoleProgramBatchMap mockUserRoleProgramBatchMap;

    @BeforeEach
    public void setUp() {
        setMockUserRoleProgramBatchMap();
    }

    private void setMockUserRoleProgramBatchMap() {

        User mockUser = new User("U07", "Mary", "Poppins", "Hazel",
                9099145876L, "India", "IST", "www.linkedin.com/MaryP301",
                "BCA", "MCA", "", "H4", Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()));

        Role mockRole = new Role("R03", "User", "", Timestamp.valueOf(LocalDateTime.now()),
                Timestamp.valueOf(LocalDateTime.now()));

        Program mockProgram = new Program(1L, "SDET", "SDET Training",
                "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

        Batch mockBatch = new Batch(1, "SDET01", "SDET BATCH 01", "Active",
                mockProgram,6, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

        mockUserRoleProgramBatchMap = new UserRoleProgramBatchMap(1L, mockUser, mockRole, mockProgram,
                mockBatch, "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

    }

    @DisplayName("test to get UserRoleProgramBatchMap by userId, roleId, programId, batchId")
    @Test
    public void testFindByUser_UserIdAndRoleRoleIdAndAndProgram_ProgramIdAndBatch_BatchId() {

        userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap);

        Optional<UserRoleProgramBatchMap> optionalMap = userRoleProgramBatchMapRepository
                .findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId
                        ("U07", "R03", 1L, 1);

        assertThat(optionalMap).isNotEmpty();
    }

    @DisplayName("test to get UserRoleProgramBatchMap by userId, roleId and status")
    @Test
    public void testFindByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase() {

        userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap);

        Optional<UserRoleProgramBatchMap> optionalMap = userRoleProgramBatchMapRepository
                .findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
                        ("U07", "R03", "active");

        assertThat(optionalMap).isNotEmpty();
    }

}
