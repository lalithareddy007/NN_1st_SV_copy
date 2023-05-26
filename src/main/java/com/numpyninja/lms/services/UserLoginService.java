package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.UserLoginDto;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.repository.UserLoginRepository;
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
    @Autowired
    private UserCache userCache;

    public UserLoginService(UserLoginRepository userLoginRepository,
                            UserRoleMapRepository userRoleMapRepository) {
        this.userLoginRepository = userLoginRepository;
        this.userRoleMapRepository = userRoleMapRepository;
    }


    public JwtResponseDto signin(LoginDto loginDto){
        // When user logs in, be it a regular login or forced relogin likein 'reset password' make sure to populate
        // userDetails from DB instead of from Cache;
        UserDetails userDetails = userCache.getUserFromCache(loginDto.getUserLoginEmailId());
        if ( userDetails != null ) {
            userCache.removeUserFromCache( loginDto.getUserLoginEmailId());
        }
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
                loginDto.getUserLoginEmailId(),
                roles);
    }

    public void logout(){
        // get the current User from SecurityContext
        SecurityContext securityContext = SecurityContextHolder.getContext();
        UserDetails userDetails = (UserDetails)securityContext.getAuthentication().getPrincipal();
        // remove the User from Cache
        userCache.removeUserFromCache( userDetails.getUsername());
    }
}


