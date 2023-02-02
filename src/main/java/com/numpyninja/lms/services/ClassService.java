package com.numpyninja.lms.services;



import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.entity.Batch;

import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.exception.DuplicateResourceFound;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ClassScheduleMapper;
import com.numpyninja.lms.repository.ClassRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.RoleRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.persistence.ElementCollection;


@Service
public class ClassService {
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired 
    ProgBatchRepository batchRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    private ClassScheduleMapper classMapper;
    
    @Autowired
	private UserRoleMapRepository userRoleMapRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    //create a new class schedule for existing batchId and staffId 
 /*   public ClassDto createClass(ClassDto newClassDto) throws DuplicateResourceFound {
    	 Class newClassScheduleEntity;
    	 
    	 Batch batchEntity;
    	 User userEntity;
    	 
		ClassDto savedclassSchdDto =null;
		 Class savedEntity =null;
		 
		 if(newClassDto != null  && newClassDto.getCsId()!=null && newClassDto.getBatchId()!=null && newClassDto.getClassStaffId()!=null) 
   {
			 

				newClassScheduleEntity= classMapper.toClassScheduleEntity(newClassDto);
				Integer batchIdInClass= newClassDto.getBatchId();
				String StaffInClass = newClassDto.getClassStaffId();
			
				
				
				
  if(batchRepository.existsById(batchIdInClass) && userRepository.existsById(StaffInClass))
				{
					batchEntity = batchRepository.findById(batchIdInClass).get();
					userEntity = userRepository.findById(StaffInClass).get();
					
				List<Class> result = 
						classRepository.findByClassIdAndBatchId(newClassScheduleEntity.getCsId(), (newClassScheduleEntity.getBatchInClass()).getBatchId());
				if(result.size()>0) {
					System.out.println("the same combination with ClassId and BatchId exists");
					throw new DuplicateResourceFound("cannot create Class , since already exists with same combination");
				}else {
					
					//save the new class details in repository since this combination is new
					//set the batch,staff entity details to class 
					
					newClassScheduleEntity.setBatchInClass(batchEntity);
					newClassScheduleEntity.setStaffInClass(userEntity);
				
					savedEntity= classRepository.save(newClassScheduleEntity);
					savedclassSchdDto =classMapper.toClassSchdDTO(savedEntity);
				//return savedclassSchdDto;
				}
				}else {
					if(!(batchRepository.existsById(batchIdInClass))) {
						 System.out.println("BatchId is Fk: no BatchId Exists in batch table "+batchIdInClass);
						 throw new NoSuchElementException("no BatchId Exists in batch table ");
					    }if(!(userRepository.existsById(StaffInClass))) {
						 System.out.println("staffId is Fk: no staffId Exists in user table "+StaffInClass);
						 throw new NoSuchElementException("no staffId Exists in user table ");
					  }//end of if
				    }//end of else
				}//end of if
		 	 else {
				System.out.println("check either dto, csId, batchId, staffId is null ");
				throw new NullPointerException();
			}
        return savedclassSchdDto;
    } */

    
    
    public ClassDto createClass(ClassDto newClassDto) throws DuplicateResourceFound
    {
    	
    	
    	int batchId = newClassDto.getBatchId();
    	String staffId = newClassDto.getClassStaffId();
    	
    	
    	Batch batchobj =  batchRepository.findById(batchId)
    			.orElseThrow(() -> new ResourceNotFoundException("Batch", "Id", batchId));
    	User user = userRepository.findById(staffId)
 				.orElseThrow(() -> new ResourceNotFoundException("staffid " + staffId + " not found"));
    	
    	boolean roleMapRepository = 
    			 userRoleMapRepository.findUserRoleMapByUser_UserIdAndRole_RoleIdNotAndUserRoleStatusEqualsIgnoreCase
    	(newClassDto.getClassStaffId(), "R02","Active").isEmpty();
    	
    	if(!roleMapRepository)
    	throw new ResourceNotFoundException("User", "Role(Admin/Staff)", newClassDto.getClassStaffId());
    	
    	
    	
    	Class class1 = classMapper.toClassScheduleEntity(newClassDto);
    	
    	LocalDateTime now = LocalDateTime.now();
 		Timestamp timestamp = Timestamp.valueOf(now);
 		class1.setCreationTime(timestamp);
 		class1.setLastModTime(timestamp);
    	
 		Class class2 = classRepository.save(class1);
 		return classMapper.toClassSchdDTO(class2);
 		
 		
 		
 		
    }
    
    
    //get All Class schedules -not mentioned in Excel
    public List<ClassDto> getAllClasses() throws ResourceNotFoundException{
      List<Class> ClassScheduleList= classRepository.findAll();
		if(ClassScheduleList.size()<=0) {
			throw new ResourceNotFoundException("ClassSchedule list is not found");
		}
		else {
	    	    return (classMapper.toClassScheduleDTOList(ClassScheduleList));
		}
      }
    
