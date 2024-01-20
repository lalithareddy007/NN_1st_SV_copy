package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.security.WebSecurityConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(UserController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@ContextConfiguration(classes = {WebSecurityConfig.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class UserControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    private static String userId;

    private static String roleId;

    private static Integer batchId;

    private static Long programId;

    private String userRoleStatus;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // fetch a token
        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");
        //convert obj to json or string
        final String loginBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(loginBody))
                .andReturn();
        // validating the expected value and actual value
        assertEquals(200, mvcResult.getResponse().getStatus());

        //convert json to obj
        String loginResponseBody = mvcResult.getResponse().getContentAsString();
        final JwtResponseDto jwtResponseDto = obj.readValue(loginResponseBody, JwtResponseDto.class);
        token = jwtResponseDto.getToken();

        assertNotNull(token, "token is null");
    }

    @Test
    @Order(1)
    public void testCreateUserWithRole() throws Exception {

        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Alex");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762345676L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("PST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("alex@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());

        UserDto userDto = obj.readValue(jsonResponse, UserDto.class);
        assertEquals("Alex", userDto.getUserFirstName());
        assertEquals("well", userDto.getUserLastName());
        assertEquals(null, userDto.getUserMiddleName());
        assertEquals(8762345676L, userDto.getUserPhoneNumber());
        assertEquals("LA", userDto.getUserLocation());
        assertEquals("PST", userDto.getUserTimeZone());
        assertEquals("https://www.linkedin.com/in/Alex/", userDto.getUserLinkedinUrl());
        assertEquals("IT Engineering", userDto.getUserEduUg());
        assertEquals("M.Tech", userDto.getUserEduPg());
        assertEquals(null, userDto.getUserComments());
        assertEquals("H4-EAD", userDto.getUserVisaStatus());
        assertEquals("alex@gmail.com", userDto.getUserLoginEmail());

        userId = userDto.getUserId();
        roleId = "R03";
        userRoleStatus = "Active";
    }

    @Test
    @Order(2)
    public void testPhoneNumberAlreadyExist() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Alex");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762345676L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("PST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("alex@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false , apiResponse.isSuccess());
        assertEquals("Failed to create new User as phone number 8762345676 already exists !!", message);
    }

    @Test
    @Order(3)
    public void testInvalidRoleId() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Alex");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762345666L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("PST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("alex12@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R04");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Failed to create user, as 'roleId' is invalid !! ", message);
    }

    @Test
    @Order(4)
    public void testEmailAlreadyExists() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Alex");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762345345L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("PST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("alex@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Failed to create new UserLogin as email already exists!", message);
    }

    @Test
    @Order(5)
    public void testPhoneNumberInCorrectFormat() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Alexa");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(123456789123L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("PST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("alex12@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Enter phone no in this format (CountryCode)(PhoneNo) +91 1234567890", message);
    }

    @Test
    @Order(6)
    public void testInvalidTimeZone() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("Huda");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762342546L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("SST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("huda@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Failed to create user, as 'TimeZone' is invalid !! ", message);
    }

    @Test
    @Order(7)
    public void testInvalidVisaStatus() throws Exception {
        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("John");
        userLoginRoleDTO.setUserLastName("well");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(8762345889L);
        userLoginRoleDTO.setUserLocation("LA");
        userLoginRoleDTO.setUserTimeZone("CST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userLoginRoleDTO.setUserEduUg("IT Engineering");
        userLoginRoleDTO.setUserEduPg("M.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H6");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("john@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R02");
        userRoleMapSlimDTO.setUserRoleStatus("Active");

        List<UserRoleMapSlimDTO> userRoleMapsList = new ArrayList<>();
        userRoleMapsList.add(userRoleMapSlimDTO);

        userLoginRoleDTO.setUserRoleMaps(userRoleMapsList);

        String jsonRequest = obj.writeValueAsString(userLoginRoleDTO);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/users/roleStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Failed to create user, as 'Visa Status' is invalid !! ", message);
    }

    @Test
    @Order(8)
    public void testGetAllUsers() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }


    @Test
    @Order(9)
    public void testGetOneById() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        UserAllDto userAllDto = obj.readValue(jsonResponse, UserAllDto.class);
        assertEquals("Alex", userAllDto.getUserDto().getUserFirstName());
        assertEquals("well", userAllDto.getUserDto().getUserLastName());
        assertEquals(8762345676L, userAllDto.getUserDto().getUserPhoneNumber());
        assertEquals("LA", userAllDto.getUserDto().getUserLocation());
        assertEquals("PST", userAllDto.getUserDto().getUserTimeZone());
        assertEquals("https://www.linkedin.com/in/Alex/", userAllDto.getUserDto().getUserLinkedinUrl());
        assertEquals("IT Engineering", userAllDto.getUserDto().getUserEduUg());
        assertEquals("M.Tech", userAllDto.getUserDto().getUserEduPg());
        assertEquals("H4-EAD", userAllDto.getUserDto().getUserVisaStatus());
        assertEquals("R03", userAllDto.getUserRoleMaps().get(0).getRoleId());
        assertEquals("Active", userAllDto.getUserRoleMaps().get(0).getUserRoleStatus());
    }

    @Order(10)
    public void testGetUserByInvalidId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/" + "U12").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Id : U12 ", message);
    }

    @Test
    @Order(11)
    public void testUpdateUser() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserFirstName("Alex");
        userDto.setUserLastName("well");
        userDto.setUserMiddleName(null);
        userDto.setUserPhoneNumber(8762345676L);
        userDto.setUserLocation("LA");
        userDto.setUserTimeZone("PST");
        userDto.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userDto.setUserEduUg("IT Engineering");
        userDto.setUserEduPg("PHD");
        userDto.setUserComments(null);
        userDto.setUserVisaStatus("H4-EAD");
        userDto.setUserLoginEmail("alex@gmail.com");


        String body = obj.writeValueAsString(userDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        UserDto dto = obj.readValue(jsonResponse, UserDto.class);
        assertEquals("PHD", dto.getUserEduPg());
    }
    @Test
    @Order(12)
    public void testUpdateUserByInvalidId() throws Exception {
        final UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setUserFirstName("Alex");
        userDto.setUserLastName("well");
        userDto.setUserMiddleName(null);
        userDto.setUserPhoneNumber(8762345676L);
        userDto.setUserLocation("LA");
        userDto.setUserTimeZone("PST");
        userDto.setUserLinkedinUrl("https://www.linkedin.com/in/Alex/");
        userDto.setUserEduUg("IT Engineering");
        userDto.setUserEduPg("PHD");
        userDto.setUserComments(null);
        userDto.setUserVisaStatus("H4-EAD");
        userDto.setUserLoginEmail("alex@gmail.com");

        String body = obj.writeValueAsString(userDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/" + "U12").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("UserID: U12 Not Found", message);
    }

    //test update user(roleId, programId, batchId, status) by userId
    @Test
    @Order(13)
    public void testAssignUpdateUserRoleProgramBatchStatus() throws Exception {
        final UserRoleProgramBatchDto userRoleProgramBatchDto = new UserRoleProgramBatchDto();
        userId = "U02";
        userRoleProgramBatchDto.setUserId(userId);
        userRoleProgramBatchDto.setRoleId("R02");
        userRoleProgramBatchDto.setProgramId(1L);

        UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto = new UserRoleProgramBatchSlimDto();
        userRoleProgramBatchSlimDto.setBatchId(1);
        userRoleProgramBatchSlimDto.setUserRoleProgramBatchStatus("Active");

        userRoleProgramBatchDto.setUserRoleProgramBatches(Arrays.asList(userRoleProgramBatchSlimDto));

        String body = obj.writeValueAsString(userRoleProgramBatchDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleProgramBatchStatus/{userId}" , userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();

        ApiResponse apiResponse = obj.readValue(responseBody, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(200,mvcResult.getResponse().getStatus());
        assertEquals(true,apiResponse.isSuccess());
        assertEquals("User "+userId+" has been successfully assigned to Program/Batch(es)",message);
    }

    @Test
    @Order(14)
    public void testUpdateUserByInvalidRoleId() throws Exception {
        final UserRoleProgramBatchDto userRoleProgramBatchDto = new UserRoleProgramBatchDto();
        userRoleProgramBatchDto.setUserId(userId);
        userRoleProgramBatchDto.setRoleId("R04");
        userRoleProgramBatchDto.setProgramId(2L);

        UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto = new UserRoleProgramBatchSlimDto();
        userRoleProgramBatchSlimDto.setBatchId(2);
        userRoleProgramBatchSlimDto.setUserRoleProgramBatchStatus("Active");

        userRoleProgramBatchDto.setUserRoleProgramBatches(Arrays.asList(userRoleProgramBatchSlimDto));

        String body = obj.writeValueAsString(userRoleProgramBatchDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleProgramBatchStatus/" + userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Role not found with Id : R04 ", message);
    }

    @Test
    @Order(15)
    public void testBatchStatusIsActiveForProgramId() throws Exception {
        final UserRoleProgramBatchDto userRoleProgramBatchDto = new UserRoleProgramBatchDto();
        userRoleProgramBatchDto.setUserId(userId);
        userRoleProgramBatchDto.setRoleId("R03");
        userRoleProgramBatchDto.setProgramId(3L);

        UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto = new UserRoleProgramBatchSlimDto();
        userRoleProgramBatchSlimDto.setBatchId(2);
        userRoleProgramBatchSlimDto.setUserRoleProgramBatchStatus("Active");

        userRoleProgramBatchDto.setUserRoleProgramBatches(Arrays.asList(userRoleProgramBatchSlimDto));

        String body = obj.writeValueAsString(userRoleProgramBatchDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleProgramBatchStatus/" + userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Batch 2 not found with Status as Active for Program 3 " + " \n ", message);
    }

    @Test
    @Order(16)
    public void testGetUserByProgramBatch() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/users/programBatch/" + 1).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("/application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<UserDto> userDtoList = obj.readValue(jsonResponse, new TypeReference<List<UserDto>>() {
        });
        UserDto userDtoForUserId = null;
        for (UserDto userDto: userDtoList) {
            if(userId.equals(userDto.getUserId())) {
                userDtoForUserId = userDto;
                break;
            }
        }
        assertNotNull(userDtoForUserId, "userDtoForUserId is null");
        assertEquals("Alex", userDtoForUserId.getUserFirstName());
        assertEquals("well", userDtoForUserId.getUserLastName());
        assertEquals(8762345676L, userDtoForUserId.getUserPhoneNumber());
        assertEquals("LA", userDtoForUserId.getUserLocation());
        assertEquals("PST", userDtoForUserId.getUserTimeZone());
        assertEquals("https://www.linkedin.com/in/Alex/", userDtoForUserId.getUserLinkedinUrl());
        assertEquals("IT Engineering", userDtoForUserId.getUserEduUg());
        assertEquals("PHD", userDtoForUserId.getUserEduPg());
        assertEquals("H4-EAD", userDtoForUserId.getUserVisaStatus());
    }

    //test update user role status
    @Test
    @Order(17)
    public void testUpdateUserRoleStatus() throws Exception {

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("active");

        String body = obj.writeValueAsString(userRoleMapSlimDTO);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleStatus/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Order(18)
    public void testUpdateUserRoleByInvalidUserId() throws Exception {
        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R03");
        userRoleMapSlimDTO.setUserRoleStatus("active");

        String body = obj.writeValueAsString(userRoleMapSlimDTO);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleStatus/" + "U21").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("UserID: U21 Not Found", message);
    }

    @Test
    @Order(19)
    public void testUpdateUserRoleStatusByInvalidRoleId() throws Exception {
        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R02");
        userRoleMapSlimDTO.setUserRoleStatus("active");

        String body = obj.writeValueAsString(userRoleMapSlimDTO);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleStatus/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("RoleID: R02 not found for the UserID: "+userId, message);
    }

    @Test
    @Order(20)
    public void testGetUsersByProgram() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/programs/" + 1).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<UserDto> userDtoList = obj.readValue(jsonResponse, new TypeReference<List<UserDto>>() {
        });
        UserDto userDtoForUserId = null;
        for(UserDto userDto: userDtoList){
            if(userId.equals(userDto.getUserId())) {
                userDtoForUserId = userDto;
                break;
            }
        }
        assertNotNull(userDtoForUserId, "userDtoForUserId is null");
        assertEquals("Alex", userDtoForUserId.getUserFirstName());
        assertEquals("well", userDtoForUserId.getUserLastName());
        assertEquals(8762345676L, userDtoForUserId.getUserPhoneNumber());
        assertEquals("LA", userDtoForUserId.getUserLocation());
        assertEquals("PST", userDtoForUserId.getUserTimeZone());
        assertEquals("https://www.linkedin.com/in/Alex/", userDtoForUserId.getUserLinkedinUrl());
        assertEquals("IT Engineering", userDtoForUserId.getUserEduUg());
        assertEquals("PHD", userDtoForUserId.getUserEduPg());
        assertEquals("H4-EAD", userDtoForUserId.getUserVisaStatus());
    }

    @Test
    @Order(21)
    public void testGetUsersByInvalidProgramId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/programs/" + 4).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("programId 4 not found", message);
    }

    @Test
    @Order(22)
    public void testGetAllUsersWithRoles() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/roles").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(23)
    public void testGetUserByRoleId() throws Exception {
        //apply the condition

        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/roles/" + roleId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<UserDto> userDtoList = obj.readValue(jsonResponse, new TypeReference<List<UserDto>>() {
        });
        UserDto userDtoForUserId = null;
        for(UserDto userDto: userDtoList){
            if(userId.equals(userDto.getUserId())) {
                userDtoForUserId = userDto;
                break;
            }
        }
        assertNotNull(userDtoForUserId, "userDtoForUserId is null");
        assertEquals("Alex", userDtoForUserId.getUserFirstName());
        assertEquals("well", userDtoForUserId.getUserLastName());
        assertEquals(8762345676L, userDtoForUserId.getUserPhoneNumber());
        assertEquals("LA", userDtoForUserId.getUserLocation());
        assertEquals("PST", userDtoForUserId.getUserTimeZone());
        assertEquals("https://www.linkedin.com/in/Alex/", userDtoForUserId.getUserLinkedinUrl());
        assertEquals("IT Engineering", userDtoForUserId.getUserEduUg());
        assertEquals("PHD", userDtoForUserId.getUserEduPg());
        assertEquals("H4-EAD", userDtoForUserId.getUserVisaStatus());
    }

    @Order(24)
    public void testGetUserByInvalidRoleId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/roles/" + "R04").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("RoleID R04 not found", message);
    }

    @Test
    @Order(25)
    public void testUpdateUserLoginStatus() throws Exception {
        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setLoginStatus("Active");
        userLoginDto.setStatus("active");
        userLoginDto.setRoleIds(Arrays.asList("R03"));
        userLoginDto.setUserLoginEmail("alexa@gmail.com");

        String body = obj.writeValueAsString(userLoginDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/userLogin/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(26)
    public void testGetUsersCountByStatus() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/byStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteUser() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/users/" + userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
