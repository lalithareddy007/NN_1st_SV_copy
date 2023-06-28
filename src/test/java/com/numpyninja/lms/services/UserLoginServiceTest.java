package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.EmailDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.entity.EmailDetails;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.JwtUtils;
import com.numpyninja.lms.util.EmailSender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserLoginServiceTest {
	@InjectMocks
	UserLoginService userLoginService;
	@Mock
	private UserLoginRepository userLoginRepository;
	@Mock
	private UserRoleMapRepository userRoleMapRepository;
	@Mock
	AuthenticationManager authenticationManager;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private UserCache userCache;

	@Value("${app.frontend.url}")
	private String frontendUrl;

	@Mock
	private UserRepository userRepository;

	@Mock
	EmailSender emailSender;

	UserLoginService userServiceLogin;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

	}

	@DisplayName("JUnit test for signin method for Valid LoginDetails")
	@Test
	public void givenValidLoginDetails_WhenSignIn_ReturnJwtResponseDto() {
		LoginDto loginDto = new LoginDto();
		loginDto.setUserLoginEmailId("vijaybharathi@gmail.com");
		loginDto.setPassword("lksez$");

		List authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));

		// given
		UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
		when(userCache.getUserFromCache(loginDto.getUserLoginEmailId())).thenReturn(userDetails);
		Authentication authentication = mock(Authentication.class);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);

		String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGVudGhhbWFyYWkubjJAZ21haWwuY29tIiwiaWF0IjoxNjg0NDU0NDYwLCJleH";
		when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);

		when(authentication.getPrincipal()).thenReturn(userDetails);

		when(userDetails.getUserId()).thenReturn("U11");

		when(userDetails.getAuthorities()).thenReturn(authorities);

		//when
		JwtResponseDto jwtResponseDtoGot = userLoginService.signin(loginDto);

		// then
		assertEquals(jwtResponseDtoGot.getEmail(), loginDto.getUserLoginEmailId());
		assertEquals(jwtResponseDtoGot.getToken(), jwtToken);
		assertEquals(jwtResponseDtoGot.getUserId(), "U11");
		assertEquals(jwtResponseDtoGot.getRoles(), Collections.singletonList("ROLE_STAFF"));
	}

	@Mock
	UserLoginService userLoginService2;

	@Test
	public void test_CreateEmailUrlConfirmPassword_withToken() {

		String loginEmail = "vijaybharathi@gmail.com";
		String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGVudGhhbWFyYWkubjJAZ21haWwuY29tIiwiaWF0IjoxNjg0NDU0NDYwLCJleH";

		//given
		when(userLoginService2.getFrontendURL()).thenReturn(frontendUrl);
		when(userLoginService2.createEmailUrlConfirmPwdWithToken(loginEmail, token)).thenCallRealMethod();

		// when
		String url = userLoginService2.createEmailUrlConfirmPwdWithToken(loginEmail, token);

		// Assert
		String expectedUrl = frontendUrl + "/reset-password?token=" + token;
		assertEquals(expectedUrl, url);
	}

	@Test
	public void test_ForgotPasswordConfirmEmail_InvalidUser() {

		//given
		EmailDto emailDto = new EmailDto();
		emailDto.setUserLoginEmailId("varun@gmail.com");
 		when(userLoginRepository.findByUserLoginEmailIgnoreCase("varun@gmail.com")).thenReturn(Optional.empty());

		//when
		JwtResponseDto result = userLoginService.forgotPasswordConfirmEmail(emailDto);

		// Assert
		assertEquals("varun@gmail.com", result.getEmail());
		assertEquals("null", result.getToken());
		assertEquals("Invalid Email", result.getStatus());
	}

	@Test
	public void test_ForgotPasswordConfirmEmail_ValidUser_InactiveLoginStatus() {
		//given
		EmailDto emailDto = new EmailDto();
		emailDto.setUserLoginEmailId("varun@gmail.com");

		UserLogin userLogin = new UserLogin();
		userLogin.setUserLoginEmail("varun@gmail.com");
		userLogin.setLoginStatus("inactive");
		userLogin.setUserId("U01");

		User user = new User();
		user.setUserId("U01");
		user.setUserFirstName("Varun");

		when(userLoginRepository.findByUserLoginEmailIgnoreCase("varun@gmail.com")).thenReturn(Optional.of(userLogin));
		when(userRepository.findById(userLogin.getUserId())).thenReturn(Optional.of(user));

		//when
		JwtResponseDto result = userLoginService.forgotPasswordConfirmEmail(emailDto);

		//assert
		assertEquals("varun@gmail.com", result.getEmail().toString());
		assertEquals("null", result.getToken());
		assertEquals("login inactive", result.getStatus());
	}


	@DisplayName("JUnit test for Validating at Account activation")
	@ParameterizedTest
	@MethodSource("validationTestData")
	public void testValidateTokenAtAccountActivation(String token, String expectedValidity) {
		String tokenparse = null;
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			tokenparse = token.substring(7, token.length());
		}

		when(jwtUtils.validateAccountActivationToken(tokenparse))
				.thenReturn(expectedValidity);

		if (expectedValidity.equalsIgnoreCase("Valid")) {
			String userName = jwtUtils.getUserNameFromJwtToken(tokenparse);
			Optional<UserLogin> userOptional =  userLoginRepository.findByUserLoginEmailIgnoreCase(userName);

		}

		String validity = userLoginService.validateTokenAtAccountActivation(token);

	}
	private static Stream<Arguments> validationTestData() {
		return Stream.of(
				Arguments.of("Bearer ValidToken", "Valid"),
				Arguments.of("Bearer ValidToken", "acctActivated already"),
				Arguments.of("InvalidToken", "Invalid")
		);
	}

	@DisplayName("JUnit test for Reset Password when token is valid ")
	@Test
	void testResetPassword_ValidToken_PasswordSaved() {
		// given
		LoginDto loginDto = new LoginDto();
		loginDto.setUserLoginEmailId("test@example.com");
		loginDto.setPassword("newpassword");
		String token = "Bearer valid_token";
		String expectedStatus = "Password saved";
		//when
		when(jwtUtils.validateJwtToken("valid_token")).thenReturn(true);
		UserLogin userLogin = new UserLogin();
		when(userLoginRepository.findByUserLoginEmailIgnoreCase(loginDto.getUserLoginEmailId()))
				.thenReturn(Optional.of(userLogin));
		String status = userLoginService.resetPassword(loginDto, token);
		// then
		assertEquals(expectedStatus, status);

	}

	@DisplayName("JUnit test for Reset Password when token is invalid")
	@Test
	void testResetPassword_InvalidTokenReset_ReturnsInvalid() {
		// given
		LoginDto loginDto = new LoginDto();
		String token = "Bearer invalid_token";
		String expectedStatus = "Invalid";
		//when
		when(jwtUtils.validateJwtToken("invalid_token")).thenReturn(false);
		String status = userLoginService.resetPassword(loginDto, token);
		// then
		assertEquals(expectedStatus, status);
	}
}