    //get class by classId
      public ClassDto getClassByClassId(Long id) throws ResourceNotFoundException{
    	  Class ClassScheduleById= classRepository.findById(id).get();
  		if(ClassScheduleById== null) {
  			throw new ResourceNotFoundException("ClassSchedule is not found for classId :"+id);
  		}
  		else {
  	    	    return (classMapper.toClassSchdDTO(ClassScheduleById));
  		}
      }
    //get all classes by classTopic - not mentioned in Excel
      public List<ClassDto> getClassesByClassTopic(String classTopic)throws ResourceNotFoundException
  	{
  		if(!(classTopic.isEmpty())) {
  					
  		List<Class>result= classRepository.findByClassTopicContainingIgnoreCaseOrderByClassTopicAsc(classTopic);
  		if(result.size()<=0) {
  			System.out.println("list of classes with "+ classTopic+" not found");
  			 throw new ResourceNotFoundException("classes with class topic Name: "+classTopic +" not found"); 
  		}
  		return classMapper.toClassScheduleDTOList(result);
  		}
  		else {
  			System.out.println("class Topic search string cannot be blank or null");
  			throw new IllegalArgumentException();
  		}
  	 }
  		
    //get all classes by batchId
     /* @Transactional
      public List<ClassDto> getClassesByBatchId(Integer batchId) throws ResourceNotFoundException,IllegalArgumentException
  	{
  		if(batchId!=null)
  		{ 
  			List<Class> result=classRepository.findByBatchInClass_batchId(batchId);
  			if(!(result.size()<0))
  			{
  				return (classMapper.toClassScheduleDTOList(result));
  				
  			}else
  			{
  				throw new ResourceNotFoundException("classes with this batcghId "+batchId +"not found");
  			}
  		}else
  		{
  			System.out.println("batchId search string cannot be null");
  			throw new IllegalArgumentException();
  		}
   	}*/

	//get all classes by batchId
	@Transactional
	public List<ClassDto> getClassesByBatchId(Integer batchId) throws ResourceNotFoundException,IllegalArgumentException
	{
		if(batchId!=null)
		{
			List<Class> result=classRepository.findByBatchInClass_batchId(batchId);
			if(!(result.size()<=0))
			{
				return (classMapper.toClassScheduleDTOList(result));

			}else
			{
				throw new ResourceNotFoundException("classes with this batchId "+batchId +"not found");
			}
		}else
		{
			System.out.println("batchId search string cannot be null");
			throw new IllegalArgumentException();
		}
	}
      
    //get all classes by classStaffId
     /* public List<ClassDto> getClassesByStaffId(String staffId) throws ResourceNotFoundException,IllegalArgumentException
    	{
    		if(staffId!=null)
    		{ 
    			List<Class> result=classRepository.findBystaffInClass_userId(staffId);  
    			if(!(result.size()<0))
    			{
    				return (classMapper.toClassScheduleDTOList(result));
    				
    			}else
    			{
    				throw new ResourceNotFoundException("classes with this staffId "+staffId +" not found");
    			}
    		}else
    		{
    			System.out.println("staffId search string cannot be null");
    			throw new IllegalArgumentException();
    		}
     	}*/
//get all classes by classStaffId
	public List<ClassDto> getClassesByStaffId(String staffId) throws ResourceNotFoundException,IllegalArgumentException
	{
		if(staffId!=null)
		{
			List<Class> result=classRepository.findBystaffInClass_userId(staffId);
			if(!(result.size()<=0))
			{
				return (classMapper.toClassScheduleDTOList(result));

			}else
			{
				throw new ResourceNotFoundException("classes with this staffId "+staffId +" not found");
			}
		}else
		{
			System.out.println("staffId search string cannot be null");
			throw new IllegalArgumentException();
		}
	}
    //get all classes by classDate
      //coming soon
    
     

