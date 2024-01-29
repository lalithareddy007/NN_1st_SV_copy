package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.UserRoleProgramBatchDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserRoleProgramBatchMapMapper;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserRoleProgramBatchMapService {

    @Autowired
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

    @Autowired
    private UserRoleProgramBatchMapMapper userRoleProgramBatchMapMapper;


    //get Assigned Program/Batch(es) for All users
    public List<UserRoleProgramBatchMapDto> getAllUserRoleProgramBatchMaps()
    {
        List<UserRoleProgramBatchMap> userRoleProgramBatchMaps = userRoleProgramBatchMapRepository.findAll();
        //get only the users whose UserRoleProgramBatchStatus is Active
        List<UserRoleProgramBatchMap> userRoleProgramBatchMaps1 = new ArrayList<>();
        for(UserRoleProgramBatchMap userRoleProgramBatchMap : userRoleProgramBatchMaps)
        {
            if(userRoleProgramBatchMap.getUserRoleProgramBatchStatus().equalsIgnoreCase("Active"))
            {
                userRoleProgramBatchMaps1.add(userRoleProgramBatchMap);
            }
        }
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = userRoleProgramBatchMapMapper.toUserRoleProgramBatchMapDtoList(userRoleProgramBatchMaps1);
        return userRoleProgramBatchMapDtos;
    }


    //get Program/Batch assigned to a user by user id
    public List<UserRoleProgramBatchMapDto> getByUserId(String id) {
        List<UserRoleProgramBatchMap> userRoleProgramBatchMaps = userRoleProgramBatchMapRepository.findByUser_UserId(id);
        if (userRoleProgramBatchMaps.isEmpty()) {
            throw new ResourceNotFoundException("UserRoleProgramBatchMap", "Id", id);
        }
        //get only the users whose UserRoleProgramBatchStatus is Active
        List<UserRoleProgramBatchMap> userRoleProgramBatchMaps1 = new ArrayList<>();
        for(UserRoleProgramBatchMap userRoleProgramBatchMap : userRoleProgramBatchMaps)
        {
            if(userRoleProgramBatchMap.getUserRoleProgramBatchStatus().equalsIgnoreCase("Active"))
            {
                userRoleProgramBatchMaps1.add(userRoleProgramBatchMap);
            }
        }
        if(userRoleProgramBatchMaps1.isEmpty())
        {
            throw new ResourceNotFoundException("UserRoleProgramBatchMap","Id",id);
        }
        return userRoleProgramBatchMapMapper.toUserRoleProgramBatchMapDtoList(userRoleProgramBatchMaps1);
    }



    public void deleteAllByUserId(String userId)
    {
        List<UserRoleProgramBatchMap> userRoleProgramBatchMaps = userRoleProgramBatchMapRepository.findByUser_UserId(userId);
        if(userRoleProgramBatchMaps.isEmpty())
        {
            throw new ResourceNotFoundException("UserRoleProgramBatchMap","id",userId);
        }
        for(UserRoleProgramBatchMap userRoleProgramBatchMap : userRoleProgramBatchMaps)
        {
            if(userRoleProgramBatchMap.getUserRoleProgramBatchStatus().equalsIgnoreCase("Active"))
            {
                userRoleProgramBatchMap.setUserRoleProgramBatchStatus("Inactive");
                userRoleProgramBatchMapRepository.save(userRoleProgramBatchMap);
            }
        }
        //userRoleProgramBatchMapRepository.deleteAll(userRoleProgramBatchMap);

    }



    //delete/Unassign the assigned Program/Batch for a user by user id
//    public void deleteById(String userId, String roleId, Long programId, Integer batchId)
//    {
//      Optional<UserRoleProgramBatchMap> userRoleProgramBatchMap = userRoleProgramBatchMapRepository.
//        findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(userId,roleId,programId,batchId);
//
//       if(userRoleProgramBatchMap.isEmpty()) {
//           throw new ResourceNotFoundException("UserRoleProgramBatchMap", "Id", userId);
//       }
//       else if(userRoleProgramBatchMap.get().getUserRoleProgramBatchStatus().equalsIgnoreCase("Active"))
//       {
//          userRoleProgramBatchMap.get().setUserRoleProgramBatchStatus("InActive");
//          userRoleProgramBatchMapRepository.save(userRoleProgramBatchMap.get());
//       }
//       else
//       {
//           throw new ResourceNotFoundException("UserRoleProgramBatchMap","Id",userId);
//       }
//       //userRoleProgramBatchMapRepository.delete(userRoleProgramBatchMap.get());
//
//    }


    //delete/Unassign the assigned Program/Batch for a user by user id
//    public void deleteById(String userId) {
//
//       List<UserRoleProgramBatchMap> userRoleProgramBatchMaps = userRoleProgramBatchMapRepository.findByUser_UserId(userId);
//       if(userRoleProgramBatchMaps.isEmpty())
//       {
//           throw new ResourceNotFoundException("UserRoleProgramBatchMap","id",userId);
//       }
//       String roleId = userRoleProgramBatchMaps.get(0).getRole().getRoleId();
//       Long programId = userRoleProgramBatchMaps.get(0).getProgram().getProgramId();
//       Integer batchId = userRoleProgramBatchMaps.get(0).getBatch().getBatchId();
//
//       Long userRoleProgramBatchId = userRoleProgramBatchMapRepository.findByUser_UserId(userId).get(0).getUserRoleProgramBatchId();
//
//       Optional<UserRoleProgramBatchMap> userRoleProgramBatchMap = userRoleProgramBatchMapRepository.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(userId,roleId,programId,batchId);
//       if(userRoleProgramBatchMap.get().getUserRoleProgramBatchStatus().equalsIgnoreCase("Active"))
//       {
//           userRoleProgramBatchMap.get().setUserRoleProgramBatchStatus("Inactive");
//           userRoleProgramBatchMapRepository.save(userRoleProgramBatchMap.get());
//       }
//    }






}
