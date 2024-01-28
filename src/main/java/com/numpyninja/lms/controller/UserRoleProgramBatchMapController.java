package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.UserRoleProgramBatchDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.ProgramServices;
import com.numpyninja.lms.services.UserRoleProgramBatchMapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userRoleProgramBatchMap")
@Api(tags="User Role Program Batch Map Controller")
public class UserRoleProgramBatchMapController {

    @Autowired
    private UserRoleProgramBatchMapService userRoleProgramBatchMapService;


    @GetMapping("")
    @ApiOperation("Get Assigned Program/Batch(es) of All Users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserRoleProgramBatchMapDto>> getAll() {
        return ResponseEntity.ok(userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps());
    }


    @GetMapping("/{userId}")
    @ApiOperation("Get Assigned Program/Batch of a User By User Id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserRoleProgramBatchMapDto>> getById(@PathVariable(value="userId") String userId) throws InvalidDataException
    {
        return ResponseEntity.ok(userRoleProgramBatchMapService.getByUserId(userId));
    }



    @DeleteMapping("/deleteAll/{userId}")
    @ApiOperation("Delete All Programs/Batches assigned to the User By UserId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> deleteAllProgramBatchesAssignedToAUser(@PathVariable(value = "userId") String userId) throws ResourceNotFoundException
    {
        userRoleProgramBatchMapService.deleteAllByUserId(userId);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Deleted All Programs/Batches assigned to User ID",true),HttpStatus.OK);

    }




//    @DeleteMapping("/{userId}/{roleId}/{programId}/{batchId}")
//    @ApiOperation("Delete Program/Batch assigned to a User By Id's")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse> deleteProgramBatchAssignedById(@PathVariable(value = "userId") String userId,
//                                             @PathVariable(value = "roleId") String roleId,
//                                             @PathVariable(value = "programId") Long programId,
//                                             @PathVariable(value = "batchId") Integer batchId) throws ResourceNotFoundException {
//        userRoleProgramBatchMapService.deleteById(userId,roleId,programId,batchId);
//        return new ResponseEntity<ApiResponse>(new ApiResponse("Deleted/Unassigned the User Id : " + userId
//                + " with Role Id : " + roleId + " assigned to Program Id : " + programId +
//                " with Batch Id : " + batchId,true),(HttpStatus.OK));
//    }



//    @DeleteMapping("/{userId}")
//    @ApiOperation("Delete Program/Batch assigned to a User By Id")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<ApiResponse> deleteProgramBatchAssignedById(@PathVariable(value = "userId") String userId)
//    {
//        //List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = userRoleProgramBatchMapService.getByUserId(userId);
//         userRoleProgramBatchMapService.deleteById(userId);
//        return new ResponseEntity<ApiResponse>(new ApiResponse("Deleted the program/batch assigned to the User Id", true),HttpStatus.OK);
//    }




}
