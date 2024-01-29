package com.numpyninja.lms.services;


import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ClassScheduleMapper;
import com.numpyninja.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class ClassService {
	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private ProgBatchRepository batchRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ClassScheduleMapper classMapper;

	@Autowired
	private UserRoleMapRepository userRoleMapRepository;

	@Autowired
	RoleRepository roleRepository;

	//create a new class schedule for existing batchId and staffId
	//this had a bug related to csid entering in json so writing another method foe create class
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



	public ClassDto createClass(ClassDto newClassDto) throws DuplicateResourceFoundException
	{
// Check for duplicate class Topic
		//if (classRepository.existsByClassTopicIgnoreCase(newClassDto.getClassTopic())) {
			//throw new DuplicateResourceFoundException("Class", " Class Topic", newClassDto.getClassTopic());
		//}

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
		Optional<Class> classOptional = classRepository.findById(id);
		if(classOptional.isPresent()) {
			Class ClassScheduleById = classOptional.get();
			return classMapper.toClassSchdDTO(ClassScheduleById);

		}
		else {

			throw new ResourceNotFoundException("ClassSchedule is not found for classId :"+id);
		}
	}
	//get class by classId
	/*public ClassDto getClassByClassId(Long id) throws ResourceNotFoundException{
		Class ClassScheduleById= classRepository.findById(id).get();
		if(ClassScheduleById== null) {
			throw new ResourceNotFoundException("ClassSchedule is not found for classId :"+id);
		}
		else {
			return (classMapper.toClassSchdDTO(ClassScheduleById));
		}
	}*/
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
			throw new InvalidDataException("BatchId is mandatory");
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
			throw new InvalidDataException("StaffId is mandatory");
		}
	}
	//get all classes by classDate
	//coming soon



	//Update Class Schedules by Id
	public ClassDto updateClassByClassId(Long id,ClassDto modifiedClassDTO) throws ResourceNotFoundException{
		String staffId = modifiedClassDTO.getClassStaffId();
		Integer batchid = modifiedClassDTO.getBatchId();

		Class savedClass = this.classRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Class", "Id", id));
		
		User user = userRepository.findById(staffId)
				.orElseThrow(() -> new ResourceNotFoundException("Staff Id " + staffId + " not found"));

		Batch batchobj =  batchRepository.findById(batchid)
				.orElseThrow(() -> new ResourceNotFoundException("Batch", "Id", batchid));
		
		Class classBatchOptional = classRepository.findByCsIdAndBatchInClass_BatchId(id,batchid)
				.orElseThrow(() -> new ResourceNotFoundException("ClassId with " +id+ " and batchId with " +batchid+ " not found"));

       boolean roleMapRepository =
        		userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(modifiedClassDTO.getClassStaffId(), "R02","Active");
	 
	    if(!roleMapRepository)
		throw new ResourceNotFoundException("User", "Role(Staff)", modifiedClassDTO.getClassStaffId());


		LocalDateTime now= LocalDateTime.now();
		Timestamp timestamp= Timestamp.valueOf(now);

		if(StringUtils.hasLength(modifiedClassDTO.getClassComments()))
			savedClass.setClassComments(modifiedClassDTO.getClassComments());
		

		if(StringUtils.hasLength(modifiedClassDTO.getClassDescription()))
			savedClass.setClassDescription(modifiedClassDTO.getClassDescription());
		

		if(StringUtils.hasLength(modifiedClassDTO.getClassNotes()))
			savedClass.setClassNotes(modifiedClassDTO.getClassNotes());
		
		if(StringUtils.hasLength(modifiedClassDTO.getClassRecordingPath()))
			savedClass.setClassRecordingPath(modifiedClassDTO.getClassRecordingPath());
		

		if(StringUtils.hasLength(modifiedClassDTO.getClassStaffId()))
			savedClass.setStaffInClass(user);
		

		if(StringUtils.hasLength(modifiedClassDTO.getClassTopic()))
			savedClass.setClassTopic(modifiedClassDTO.getClassTopic());

		if(StringUtils.hasLength(modifiedClassDTO.getClassStatus()))
			savedClass.setClassStatus(modifiedClassDTO.getClassStatus());
		

		if(modifiedClassDTO.getClassNo().getClass() == Integer.class)
			savedClass.setClassNo(modifiedClassDTO.getClassNo());
		
		Optional<Date> optionalDate = Optional.ofNullable(modifiedClassDTO.getClassDate());
        if(optionalDate.isPresent())
        	savedClass.setClassDate(optionalDate.get());
		
     if(modifiedClassDTO.getBatchId().getClass() == Integer.class)
    	savedClass.setBatchInClass(batchobj);
      	savedClass.setLastModTime(timestamp);

		Class updatedClass = this.classRepository.save(savedClass);
		ClassDto updatedClassDto = classMapper.toClassSchdDTO(updatedClass);
		return updatedClassDto;
	}

	//delete by classId
	public Boolean deleteByClassId(Long classId) throws ResourceNotFoundException
	{
		System.out.println("in delete by classId Service Method");
		if(classId!=null) {
			Boolean value= classRepository.existsById(classId);
			if(value)
			{
				Class classEntity = classRepository.findById(classId).get();
				classEntity.setClassStatus("Inactive");
				classRepository.save(classEntity);
				return true;
			}
			else
			{
				System.out.println("record not found with classId: "+classId);
				throw new ResourceNotFoundException("record not found with classId");
			}

		}
		else
		{
			throw new InvalidDataException("ClassId is mandatory");
		}

	}

	//get class Recording by batchId
	public List<ClassRecordingDTO> getClassesRecordingByBatchId(Integer batchId) throws ResourceNotFoundException {

		List<Class> ClassObj =classRepository.findByBatchInClass_batchId(batchId);
		List<ClassRecordingDTO> classRecordingList = new ArrayList<>();
		if(ClassObj.isEmpty()) {
			throw new ResourceNotFoundException("Class Recording not found with batchId :" + batchId);
		}
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

		Class ClassRecord= classRepository.findById(id).
				orElseThrow(() -> new ResourceNotFoundException("Class", "Id", id));

		String classRecordingPath = ClassRecord.getClassRecordingPath();

		ClassRecordingDTO classRecordingDTO = new ClassRecordingDTO(id,classRecordingPath);
		return classRecordingDTO;
		//return new ClassRecordingDTO(id,classRecordingPath);
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

	//get All Class Recordings
	public List<ClassRecordingDTO> getAllClassRecordings(){
		final List<Class> classRecordingsList = classRepository.findAll();
		if(classRecordingsList.isEmpty()){
			throw new ResourceNotFoundException("Class Recording list is not found");
		} else {
			return classMapper.toClassRecordingDtoList(classRecordingsList);
		}
	}
}
