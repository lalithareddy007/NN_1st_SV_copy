package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.SkillMaster;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class SkillMasterRepositoryTest {

    @Autowired
    private SkillMasterRepository skillMasterRepository;

    private SkillMaster mockSkillMaster;

    @BeforeEach
    public void setUp() {
        setMockSkillMaster();
    }

    private void setMockSkillMaster() {
        mockSkillMaster = new SkillMaster(1L, "Java Basics", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
    }

    @DisplayName("test - FindBySkillName - Get Skills By SkillName")
    @SneakyThrows
    @Test
    public void testFindBySkillName() {
        //given
        skillMasterRepository.save(mockSkillMaster);

        //when
        List<SkillMaster> skillMasterList = skillMasterRepository.findBySkillName(mockSkillMaster.getSkillName());

        //then
        assertThat(skillMasterList).isNotNull();
        assertThat(skillMasterList.size()).isGreaterThan(0);
    }
}
