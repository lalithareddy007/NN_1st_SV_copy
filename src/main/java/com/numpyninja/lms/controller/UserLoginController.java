package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.EmailDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.services.UserLoginService;

import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Api(tags = "User Login Controller", description = "User Login Authentication")
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
    @ApiOperation("User Sign In")
    public ResponseEntity<JwtResponseDto> signin(@Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userLoginService.signin(loginDto));
    }


    @GetMapping("/logoutlms")
    @ApiOperation("User log out")
    public ResponseEntity<String> logout() {
        userLoginService.logout();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/login/AccountActivation")
    public ResponseEntity<ApiResponse> validateAccountActToken(
            @RequestHeader(value = "Authorization") String token) {
        String validity = this.userLoginService.validateTokenAtAccountActivation(token);

        if(validity.equalsIgnoreCase("Invalid"))
            return new ResponseEntity<ApiResponse>(new ApiResponse("Invalid/Expired Token", false), HttpStatus.BAD_REQUEST);
        else if(validity.equalsIgnoreCase("acctActivated already"))
            return new ResponseEntity<ApiResponse>(new ApiResponse(validity, false), HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse(validity, true), HttpStatus.OK); // validity has email id in this case
    }

    @PostMapping("/resetPassowrd")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody LoginDto logindto,
                                                     @RequestHeader(value = "Authorization") String token) {
        String status = this.userLoginService.resetPassword(logindto, token);
        if (status.equalsIgnoreCase("Password saved"))
            return new ResponseEntity<ApiResponse>(new ApiResponse(status, true), HttpStatus.OK);
        else if (status.equalsIgnoreCase("Invalid"))
            return new ResponseEntity<ApiResponse>(new ApiResponse(status, false), HttpStatus.BAD_REQUEST);
        return null;
    }
    
    @PostMapping("/login/forgotpassword/confirmEmail")
	@ApiOperation("ForgotPassword Confirm Email")
	public ResponseEntity<JwtResponseDto> forgotPasswordConfirmEmail(@Valid @RequestBody EmailDto userLoginEmail) throws InvalidDataException {
    	JwtResponseDto forgotPassResDto = userLoginService.forgotPasswordConfirmEmail(userLoginEmail);
		String status=forgotPassResDto.getStatus();
		if (status.equalsIgnoreCase("Invalid Email")) {
			System.out.println("Invalid Email Id. Id not registered");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(forgotPassResDto);
		}
		else {
			System.out.println("Valid EMail Id. Status : Ok");
			return ResponseEntity.status(HttpStatus.CREATED).body(forgotPassResDto);
		}
	}
    
    @GetMapping("/validateToken")
    public ResponseEntity<ApiResponse> validateToken(@RequestHeader(value = "Authorization") String token) {
        boolean status = this.userLoginService.validateToken(token);

        if (status)
            return new ResponseEntity<ApiResponse>(new ApiResponse("Valid", true), HttpStatus.OK);
        else
            return new ResponseEntity<ApiResponse>(new ApiResponse("InValid", false), HttpStatus.BAD_REQUEST);
    }
}
