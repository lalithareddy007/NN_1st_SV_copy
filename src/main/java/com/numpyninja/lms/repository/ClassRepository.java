package com.numpyninja.lms.repository;


import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.entity.Batch;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class,Long>{

    Optional<Class> findById(Long id);

    List<Class> findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(String classTopic);
    
    @Query(value = "SELECT * FROM tbl_lms_class_sch WHERE cs_id = ?1 and batch_id = ?2", nativeQuery = true)
	List<Class> findByClassIdAndBatchId ( Long csId, Integer batchIdClass);
    
    //@Param("BatchInClass.batchId")
    @Query(value = "SELECT * FROM tbl_lms_class_sch WHERE batch_id = ?1", nativeQuery = true)
    List<Class> findByBatchInClass_batchId( Integer batchId);
    
    //@Query(value = "SELECT * FROM ClassSchedule WHERE staffInClass.userId = ?1")
    List<Class> findBystaffInClass_userId(String staffId);

    
    Optional<Class> findByCsIdAndBatchInClass_BatchId(Long csId, Integer batchid);
    
    
    @Query(value = "SELECT * FROM tbl_lms_class_sch WHERE class_recording_path = ?1", nativeQuery = true)
    List<ClassRecordingDTO> findByclassRecordingPath_ClassRecordingDTOInClasses(List<ClassRecordingDTO> class1 );
   // List<Class> findByBatch(Batch batch);
    //List<Class> findByRoles(@Param("roles")List<String> roles);

	Optional<Batch> findByCsIdAndBatchInClass_BatchId(Long id, Batch batchobj);
    
   // List<Class> findByRolesIn(List<String> roles);
    


    

}
