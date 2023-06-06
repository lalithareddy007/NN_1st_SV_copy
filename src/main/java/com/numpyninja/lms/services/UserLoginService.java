package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
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

    private UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserCache userCache;



    public UserLoginService(UserLoginRepository userLoginRepository,
                            UserRoleMapRepository userRoleMapRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleMapRepository = userRoleMapRepository;
    }


    /*public UserLoginDto authenticateUser(UserLoginDto uDto) {
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
                        List<String> activeRoleIds = extUserRoleMaps.stream()
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
                } else // Login status is inactive
                    uDto.setStatus("login inactive");
            } else  // Password mismatch for requested User
                uDto.setStatus("invalid");
        } else // User is NOT present in database
            uDto.setStatus("invalid");

        UserLoginDto resUserLoginDto = UserLoginDto.builder()
                .userLoginEmail(uDto.getUserLoginEmail())
                .status(uDto.getStatus())
                .roleIds(uDto.getRoleIds())
                .build();

        return resUserLoginDto;
    } */


    public JwtResponseDto signin(LoginDto loginDto) {
        // When user logs in, be it a regular login or forced relogin likein 'reset password' make sure to populate
        // userDetails from DB instead of from Cache;
        UserDetails userDetails = userCache.getUserFromCache(loginDto.getUserLoginEmailId());
        if (userDetails != null) {
            userCache.removeUserFromCache(loginDto.getUserLoginEmailId());
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUserLoginEmailId(), loginDto.getPassword())); // calls loadUserByName() in UserServices
        // UserName, password verification is done by Spring security by calling loadUserByUsername() in UserService
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetailsImpl.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponseDto(jwt,
                userDetailsImpl.getUserId(),
                loginDto.getUserLoginEmailId(),
                roles);
    }


    public void logout() {
        // get the current User from SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
        // remove the User from Cache
        userCache.removeUserFromCache(userDetails.getUsername());
    }


    //validating token on page load when token is received from front end
    public String validateTokenAtAccountActivation(String token) {
        String tokenparse = null;
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            tokenparse = token.substring(7, token.length());
        }
        String validity = jwtUtils.validateAccountActivationToken(tokenparse);

        //checking if its first login or account already exist
        String userLoginEMail = null;
        if (validity.equalsIgnoreCase("Valid")) {
            userLoginEMail = jwtUtils.getUserNameFromJwtToken(tokenparse);
            Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEMail);
            if (userOptional.isPresent()) { // User is present in database
                UserLogin userLogin = userOptional.get();
                String password = userLogin.getPassword();

                //if password is present in table
                if (!password.isEmpty()) {
                    validity = "acctActivated";
                }
            }
        }
       // Front end will send this emailid with password when they click
        // submit button on reset password page
        return userLoginEMail;
        // return validity;
    }

    public String resetPassword(LoginDto loginDto,String token) {
          String status ="inactive";
        //check if token is valid and return valid_email to front end
            String valid_email = validateTokenAtAccountActivation(token);

            //after clicking on resetlink or forgot password or change password
           // Front end will send the email and new password to be saved in db
            String Password = loginDto.getPassword();
            String userLoginEmail = loginDto.getUserLoginEmailId();

        Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEmail);
        // encrypt password
        String encryptedPassword =  encoder.encode(Password);

        if (userOptional.isPresent()) { // User is present in database
            UserLogin userLogin = userOptional.get();
           // String userId = userLogin.getUserId();
//            //email returned from validation token is same as one sent by dto
//            if(!valid_email.equalsIgnoreCase(userLoginEmail))
//                return "email not valid or not present in DB";
          //  else

                userLogin.setPassword(encryptedPassword);
                userLogin.setLoginStatus("active");
                userLoginRepository.save(userLogin);
                status = "activated";
        }
        return status;
    }
}


