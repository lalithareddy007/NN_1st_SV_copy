package com.numpyninja.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.numpyninja.lms.entity.SkillMaster;

@Repository
public interface SkillMasterRepository extends JpaRepository<SkillMaster, Long>{
	@Query(value = "SELECT * FROM tbl_lms_skill_master WHERE skill_name = ?", nativeQuery = true)
	List<SkillMaster> findBySkillName ( String skillName);
}
