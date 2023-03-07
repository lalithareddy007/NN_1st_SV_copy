package com.numpyninja.lms.controller;

import com.numpyninja.lms.dto.UserLoginDto;
import com.numpyninja.lms.services.UserLoginService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
@Api(tags="User Login Controller", description="User Login Authentication")
public class UserLoginController {
    private UserLoginService userLoginService;

    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/authenticate")
	@ApiOperation("Authenticate User")
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
}
