package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.services.UserLoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/login")
public class UserLoginController {
    private UserLoginService userLoginService;

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

    @PostMapping("")
    public ResponseEntity<JwtResponseDto> signin(@Valid @RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userLoginService.signin( loginDto ));
    }

    @GetMapping("/AccountActivation")
    public ResponseEntity<ApiResponse> validateAccountActToken(
            @RequestHeader(value = "Authorization") String token) {

      String status = this.userLoginService.validateTokenAtAccountActivation( token );

       if(status.equalsIgnoreCase("Valid"))
           return new ResponseEntity<ApiResponse>(new ApiResponse(status, true), HttpStatus.OK);
      else if(status.equalsIgnoreCase("Invalid"))
      return new ResponseEntity<ApiResponse>(new ApiResponse(status, false), HttpStatus.BAD_REQUEST);
        else if(status.equalsIgnoreCase("acctActivated"))
           return new ResponseEntity<ApiResponse>(new ApiResponse(status, true), HttpStatus.OK);

        return null;
    }
}
