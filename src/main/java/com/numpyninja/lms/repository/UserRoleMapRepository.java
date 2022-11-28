package com.numpyninja.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;

@Transactional
@Repository
public interface UserRoleMapRepository  extends JpaRepository <UserRoleMap, Long>{
	List<UserRoleMap> findUserRoleMapsByRoleRoleName( String roleName );
	
	List<UserRoleMap> findUserRoleMapsByBatchesProgramProgramId( Long programId ) ;
	
	List<UserRoleMap> findUserRoleMapsByUserUserId(String userId );
        
	UserRoleMap findUserRoleMapByUserUserIdAndRoleRoleId(String userId ,String RoleId);
	
	@Modifying
	@Query("update UserRoleMap u set u.userRoleStatus = :roleStatusToUpdate, u.lastModTime= CURRENT_TIMESTAMP where u.userRoleId = :userRoleId")
	void updateUserRole(@Param(value = "userRoleId") Long userRoleId, @Param(value = "roleStatusToUpdate") String roleStatusToUpdate);
       
}


