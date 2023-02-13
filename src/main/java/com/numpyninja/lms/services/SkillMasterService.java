package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.SkillMasterDto;
import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.SkillMasterMapper;
import com.numpyninja.lms.repository.SkillMasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillMasterService {

	@Autowired
	SkillMasterRepository skillMasterRepository;

	@Autowired
	SkillMasterMapper skillMasterMapper;

	//createSkills
	public SkillMasterDto createAndSaveSkillMaster(SkillMasterDto skillMasterModel)throws DuplicateResourceFoundException
	{
		SkillMaster newSkillMaster = skillMasterMapper.toSkillMasterEntity(skillMasterModel);
		SkillMasterDto savedSkillMasterDTO =null;
		SkillMaster savedEntity =null;

		List<SkillMaster>result= skillMasterRepository.findBySkillName(newSkillMaster.getSkillName());
		if(result.size()>0) {
			throw new DuplicateResourceFoundException("cannot create skillMaster , since already exists");
		}else {
			savedEntity = skillMasterRepository.save(newSkillMaster);
			savedSkillMasterDTO= skillMasterMapper.INSTANCE.toSkillMasterDTO(savedEntity);
			return (savedSkillMasterDTO);
		}

	}

	//GetAllSkills
	public List<SkillMasterDto> getAllSkillMaster()  throws ResourceNotFoundException
	{
		List<SkillMaster> SkillMasterEntityList= skillMasterRepository.findAll();
		if(SkillMasterEntityList.size()<=0) {
			throw new ResourceNotFoundException("skillMaster list is not found");
		}
		else {
			return (skillMasterMapper.toSkillMasterDTOList(SkillMasterEntityList));
		}
	}


	//GetSkillByName
	public List<SkillMasterDto> getSkillMasterByName(String skillName)throws ResourceNotFoundException
	{
		if(!(skillName.isEmpty())) {

			List<SkillMaster>result= skillMasterRepository.findBySkillName(skillName);
			if(result.size()<=0) {
				System.out.println("skill with "+ skillName+"not found");
				throw new ResourceNotFoundException("skill with id"+skillName +"not found");
			}
			return skillMasterMapper.toSkillMasterDTOList(result);
		}
		else {
			System.out.println("skill cannot be blank or null");
			throw new InvalidDataException("SkillName is mandatory");
		}
	}


	//UpdateSkillById
	public SkillMasterDto updateSkillMasterById(Long skillId,SkillMasterDto skillDTO)throws ResourceNotFoundException
	{
		SkillMaster updatedSkillMasterEntity =null;
		SkillMaster savedSkillMasterEntity =null;
		SkillMasterDto savedSkillMasterDTO =null;


		if(skillId!= null) {
			Boolean value=skillMasterRepository.existsById(skillId);
			if(!(value)) {
				System.out.println("skill with "+ skillId+"not found");
				throw new ResourceNotFoundException("skill with id"+skillId +"not found");
			}
			else {

				updatedSkillMasterEntity= skillMasterRepository.findById(skillId).get();
				updatedSkillMasterEntity.setSkillName(skillDTO.getSkillName());
				updatedSkillMasterEntity.setCreationTime(skillDTO.getCreationTime());
				updatedSkillMasterEntity.setLastModTime(skillDTO.getLastModTime());

				savedSkillMasterEntity = skillMasterRepository.save(updatedSkillMasterEntity);
				savedSkillMasterDTO =skillMasterMapper.INSTANCE.toSkillMasterDTO(savedSkillMasterEntity);
				return savedSkillMasterDTO;
			}
		}else
		{
			System.out.println("skill id cannot be blank or null");
			throw new InvalidDataException("SkillId is mandatory");
		}
	}

	//DeleteSkillsById
	public boolean deleteBySkillId(Long skillId)throws ResourceNotFoundException
	{
		System.out.println("in delete skill by id method");
		if(skillId!=null)
		{
			Boolean value= skillMasterRepository.existsById(skillId);
			if(value) {
				SkillMaster skillMasterEntity= skillMasterRepository.findById(skillId).get();
				skillMasterRepository.delete(skillMasterEntity);
				return value;
			}else {
				System.out.println("no record found with skillId"+skillId);
				throw new ResourceNotFoundException("no record found with skillId");
			}

		}
		else
		{
			System.out.println("skillId cannot be blank or null");
			throw new InvalidDataException("SkillId is mandatory");
		}
	}


}