    //Update Class Schedules by Id
	
	public ClassDto updateClassByClassId(Long id,ClassDto modifiedClassDTO) throws ResourceNotFoundException{
		
	 Class savedClass = this.classRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class", "Id", id));

		

		Class updateClass = classMapper.toClassScheduleEntity(modifiedClassDTO);
		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);
		//Batch batch = this.batchRepository.findById(modifiedClassDTO.getBatchId());

		if(StringUtils.hasLength(modifiedClassDTO.getClassComments()))
			updateClass.setClassComments(modifiedClassDTO.getClassComments());
		else
			updateClass.setClassComments(savedClass.getClassComments());

		if(StringUtils.hasLength(modifiedClassDTO.getClassDescription()))
			updateClass.setClassDescription(modifiedClassDTO.getClassDescription());
		else
			updateClass.setClassDescription(savedClass.getClassDescription());

		if(StringUtils.hasLength(modifiedClassDTO.getClassNotes()))
			updateClass.setClassNotes(modifiedClassDTO.getClassNotes());
		else
			updateClass.setClassNotes(savedClass.getClassNotes());

		if(StringUtils.hasLength(modifiedClassDTO.getClassRecordingPath()))
			updateClass.setClassRecordingPath(modifiedClassDTO.getClassRecordingPath());
		else
			updateClass.setClassRecordingPath(savedClass.getClassRecordingPath());

		if(StringUtils.hasLength(modifiedClassDTO.getClassStaffId()))
		//	updateClass.setStaffInClass(modifiedClassDTO.getClassStaffId());
		//else
			updateClass.setStaffInClass(savedClass.getStaffInClass());
		
		if(StringUtils.hasLength(modifiedClassDTO.getClassTopic()))
			updateClass.setClassTopic(modifiedClassDTO.getClassTopic());
		else
			updateClass.setClassTopic(savedClass.getClassTopic());
		
		//if(StringUtils.(modifiedClassDTO.getClassNo()))
		updateClass.setClassNo(modifiedClassDTO.getClassNo());
		//else 
			//updateClass.setClassNo(modifiedClassDTO.getClassNo());
		
		updateClass.setClassDate(modifiedClassDTO.getClassDate());
		
		
		
		updateClass.setBatchInClass(savedClass.getBatchInClass());
		updateClass.setCsId(id);
		updateClass.setCreationTime(savedClass.getCreationTime());
		updateClass.setLastModTime(timestamp);

		Class updatedClass = this.classRepository.save(updateClass);
		ClassDto updatedClassDto = classMapper.toClassSchdDTO(updatedClass);
		return updatedClassDto;
	}
		
		
		
		
		
		
	
	
