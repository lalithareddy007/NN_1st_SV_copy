package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.EmailDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.entity.EmailDetails;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.JwtUtils;
import com.numpyninja.lms.util.EmailSender;

import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UserLoginService {
	
	@Autowired
    private UserLoginRepository userLoginRepository;
    private UserRoleMapRepository userRoleMapRepository;
   
    @Autowired
    private UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private UserCache userCache;
   
    @Autowired
    private EmailSender emailSender;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public String getFrontendURL() {
    	return frontendUrl;
    }

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
    //Front end calls this service to validate token
    //token sent by front end is one generated by generateEmailUrlToken which has admin created user_emailid
    public String validateTokenAtAccountActivation(String token) {
        String tokenparse = null;
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            tokenparse = token.substring(7, token.length());
        }
        String validity = jwtUtils.validateAccountActivationToken(tokenparse);
        //checking if its first login or account activated already
        String userLoginEMail = null;
        if (validity.equalsIgnoreCase("Valid")) {
            userLoginEMail = jwtUtils.getUserNameFromJwtToken(tokenparse);
            Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEMail);
            if (userOptional.isPresent()) { // User is present in database
                UserLogin userLogin = userOptional.get();
                String password = userLogin.getPassword();
                //if password is present in table for forgot/reset password
                if (!password.isEmpty()){   // Presence of password means, Token is valid, but Account is already Activated
                    validity = "acctActivated already";
                }
                else return userLoginEMail;
                // Token is valid, account is not already activated
                // we shd send the email to FrontEnd, they will store it and when user types in new password, and click submit
                // they will sendback the emailid with password to /resetPassword endpoint.
            }
        }
        return validity;
    }




   //Regular users changePassword or forgotPassword or resetPassword_accountActivation calls this method
    public String resetPassword(LoginDto loginDto,String token) {
        String status = "Invalid";
        String tokenparse = null;

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            tokenparse = token.substring(7, token.length());
        }

        boolean valid = jwtUtils.validateJwtToken(tokenparse);
        if (!valid)
            return status;

        //after clicking on account activation link or forgot password or reset password
        // Front end will send the email and new password to be saved in db
        String password = loginDto.getPassword();
        // encrypt password
        String encryptedPassword = encoder.encode(password);

        String userLoginEmail = loginDto.getUserLoginEmailId();
        Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEmail);

        if (userOptional.isPresent()) { // User is present in database
            UserLogin userLogin = userOptional.get();
            userLogin.setPassword(encryptedPassword);
            userLoginRepository.save(userLogin);
            status = "Password saved";
        }
        return status;
    }
        
    public JwtResponseDto forgotPasswordConfirmEmail(EmailDto emailDto) {
		String userLoginEmail = emailDto.getUserLoginEmailId();
		Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEmail);
		JwtResponseDto forgotPwdDto = new JwtResponseDto();
		if (userOptional.isPresent()) { // User is present in database
			UserLogin userLogin = userOptional.get();
			String userId = userLogin.getUserId();
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) { 
            User userDetails = user.get();
           
			if ("active".equalsIgnoreCase(userLogin.getLoginStatus())) {
			
				// sending email to change password
				try {
					Map<String, Object> model = new HashMap<>();
					model.put("firstName", userDetails.getUserFirstName());
					System.out.println("FirstName"+userDetails.getUserFirstName());
					model.put("lastName", userDetails.getUserLastName());
					String token = jwtUtils.generateJwtTokenForgotPwd(userLogin.getUserLoginEmail());
					String url = createEmailUrlConfirmPwdWithToken(userLogin.getUserLoginEmail(), token);
					System.out.println("email URL:" + url);
					model.put("regLink", url);

					String emailMessage = emailSender.sendEmailUsingTemplateForgotPassword(new EmailDetails(userLogin.getUserLoginEmail(), "", "",
									"Please click on link to generate new password", model));
					System.out.println(emailMessage);
					forgotPwdDto.setEmail(userLoginEmail);
					forgotPwdDto.setToken(token);
					forgotPwdDto.setStatus("Email sent to your registered email Id");

				} catch (Exception e) {
					e.printStackTrace();
					forgotPwdDto.setEmail(userLoginEmail);
	                forgotPwdDto.setStatus("Failed to send email");
	                forgotPwdDto.setToken(null);
				}

			} else {
				forgotPwdDto.setEmail(userLoginEmail);
				forgotPwdDto.setStatus("login inactive");
				forgotPwdDto.setToken("null");
			}
            }else {
	            forgotPwdDto.setEmail(userLoginEmail);
	            forgotPwdDto.setStatus("User not found");
	            forgotPwdDto.setToken(null);
	        }
		}else {
			forgotPwdDto.setEmail(userLoginEmail);
			forgotPwdDto.setStatus("Invalid Email");
			forgotPwdDto.setToken("null");
		}

		return forgotPwdDto;
	}

	public String createEmailUrlConfirmPwdWithToken(String loginEmail, String token) {
		final String url = UriComponentsBuilder.fromHttpUrl(getFrontendURL()).path("/reset-password")
				.queryParam("token", token).toUriString();

		return url;
	}
	
	//validate token 
    public boolean validateToken(String token) {
        String tokenparse = null;
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            tokenparse = token.substring(7, token.length());
        }
        Boolean validity = jwtUtils.validateJwtToken(tokenparse);

        if (validity == true) {
            String userLoginEMail = jwtUtils.getUserNameFromJwtToken(tokenparse);
            Optional<UserLogin> userOptional = userLoginRepository.findByUserLoginEmailIgnoreCase(userLoginEMail);
            if (userOptional.isPresent()) { 
            	// User is present in database
                UserLogin userLogin = userOptional.get();
                validity = true;
            }
            else{
            	validity = false;
            }
        }
        return validity;
    }


}


