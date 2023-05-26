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