//    public ClassDto updateClassByClassId(Long id,ClassDto modifiedClassDTO) throws ResourceNotFoundException{
//    	{
//			System.out.println("in updateClassServiceById method");
//			Class updateClassSchedule;
//			ClassDto savedClassDTO = null;
//			Class savedClassSchedule =null;
//			
//		        //Class savedClassSchedule = this.classRepository.findById(id);
//			if(id!=null)
//			{
//				Class newClassSchedule  = classMapper.toClassScheduleEntity(modifiedClassDTO);
//			LocalDateTime now= LocalDateTime.now();
//				Timestamp timestamp= Timestamp.valueOf(now);
//			Boolean isPresentTrue=classRepository.findById(id).isPresent();
//			
//			if(isPresentTrue)
//			{
//				updateClassSchedule = classRepository.getById(id);
//				updateClassSchedule.setClassComments(modifiedClassDTO.getClassComments());
//				updateClassSchedule.setClassDate(modifiedClassDTO.getClassDate());
//				updateClassSchedule.setClassDescription(modifiedClassDTO.getClassDescription());
//				updateClassSchedule.setClassNo(modifiedClassDTO.getClassNo());
//				updateClassSchedule.setCreationTime(newClassSchedule.getCreationTime());     
//				//updateClassSchedule.setCreationTime(modifiedClassDTO.getCreationTime());
//				updateClassSchedule.setLastModTime(timestamp);
//				updateClassSchedule.setClassNotes(modifiedClassDTO.getClassNotes());
//				updateClassSchedule.setClassRecordingPath(modifiedClassDTO.getClassRecordingPath());
//				updateClassSchedule.setClassTopic(modifiedClassDTO.getClassTopic());
//				
//				
//				Batch updatedBatchEntityInClass = batchRepository.getById(modifiedClassDTO.getBatchId());
//				User updatedStaffEntityInClass = userRepository.getById(modifiedClassDTO.getClassStaffId());
//				
//				updateClassSchedule.setBatchInClass(updatedBatchEntityInClass);
//				updateClassSchedule.setStaffInClass(updatedStaffEntityInClass);
//				
//				savedClassSchedule = classRepository.save(updateClassSchedule);
//				 savedClassDTO = classMapper.toClassSchdDTO(savedClassSchedule);
//				 
//				 return savedClassDTO; 
//			}
//			else {
//				throw new ResourceNotFoundException("no record found with "+ id);
//			}
//			
//		}else {
//			throw new IllegalArgumentException();
//		}
//	}
//   }    

    
    	//delete by classId
    	public Boolean deleteByClassId(Long classId) throws ResourceNotFoundException
		{
			System.out.println("in delete by classId Service Method");
			if(classId!=null) {
				Boolean value= classRepository.existsById(classId);
				if(value)
				{
					classRepository.deleteById(classId);
					return value;
				}
				else
				{
					System.out.println("record not found with classId: "+classId);
					throw new ResourceNotFoundException("record not found with classId");
				}
				 
			}				
			else
			{
				throw new IllegalArgumentException();
			}
			
		}

    	
    	//get class Recording by batchId
  	  	
    	public List<ClassRecordingDTO> getClassesRecordingByBatchId(Integer batchId) throws ResourceNotFoundException {
    		
    		List<Class> ClassObj =classRepository.findByBatchInClass_batchId(batchId);
    			List<ClassRecordingDTO> classRecordingList = new ArrayList<>();
    			ClassObj.stream().forEach((k) -> {
    			ClassRecordingDTO classRecordObject = new ClassRecordingDTO();
    			classRecordObject.setCsId(k.getCsId());
    			classRecordObject.setClassRecordingPath(k.getClassRecordingPath());
                classRecordingList.add(classRecordObject);
    		});
    		return classRecordingList;
    	} 
    	
    	
    	
    	
    	
    	
 //get class Recording by ClassId	   	
  	public ClassRecordingDTO getClassRecordingByClassId(Long id) throws ResourceNotFoundException{
			
    		Class ClassRecord= classRepository.findById(id).get();
    		String classRecordingPath = classRepository.getById(id).getClassRecordingPath();
    		
    		
    		if(ClassRecord== null) {
    			throw new ResourceNotFoundException("ClassRecording is not found for classId :"+id);
    		}
    		
      		else {
      	    	    return new ClassRecordingDTO(id,classRecordingPath);
      		}
    			 
    	}
    	
    	
    	
    //Update Class Recording by ClassId
    	public ClassDto updateClassRecordingByClassId(Long id, ClassRecordingDTO classRecordingDTO) throws ResourceNotFoundException{
    		{

    			Class updateClassSchedule;
    			ClassDto savedClassDTO = null;
    			Class savedClassSchedule =null;
    			if(id!=null)
    			{
    				//Class newClassSchedule  = classMapper.toClassScheduleEntity();
    				Boolean isPresentTrue=classRepository.findById(id).isPresent();

    				if(isPresentTrue)
    				{
    					updateClassSchedule = classRepository.getById(id);
    					updateClassSchedule.setClassRecordingPath(classRecordingDTO.getClassRecordingPath());
    					savedClassSchedule = classRepository.save(updateClassSchedule);
    					savedClassDTO = classMapper.toClassSchdDTO(savedClassSchedule);

    					return savedClassDTO;
    				}
    				else {
    					throw new ResourceNotFoundException("no record found with "+ id);
    				}

    			}else {
    				throw new IllegalArgumentException();
    			}
    		}
    	}	
}
