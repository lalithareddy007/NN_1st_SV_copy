package com.numpyninja.lms.controller;

import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.ClassService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping
@Api(tags="Class Controller", description="Class CRUD Operations")
public class ClassController {
	
	@Autowired
	ClassService classServices;

	//createClass
	@PostMapping(path="/CreateClassSchedule",consumes = "application/json", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Create new Class")
	private ResponseEntity<ClassDto> createAndSaveClass(@Valid @RequestBody ClassDto classDTO)throws DuplicateResourceFoundException
	{  
		System.out.println("in create a new class schedule");
		ClassDto savedClassDTO = this.classServices.createClass(classDTO);
	return ResponseEntity.status(HttpStatus.CREATED).body(savedClassDTO);	
	} 
	
	//GetAllClasses
	@GetMapping(value = "/allClasses", produces = "application/json")
	@ApiOperation("Get all Class List")
	private ResponseEntity<?> getAllClassesList()  throws ResourceNotFoundException
	{ 
		System.out.println("in getall classes");
		List<ClassDto> ClassesList = classServices.getAllClasses();
		return ResponseEntity.ok(ClassesList);  
	}  
	
	//GetClassesByClassId
	@GetMapping(path="class/{classId}", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Get Class details by ID")
	private ResponseEntity <?> getClassesById(@PathVariable("classId") @NotBlank @Positive Long classId)throws ResourceNotFoundException
	{  
		System.out.println("in get All classes by ClassId");
		ClassDto classesDTOList= classServices.getClassByClassId(classId);
		return ResponseEntity.ok(classesDTOList);
	}  
	
	//GetAllClassesByClassTopic
		@GetMapping(value = "/classes/{classTopic}", produces = "application/json")
		@ApiOperation("Get all Classes by Class Topic ")
		private ResponseEntity<?> getAllClassesByClassTopic(@PathVariable("classTopic") @NotBlank @Positive String classTopic)  throws ResourceNotFoundException
		{ 
			System.out.println("in getall Class By ClassTopic");
			List<ClassDto> ClassesList = classServices.getClassesByClassTopic(classTopic);
			return ResponseEntity.ok(ClassesList);  
		} 
		
		//get all classes by batchId
		@GetMapping(path="classesbyBatch/{batchId}", produces = "application/json")  
		@ResponseBody
		@ApiOperation("Get all Classes by Batch ID")
		private ResponseEntity <?> getClassesByBatchId(@PathVariable("batchId") @NotBlank @Positive Integer batchId)throws ResourceNotFoundException
		{  
			System.out.println("in get All classes by BatchId");
			List<ClassDto> classesDTOList= classServices.getClassesByBatchId(batchId);
			return ResponseEntity.ok(classesDTOList);
		}  
		
		//get all classes by classStaffId
		@GetMapping(path="classesByStaff/{staffId}", produces = "application/json")  
		@ResponseBody
		@ApiOperation("Get all Classes by Staff ID")
		private ResponseEntity <?> getClassesByStaffId(@PathVariable(value="staffId") @NotBlank @Positive String staffId)throws ResourceNotFoundException
		{  
			System.out.println("in get All classes by staffId");
			List<ClassDto> classesDTOList= classServices.getClassesByStaffId(staffId);
			return ResponseEntity.ok(classesDTOList);
		}  
		
		//get all classes by classDate
	      //coming soon
		
		
	//Update Class Schedule by Id
	@PutMapping(path="updateClass/{classId}", consumes = "application/json", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Update Class Schedule by Class ID")
	private ResponseEntity <ClassDto> updateClassScheduleById(@PathVariable @NotBlank @Positive Long classId ,@Valid @RequestBody ClassDto modifyClassSchedule) throws ResourceNotFoundException
	{  
	return ResponseEntity.ok(classServices.updateClassByClassId(classId,modifyClassSchedule));
	} 
	
	//DeleteClassById
	@DeleteMapping(path="deletebyClass/{classId}",produces = "application/json")  
	@ResponseBody
	@ApiOperation("Delete Class by Class ID")
	private ResponseEntity<?>  deleteByClassId(@PathVariable("classId")@NotBlank @Positive Long classId) throws ResourceNotFoundException  
	{  
	System.out.println("in delete by classId controller");
	boolean deleted = classServices.deleteByClassId(classId); 
	if(deleted)
		return ResponseEntity.status(HttpStatus.OK).build();
			else
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}  

	
	////Update Class Recording by ClassId
	@PutMapping(path="updateClassrecording/{classId}", consumes = "application/json", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Update Class Recordings By Class ID")
	private ResponseEntity <ClassDto> updateClassRecordingByClassId(@PathVariable @NotBlank @Positive Long classId ,@Valid @RequestBody ClassRecordingDTO classRecordingDTO) throws ResourceNotFoundException
	{  
	return ResponseEntity.ok(classServices.updateClassRecordingByClassId(classId,classRecordingDTO));
	} 
	
	
	//get class Recording by classId
	
	@GetMapping(path="classrecordings/{classId}", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Get Class Recordings by Class ID")
	private ResponseEntity <ClassRecordingDTO> getClassRecordingByClassId(@PathVariable("classId") @NotBlank @Positive Long classId)throws ResourceNotFoundException
	{  
		System.out.println("in get class recordng by ClassId");
		
		
		return ResponseEntity.ok(classServices.getClassRecordingByClassId(classId));
	} 
	
	
	@GetMapping(path="batchrecordings/{batchId}", produces = "application/json")  
	@ResponseBody
	@ApiOperation("Get Class Recordings by Batch ID")
	private ResponseEntity<List<ClassRecordingDTO>> getClassRecordingByBatchId(@PathVariable("batchId") @NotBlank @Positive Integer batchId)throws ResourceNotFoundException
	{  
		System.out.println("in get class recordng by batchId");
		
		
		return ResponseEntity.ok(classServices.getClassesRecordingByBatchId(batchId));
	}
	
	
	
}
