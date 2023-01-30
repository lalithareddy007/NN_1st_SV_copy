package com.numpyninja.lms.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.dto.UserAndRoleDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserRoleMapSlimDTO;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.exception.DuplicateResourceFound;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.services.UserServices;

@RestController
//@RequestMapping("/users")
public class UserController {	
	
	private UserMapper userMapper;
	private UserServices userServices;
	
	@Autowired
    public UserController(
            UserMapper userMapper,
            UserServices userServices
      
    ) {
        this.userMapper = userMapper;
        this.userServices = userServices;
 
    }

	//get all users from LMS_Users table
	@GetMapping("/users")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		//List<User> userList = userServices.getAllUsers();
		List<UserDto> userList = userServices.getAllUsers();
		return ResponseEntity.ok(userList);  
	}
	

	//get user by ID - information on role, user, batch and details displayed
	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserInfoById(@PathVariable String id) throws ResourceNotFoundException {
		List<?> userInfo = userServices.getUserInfoById(id);
		return ResponseEntity.status(200).body(userInfo);
	}
	
	//Get all users with all their info - Role, status, Program, Batch
	@GetMapping("/users/roles")
    protected List<?> getAllUsersWithRoles() {
    	return userServices.getAllUsersWithRoles() ;
    }
    
    //create user with Role 
    @PostMapping("/users/roleStatus")
    public ResponseEntity<UserDto> createUserWithRole(@Valid @RequestBody UserAndRoleDTO newUserRoleDto) throws InvalidDataException, DuplicateResourceFound {
    	UserDto responseDto = userServices.createUserWithRole(newUserRoleDto);
    	return ResponseEntity.status(HttpStatus.CREATED).body(responseDto); 
    }
    
    //update user info in User Table
    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto updateuserDto, @PathVariable(value="userId") String userId) throws DuplicateResourceFound, ResourceNotFoundException, InvalidDataException {
    	UserDto responseDto = userServices.updateUser(updateuserDto, userId);
    	return ResponseEntity.status(HttpStatus.OK).body(responseDto); 
    }
    
    //Ask front end to include a separate link to update role status for user
    //update User role - (Active/inactive) for a given user id and role id 
    @PutMapping("/users/roleStatus/{userId}")
    public ResponseEntity<String> updateUserRoleStatus(@Valid @PathVariable(value="userId") String userId, @RequestBody UserRoleMapSlimDTO updateUserRoleStatus) throws InvalidDataException {
    		//String UserRole, String UserStatus
    	String responseDto = userServices.updateUserRoleStatus(updateUserRoleStatus,userId);
    	return ResponseEntity.status(HttpStatus.OK).body("UserStatus Updated for User: " +userId); 
    }
    
       
    //cascade deletes users and User roles
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable(value="userId") String userId) throws ResourceNotFoundException{
    	String deletedUserId = userServices.deleteUser(userId);
    	return ResponseEntity.status(HttpStatus.OK).body("Deleted User ID:  "+deletedUserId);
    	//return deletedUserId;
    }
    
    /** Check if the below end points are required or not for the future**/
    
    /*
	//Check if this is needed form front end or not??
	//Get all the users for a given role (Admin,Staff,User)- only giving user table info
    @GetMapping("/users/roles/{rolename}")
    protected List<?> getAllUsersByRole(@PathVariable(value="rolename")String roleName) {
    	return userMapper.userDtos( userServices.getAllUsersByRole(roleName) );
    }
	*/
    /* @GetMapping("/users/roles/{rolename}")
     protected List<?> getAllRoles(@PathVariable(value="rolename")String roleName) {
     	return userMapper.userDtos( userServices.getAllUsersByRole(roleName) );
     }*/
     
    /*
     //To check if this is needed from front end? 
     // Batch value coming empty?? - check this logic
     @GetMapping("/users/programs/{programid}")
     protected List<?> getUsersForProgram(@PathVariable(value="programid")Long programId) {
         return userServices.getUsersForProgram(programId);
     }
     */
 	/*
 	//get user by ID from LMS_Users table	
 	@GetMapping("/users/{id}")
 	public ResponseEntity<UserDto> getAllUsersById(@PathVariable String id) throws ResourceNotFoundException {
 		UserDto userDto = userServices.getAllUsersById(id);
 		return ResponseEntity.status(200).body(userDto);
 	}
 	*/
    
    /*
    //Creates user only but no role added
    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto newuserDto) throws InvalidDataException, DuplicateResourceFound {
    	UserDto responseDto = userServices.createUser(newuserDto);
    	return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);  
    }
    */
    
    /* @PutMapping("/users/roleStatus/{userId}")
    public ResponseEntity<UserDto> updateUserWithRole(@Valid @RequestBody UserAndRoleDTO updateUserRoleDto, @PathVariable(value="userId") String userId) throws DuplicateResourceFound, ResourceNotFoundException, InvalidDataException {
    	UserDto responseDto = userServices.updateUserWithRole(updateUserRoleDto, userId);
    	return ResponseEntity.status(HttpStatus.OK).body(responseDto); 
    }*/
    
}
