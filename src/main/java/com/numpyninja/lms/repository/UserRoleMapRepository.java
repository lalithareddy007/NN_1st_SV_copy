package com.numpyninja.lms.repository;

import java.util.List;

//import javax.persistence.EntityManager;

import com.numpyninja.lms.entity.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.numpyninja.lms.dto.UserCountByStatusDTO;
import com.numpyninja.lms.entity.UserRoleMap;

@Transactional
@Repository
public interface UserRoleMapRepository  extends JpaRepository <UserRoleMap, Long>{
	
	List<UserRoleMap> findUserRoleMapsByRoleRoleName( String roleName );

	//List<UserRoleMap> findUserRoleMapsByBatchesProgramProgramId( Long programId ) ;

	List<UserRoleMap> findUserRoleMapsByUserUserId(String userId );

	UserRoleMap findUserRoleMapByUserUserIdAndRoleRoleId(String userId ,String RoleId);

	List<UserRoleMap> findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase
			(String userId, String roleId, String userRoleStatus);

	boolean existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
	(String userId, String roleId, String userRoleStatus);

	
	@Modifying
	@Query("update UserRoleMap u set u.userRoleStatus = :roleStatusToUpdate, u.lastModTime= CURRENT_TIMESTAMP where u.userRoleId = :userRoleId")
	void updateUserRole(@Param(value = "userRoleId") Long userRoleId, @Param(value = "roleStatusToUpdate") String roleStatusToUpdate);
	

	@Modifying
	@Query("update UserRoleMap u set u.role.roleId = :roleIdToUpdate, u.lastModTime= CURRENT_TIMESTAMP where u.userRoleId = :userRoleId")
	void updateRoleId(@Param(value = "userRoleId") Long userRoleId, @Param(value = "roleIdToUpdate") String roleIdToUpdate);
	
	

	@Query("SELECT new com.numpyninja.lms.dto.UserCountByStatusDTO(u.userRoleStatus, COUNT(u.userRoleId)) "
			+ "FROM UserRoleMap AS u "
			+ "GROUP BY u.userRoleStatus")
	List<UserCountByStatusDTO> getUsersCountByStatus();


	@Query("SELECT new com.numpyninja.lms.dto.UserCountByStatusDTO(u.userRoleStatus, COUNT(u.userRoleId)) "
			+ "FROM UserRoleMap AS u "
			+ "WHERE u.role.roleId=:roleId "
			+ "GROUP BY u.userRoleStatus")
	List<UserCountByStatusDTO> getUsersCountByStatusByRole(String roleId);

	@Query("Select u FROM UserRoleMap u WHERE u.userRoleStatus=:userRoleStatus")
    List<UserRoleMap> findByUserRoleStatus(@Param(value="userRoleStatus") String userRoleStatus);
}


