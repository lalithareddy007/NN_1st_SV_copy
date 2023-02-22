package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserSkill;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.repository.SkillMasterRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class UserSkillService {
  @Autowired
    private UserSkillRepository userSkillRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
    private UserSkillMapper userSkillMapper;

  @Autowired
  private SkillMasterRepository skillMasterRepository;


public UserSkillDTO createUserSkill(UserSkillDTO userSkillDTO){
    String userId = userSkillDTO.getUserId();
    long skillId = userSkillDTO.getSkillId();
    UserSkill newUserSkill =userSkillMapper.toUserSkillEntity(userSkillDTO);
    User  user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","Id",userId));
    newUserSkill.setUser(user);
    SkillMaster skillMaster= skillMasterRepository.findById((long) skillId).orElseThrow(()-> new ResourceNotFoundException("Skill","Id",skillId));
    newUserSkill.setSkill(skillMaster);
    LocalDateTime now= LocalDateTime.now();
    Timestamp timestamp= Timestamp.valueOf(now);
    newUserSkill.setCreationTime(timestamp);
    newUserSkill.setLastModTime(timestamp);
    UserSkill userSkillCreated = userSkillRepository.save(newUserSkill);
    return  userSkillMapper.toUserSkillDTO(userSkillCreated);
}
  public List<UserSkillDTO> getAllUserSkills(){
      List<UserSkill> userSkills = this.userSkillRepository.findAll();
      List<UserSkillDTO> userSkillDTOS = userSkillMapper.toUserSkillDTOList(userSkills);
      return userSkillDTOS;
  }
  public List<UserSkillDTO> getUserSkillForUser(String userId) {
      User user = this.userRepository.findById(userId)
              .orElseThrow(()->new ResourceNotFoundException("User" ,"Id", userId));
      List<UserSkill> userSkills =this.userSkillRepository.findByUser(user);
      List<UserSkillDTO> userSkillDTOS=userSkillMapper.toUserSkillDTOList(userSkills);
      return userSkillDTOS;
    }

    public UserSkillDTO updateUserSkill(UserSkillDTO userSkillDTO, String id) {
      UserSkill savedUserSkill = this.userSkillRepository.findById(id)
              .orElseThrow(()->new ResourceNotFoundException("User","Id",id));
      UserSkill updateUserSkill =userSkillMapper.toUserSkillEntity(userSkillDTO);
        LocalDateTime now= LocalDateTime.now();
        Timestamp timestamp= Timestamp.valueOf(now);
        updateUserSkill.setUserSkillId(id);
        updateUserSkill.setCreationTime(savedUserSkill.getCreationTime());
        updateUserSkill.setLastModTime(timestamp);
        UserSkill updatedUserSkill = this.userSkillRepository.save(updateUserSkill);
        UserSkillDTO updatedUserSkillDTO = userSkillMapper.toUserSkillDTO(updatedUserSkill);
        return updatedUserSkillDTO;
    }
    public void deleteUserSkillByUserSkillId(String id) {
        UserSkill userSkill = this.userSkillRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("UserSkill","Id",id));
        this.userSkillRepository.deleteById(id);
    }
   /* @Transactional
    public void deleteUserByUserId(String id) {
        List<UserSkill> userExists = userSkillRepository.existsByUserId(id);
        if(userExists.isEmpty()) {
            throw new ResourceNotFoundException("UserID: " + id + " doesnot exist ");
        }
        else
        {
            User user = new User();
            user.setUserId(id);
            userSkillRepository.deleteByUser(user);
        }
  }*/
}
