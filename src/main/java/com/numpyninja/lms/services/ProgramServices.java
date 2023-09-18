package com.numpyninja.lms.services;


import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.dto.ProgramWithUsersDTO;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.ProgramMapper;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Pattern;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProgramServices {
	@Autowired
	private ProgramRepository programRepository;

	@Autowired
	private ProgramMapper programMapper;
	
	@Autowired
	private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepo;
	
	@Autowired
	private UserMapper userMapper;
	
	public List<ProgramWithUsersDTO> getAllProgramsWithUsers(){
		List<ProgramWithUsersDTO> programsWithUserDto = new ArrayList<>();
		List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = userRoleProgramBatchMapRepo.findAll();
		Map<Program, List<UserRoleProgramBatchMap>> programMap = userRoleProgramBatchMapList.stream().collect(Collectors.groupingBy(UserRoleProgramBatchMap::getProgram));
		
		for(Entry<Program, List<UserRoleProgramBatchMap>> entry : programMap.entrySet()){

			programsWithUserDto.add(ProgramWithUsersDTO.builder().programId(entry.getKey().getProgramId())
			.programName(entry.getKey().getProgramName())
			.programDescription(entry.getKey().getProgramDescription())
			.programStatus(entry.getKey().getProgramStatus())
			.creationTime(entry.getKey().getCreationTime())
			.lastModTime(entry.getKey().getLastModTime())
			.programUsers(entry.getValue().stream().map(UserRoleProgramBatchMap::getUser).map(o->userMapper.userDto(o)).collect(Collectors.toList())).build());
		}
		return programsWithUserDto;
	}

	//getALLPrograms
	public List<ProgramDTO> getAllPrograms()  throws ResourceNotFoundException
	{
		List<Program> programEntityList= programRepository.findAll();
		if(programEntityList.size()<=0) {
			throw new ResourceNotFoundException("programs list is not found");
		}
		else {

			return (programMapper.toProgramDTOList(programEntityList));
		}
	}

	//getProgramByProgramId
	public ProgramDTO getProgramsById(Long programId) throws ResourceNotFoundException
	{
		if(programId!=null) {
			if (programRepository.existsById(programId)) {
				Program programEntity = programRepository.findById(programId).get();
				return (programMapper.toProgramDTO(programEntity));

			} else {
				throw new ResourceNotFoundException("program with this: " + programId + "not found");
			}
		}
		else {
			throw new InvalidDataException("ProgramId is mandatory");
		}
		//programEntity programEntity  = programRepository.findById(programId)
		//        .orElseThrow(() -> new ResourceNotFoundException("programId not found for this id :: " + programId));
	}


	//post
	public ProgramDTO createAndSaveProgram(ProgramDTO program)throws  DuplicateResourceFoundException
	{
		Program newProgramEntity = programMapper.toProgramEntity(program);
		ProgramDTO savedProgramDTO =null;
		Program savedEntity =null;
		LocalDateTime now = LocalDateTime.now();
		Timestamp timestamp = Timestamp.valueOf(now);
		newProgramEntity.setCreationTime(timestamp);
		newProgramEntity.setLastModTime(timestamp);
		List<Program>result= programRepository.findByProgramNameContainingIgnoreCaseOrderByProgramIdAsc(newProgramEntity.getProgramName());
		if(result.size()>0) {
			throw new DuplicateResourceFoundException("cannot create program , since already exists");
		}else {

			savedEntity = programRepository.save(newProgramEntity);
			savedProgramDTO= programMapper.toProgramDTO(savedEntity);
			return (savedProgramDTO);
		}

	}

	//update based on programId
	public ProgramDTO updateProgramById(Long programId,ProgramDTO program)throws ResourceNotFoundException
	{
		//Program updateLMSProgramEntity =null;
		Program savedProgramEntity =null;
		ProgramDTO savedProgramDTO =null;

		Boolean value=programRepository.existsById(programId);
		if(!(value)) {
			System.out.println("program with "+ programId+"not found");
			throw new ResourceNotFoundException("program with id"+programId +"not found");
		}
		else {

			Program updateLMSProgramEntity= programRepository.findById(programId).get();
			updateLMSProgramEntity.setProgramName(program.getProgramName());
			updateLMSProgramEntity.setProgramDescription(program.getProgramDescription());
			updateLMSProgramEntity.setProgramStatus(program.getProgramStatus());
//Bug8 ProgramBatch
			updateLMSProgramEntity.setCreationTime(updateLMSProgramEntity.getCreationTime());
			updateLMSProgramEntity.setLastModTime(new Timestamp( new Date().getTime()));

			savedProgramEntity = programRepository.save(updateLMSProgramEntity);
			savedProgramDTO =programMapper.INSTANCE.toProgramDTO(savedProgramEntity);
			return savedProgramDTO;
		}
	}

	//update based on programName
	public ProgramDTO updateProgramByName(String programName,ProgramDTO program)throws ResourceNotFoundException
	{
		Program updateProgramEntity =null;
		if(!(programName.isEmpty())) {

			List<Program>result= programRepository.findByProgramName(programName);
			if(result.size()<=0) {
				System.out.println("in update program, no program name list is found");
				throw new ResourceNotFoundException("no list with such program name"+programName);
			}else {
				for(Program rec:result) {

					if( rec.getProgramName().equalsIgnoreCase(programName)) {

						updateProgramEntity =rec;
					}

				}
				//updateProgramEntity.setProgram_id(program.getProgram_id());
				updateProgramEntity.setProgramName(program.getProgramName());
				updateProgramEntity.setProgramDescription(program.getProgramDescription());
				updateProgramEntity.setProgramStatus(program.getProgramStatus());
				updateProgramEntity.setCreationTime(updateProgramEntity.getCreationTime());
				updateProgramEntity.setLastModTime(new Timestamp( new Date().getTime()));
				//updateProgramEntity= programMapper.INSTANCE.toProgramEntity(program);
				programRepository.save(updateProgramEntity);

				return programMapper.INSTANCE.toProgramDTO(updateProgramEntity);
			}

		}//end of else
		else
		{
			throw new InvalidDataException("Program Name is mandatory");
		}
	}//end of method


	//delete by programId
	public boolean deleteByProgramId(Long programId)throws ResourceNotFoundException
	{
		System.out.println("in delete program by id method");
		if(programId!=null) {
			Boolean value = programRepository.existsById(programId);
			if (value) {
				Program progEntity = programRepository.findById(programId).get();
				programRepository.delete(progEntity);
				return value;
			} else {
				System.out.println("no record found with programId" + "  " + programId);
				throw new ResourceNotFoundException("no record found with programId" + programId);
			}
		}
		else {
			throw new InvalidDataException("ProgramId is mandatory");
		}
	}


	//delete by programName
	public boolean deleteByProgramName(String programName)throws ResourceNotFoundException
	{
		boolean deleted = false;
		Program deletedLMSProgramEntity =null;
		System.out.println("in deleteByprogramName impl");
		if(!(programName.isBlank())) {
			List<Program> result= programRepository.findByProgramName(programName);

			if(result.size()<=0) {
				System.out.println("no record found with progarmName:"+programName);
				throw new ResourceNotFoundException("no record found with programName");
			}else {
				for(Program eachRec:result) {

					if( eachRec.getProgramName().equalsIgnoreCase(programName)) {

						deletedLMSProgramEntity =eachRec;
					}
				}
				programRepository.delete(deletedLMSProgramEntity);
				deleted =true;
				return deleted;
			}

		}	else
		{
			throw new InvalidDataException("Program Name is mandatory");
		}

	}

}
