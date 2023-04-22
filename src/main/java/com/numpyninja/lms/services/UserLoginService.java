package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.UserLoginDto;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserLoginService {
    private UserLoginRepository userLoginRepository;
    private UserRoleMapRepository userRoleMapRepository;

    public UserLoginService(UserLoginRepository userLoginRepository,
                            UserRoleMapRepository userRoleMapRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleMapRepository = userRoleMapRepository;
    }

    public UserLoginDto authenticateUser(UserLoginDto uDto) {
        String userLoginEmail = uDto.getUserLoginEmail();
        String password = uDto.getPassword();

        Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEmail);
        if (userOptional.isPresent()) { // User is present in database
            UserLogin userLogin = userOptional.get();
            if (password.equals(userLogin.getPassword())) { // Password matches for requested User
                if ("active".equalsIgnoreCase(userLogin.getLoginStatus())) { // Login status is active
                    // Check for associated roles
                    List<UserRoleMap> extUserRoleMaps = userRoleMapRepository.findUserRoleMapsByUserUserId(userLogin.getUser().getUserId());
                    if (extUserRoleMaps.isEmpty()) // No roles available for requested user
                        uDto.setStatus("role unavailable");
                    else {
                        // Retrieve list of rolesIds for associated Active roles
                        List<String> activeRoleIds =  extUserRoleMaps.stream()
                                .filter(userRoleMap -> "active".equalsIgnoreCase(userRoleMap.getUserRoleStatus()))
                                .map(userRoleMap -> userRoleMap.getRole().getRoleId())
                                .collect(Collectors.toList());
                        if (activeRoleIds.isEmpty())  // No active roles present for requested user
                            uDto.setStatus("role inactive");
                        else {  // Retrieve list of Active rolesIds for requested user
                            uDto.setStatus("active");
                            uDto.setRoleIds(activeRoleIds);
                        }
                    }
                }
                else // Login status is inactive
                    uDto.setStatus("login inactive");
            }
            else  // Password mismatch for requested User
                uDto.setStatus("invalid");
        }
        else // User is NOT present in database
            uDto.setStatus("invalid");

        UserLoginDto resUserLoginDto = UserLoginDto.builder()
                .userLoginEmail(uDto.getUserLoginEmail())
                .status(uDto.getStatus())
                .roleIds(uDto.getRoleIds())
                .build();

        return resUserLoginDto;
    }
}