package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.UserLoginDto;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.AuthTokenFilter;
import com.numpyninja.lms.security.jwt.JwtUtils;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Service
public class UserLoginService {
    private UserLoginRepository userLoginRepository;
    private UserRoleMapRepository userRoleMapRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;



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


    public JwtResponseDto signin(LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUserLoginEmailId(), loginDto.getPassword())); // calls loadUserByName() in UserServices
                                                               // password verification is done by Spring security
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetailsImpl.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return  new JwtResponseDto(jwt,
                userDetailsImpl.getUserId(),
                userDetailsImpl.getUsername(),
                loginDto.getUserLoginEmailId(),
                roles);
    }


//validating token on page load when token is received from front end
    public boolean validateTokenAtAccountActivation(String token) {
        String tokenparse = null;
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            tokenparse = token.substring(7, token.length());
        }
        boolean tokenvalue = jwtUtils.validateJwtToken(tokenparse);
        if (tokenvalue)
            return tokenvalue;
        else
            throw new InvalidDataException("token not valid");

    }
}


