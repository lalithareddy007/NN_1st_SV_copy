package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.services.UserSkillService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/userSkill")
@Api(tags="User Skill Controller", description="User Skill CRUD Operations")
public class UserSkillController {
    private UserSkillMapper userSkillMapper;
    private UserSkillService userSkillService;
    public UserSkillController( UserSkillMapper userSkillMapper, UserSkillService userSkillService){
        this.userSkillMapper=userSkillMapper;
        this.userSkillService=userSkillService;

    }
    @PostMapping("/create")
    @ApiOperation("Create New User Skill")
        public ResponseEntity<UserSkillDTO> createUserSkill(@RequestBody  UserSkillDTO newuserskillDTO ) throws DuplicateResourceFoundException {
            UserSkillDTO responseDto = this.userSkillService.createUserSkill(newuserskillDTO);
            return new ResponseEntity<>(responseDto,HttpStatus.CREATED);
    }

    @GetMapping()
    @ApiOperation("Get All User Skill")
    public List<UserSkillDTO> getAllUserSkill(){

        return userSkillService.getAllUserSkills();
    }

    @GetMapping("/user/{userId}")
    @ApiOperation("Get User Skill for User Id")
    public ResponseEntity<List<UserSkillDTO>> getUserSkillForUser(@PathVariable(value = "userId") String userId){
        return ResponseEntity.ok(this.userSkillService.getUserSkillForUser(userId));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update User Skill")
    public  ResponseEntity<UserSkillDTO>  updateUserSkill(@RequestBody UserSkillDTO userSkillDTO, @PathVariable String id){
     UserSkillDTO updateUserSkillDTO =this.userSkillService.updateUserSkill(userSkillDTO,id);
     return ResponseEntity.ok(updateUserSkillDTO);
    }


   /* @DeleteMapping(path="/deleteByUser/{id}")
    public ResponseEntity<ApiResponse> deleteUserByUserId(@PathVariable String id) {
        this.userSkillService.deleteUserByUserId(id);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Users deleted successfully", true), HttpStatus.OK);
    }*/
    @DeleteMapping(path="/deleteByUserSkillId/{id}")
    @ApiOperation("Delete User Skill by User Skill ID")
    public ResponseEntity< ApiResponse>deleteUserSkillByUserSkillId(@PathVariable String id){
        this.userSkillService.deleteUserSkillByUserSkillId(id);
        return new ResponseEntity<ApiResponse>(new ApiResponse("UserSkill deleted successfully", true), HttpStatus.OK);


    }


}
