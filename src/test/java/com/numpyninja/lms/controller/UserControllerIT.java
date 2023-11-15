package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
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
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

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

    //test create user with role
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

    //test get all users
    @Test
    @Order(2)
    public void testGetAllUsers() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //test get user by id
   // @Test
    @Order(3)
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

    //test update user
    @Test
    @Order(4)
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

    //test update user(roleId, programId, batchId, status) by userId
    @Test
    @Order(5)
    public void testAssignUpdateUserRoleProgramBatchStatus() throws Exception {

        final UserRoleProgramBatchDto userRoleProgramBatchDto = new UserRoleProgramBatchDto();
        userRoleProgramBatchDto.setUserId(userId);
        userRoleProgramBatchDto.setRoleId("R03");
        userRoleProgramBatchDto.setProgramId(1L);

        UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto = new UserRoleProgramBatchSlimDto();
        userRoleProgramBatchSlimDto.setBatchId(1);
        userRoleProgramBatchSlimDto.setUserRoleProgramBatchStatus("Active");

        userRoleProgramBatchDto.setUserRoleProgramBatches(Arrays.asList(userRoleProgramBatchSlimDto));

        String body = obj.writeValueAsString(userRoleProgramBatchDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/users/roleProgramBatchStatus/" + userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //test get user by programBatch id
    @Test
    @Order(6)
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
    @Order(7)
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

    //test get users by programId
    @Test
    @Order(8)
    public void testGetUsersByProgram() throws Exception {
        //apply the condition

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

    //test get all users with roles
    @Test
    @Order(9)
    public void testGetAllUsersWithRoles() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/roles").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }


    //test get users by roleId
    @Test
    @Order(10)
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

    //test update user login status
    @Test
    @Order(11)
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

    //test get users count by status
    @Test
    @Order(12)
    public void testGetUsersCountByStatus() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/users/byStatus").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    //test delete user
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
