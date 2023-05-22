package com.numpyninja.lms.controller;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.UserLoginDto;
import com.numpyninja.lms.services.UserLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.validation.Valid;

@RestController
public class UserLoginController {
    private UserLoginService userLoginService;
    @Autowired
    private UserCache userCache;

    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    /*@PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDto uDto){
        UserLoginDto resUserLoginDto = userLoginService.authenticateUser(uDto);
        String status = resUserLoginDto.getStatus().toLowerCase();
        if (status.equals("invalid"))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else if ((status.equals("role inactive"))
                || (status.equals("login inactive"))
                || (status.equals("role unavailable")))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        else
            return ResponseEntity.status(HttpStatus.OK).body(resUserLoginDto);
    }
    */

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> signin(@Valid @RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userLoginService.signin( loginDto ));
    }

    @GetMapping("/logoutlms")
    public ResponseEntity<String> logout(){
        userLoginService.logout();
        return ResponseEntity.ok("Logout successful");
    }
}
