package com.numpyninja.lms.repository;

import java.util.List;

import javax.transaction.Transactional;

import com.numpyninja.lms.entity.UserFileEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.numpyninja.lms.entity.User;

@Repository
public interface UserFileRepository extends JpaRepository<UserFileEntity,Long> {

	List<UserFileEntity> findByUser_UserId(String userId);

//		@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
//			 + " and t.user_file_type = ?2 ", nativeQuery = true)
//	UserFileEntity findByuserAnduserFileType( User user_id,String user_file_type);

	 @Modifying
	 @Transactional
	@Query(value= "delete from  tbl_lms_user_files t WHERE  t.user_id = ?1 "
			 + " and t.user_file_type = ?2 ", nativeQuery = true)

	public void deleteByuserAnduserFiletype(User userid, String filetype);

	
	@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
			 + " and t.user_file_type = ?2 ", nativeQuery = true)
	public UserFileEntity findByuserAnduserFileType(String userId, String userFileType);

//	@Modifying
//	@Transactional
//	@Query(value= "delete from  tbl_lms_user_files t WHERE  t.user_id = ?1 "
//			 + " and t.user_file_type = ?2 ", nativeQuery = true)
//	public void deleteByuserAnduserFiletype(String userid, String filetype);


	//UserFileEntity findByuserAnduserFileType2(User user, String filetype);


	
	
	
//	@Query(value= "SELECT *  FROM  tbl_lms_user_files t WHERE  t.user_id = ?1 "
//			 + " and t.user_file_type = ?2 ", nativeQuery = true)
//	UserFileEntity getByuserAndFileType2(String userId, String userFileType);

}