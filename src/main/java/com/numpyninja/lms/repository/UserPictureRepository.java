package com.numpyninja.lms.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;

@Repository
public interface UserPictureRepository extends JpaRepository<UserPictureEntity,Long> {

//		@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
//			 + " and t.user_file_type = ?2 ", nativeQuery = true)
//	UserPictureEntity findByuserAnduserFileType( User user_id,String user_file_type);

	// @Modifying
	 //@Transactional
	@Query(value= "delete from  tbl_lms_user_files t WHERE  t.user_id = ?1 "
			 + " and t.user_file_type = ?2 ", nativeQuery = true)

	public UserPictureEntity deleteByuserAnduserFiletype(User userid, String filetype);

	
	@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
			 + " and t.user_file_type = ?2 ", nativeQuery = true)
	public UserPictureEntity findByuserAnduserFileType1(String userId, String userFileType);


	//UserPictureEntity findByuserAnduserFileType2(User user, String filetype);


	
	
	
//	@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
//			 + " and t.user_file_type = ?2 ", nativeQuery = true)
//	UserPictureEntity getByuserAndFileType2(String userId, String userFileType);

}