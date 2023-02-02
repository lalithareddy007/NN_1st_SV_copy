package com.numpyninja.lms.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

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

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.exception.DuplicateResourceFound;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.ClassService;


@RestController
@RequestMapping
public class ClassController {
	
	@Autowired
	ClassService classServices;

	//createClass
	@PostMapping(path="/CreateClassSchedule",consumes = "application/json", produces = "application/json")  
	@ResponseBody
	private ResponseEntity<ClassDto> createAndSaveClass(@Valid @RequestBody ClassDto classDTO)throws  DuplicateResourceFound
	{  
		System.out.println("in create a new class schedule");
		ClassDto savedClassDTO = this.classServices.createClass(classDTO);
	return ResponseEntity.status(HttpStatus.CREATED).body(savedClassDTO);	
	} 
	
	//GetAllClasses
	@GetMapping(value = "/allClasses", produces = "application/json")
	private ResponseEntity<?> getAllClassesList()  throws ResourceNotFoundException
	{ 
		System.out.println("in getall classes");
		List<ClassDto> ClassesList = classServices.getAllClasses();
		return ResponseEntity.ok(ClassesList);  
	}  
	
	//GetClassesByClassId
	@GetMapping(path="class/{classId}", produces = "application/json")  
	@ResponseBody
	private ResponseEntity <?> getClassesById(@PathVariable("classId") @NotBlank @Positive Long classId)throws ResourceNotFoundException
	{  
		System.out.println("in get All classes by ClassId");
		ClassDto classesDTOList= classServices.getClassByClassId(classId);
		return ResponseEntity.ok(classesDTOList);
	}  
	
	//GetAllClassesByClassTopic
		@GetMapping(value = "/classes/{classTopic}", produces = "application/json")
		private ResponseEntity<?> getAllClassesByClassTopic(@PathVariable("classTopic") @NotBlank @Positive String classTopic)  throws ResourceNotFoundException
		{ 
			System.out.println("in getall Class By ClassTopic");
			List<ClassDto> ClassesList = classServices.getClassesByClassTopic(classTopic);
			return ResponseEntity.ok(ClassesList);  
		} 
		
		//get all classes by batchId
		@GetMapping(path="classesbyBatch/{batchId}", produces = "application/json")  
		@ResponseBody
		private ResponseEntity <?> getClassesByBatchId(@PathVariable("batchId") @NotBlank @Positive Integer batchId)throws ResourceNotFoundException
		{  
			System.out.println("in get All classes by BatchId");
			List<ClassDto> classesDTOList= classServices.getClassesByBatchId(batchId);
			return ResponseEntity.ok(classesDTOList);
		}  
		
		//get all classes by classStaffId
		@GetMapping(path="classesByStaff/{staffId}", produces = "application/json")  
		@ResponseBody
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
	private ResponseEntity <ClassDto> updateClassScheduleById(@PathVariable @NotBlank @Positive Long classId ,@Valid @RequestBody ClassDto modifyClassSchedule) throws ResourceNotFoundException
	{  
	return ResponseEntity.ok(classServices.updateClassByClassId(classId,modifyClassSchedule));
	} 
	
	//DeleteClassById
	@DeleteMapping(path="deletebyClass/{classId}",produces = "application/json")  
	@ResponseBody
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
	private ResponseEntity <ClassDto> updateClassRecordingByClassId(@PathVariable @NotBlank @Positive Long classId ,@Valid @RequestBody ClassRecordingDTO classRecordingDTO) throws ResourceNotFoundException
	{  
	return ResponseEntity.ok(classServices.updateClassRecordingByClassId(classId,classRecordingDTO));
	} 
	
	
	//get class Recording by classId
	
	@GetMapping(path="classrecordings/{classId}", produces = "application/json")  
	@ResponseBody
	private ResponseEntity <ClassRecordingDTO> getClassRecordingByClassId(@PathVariable("classId") @NotBlank @Positive Long classId)throws ResourceNotFoundException
	{  
		System.out.println("in get class recordng by ClassId");
		
		
		return ResponseEntity.ok(classServices.getClassRecordingByClassId(classId));
	} 
	
	
	@GetMapping(path="batchrecordings/{batchId}", produces = "application/json")  
	@ResponseBody
	private ResponseEntity<List<ClassRecordingDTO>> getClassRecordingByBatchId(@PathVariable("batchId") @NotBlank @Positive Integer batchId)throws ResourceNotFoundException
	{  
		System.out.println("in get class recordng by batchId");
		
		
		return ResponseEntity.ok(classServices.getClassesRecordingByBatchId(batchId));
	}
	
	
	
}
