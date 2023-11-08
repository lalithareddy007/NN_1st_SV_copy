package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserSkillRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.numpyninja.lms.config.ApiResponse;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(UserSkillController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class UserSkillControllerIntegrationTest {

    private static  MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    private UserSkillRepository userSkillRepository;

    @Autowired
    private UserRepository userRepository;

    ObjectMapper obj = new ObjectMapper();

    private static String token;

    private static String userId;

    private static String userSkillId;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void beforeSetup() throws Exception {
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
    @Order(1)
    @Test
    public void testCreateUserWithRole() throws Exception {

        final UserLoginRoleDTO userLoginRoleDTO = new UserLoginRoleDTO();
        userLoginRoleDTO.setUserFirstName("IntegrationTest");
        userLoginRoleDTO.setUserLastName("testIntegration");
        userLoginRoleDTO.setUserMiddleName(null);
        userLoginRoleDTO.setUserPhoneNumber(1625364956L);
        userLoginRoleDTO.setUserLocation("WA");
        userLoginRoleDTO.setUserTimeZone("EST");
        userLoginRoleDTO.setUserLinkedinUrl("https://www.linkedin.com/in/IntegrationTest1625/");
        userLoginRoleDTO.setUserEduUg("Comp Engineering");
        userLoginRoleDTO.setUserEduPg("B.Tech");
        userLoginRoleDTO.setUserComments(null);
        userLoginRoleDTO.setUserVisaStatus("H4-EAD");

        final UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setUserLoginEmail("integrationtest1625@gmail.com");
        userLoginDto.setLoginStatus("active");

        userLoginRoleDTO.setUserLogin(userLoginDto);

        final UserRoleMapSlimDTO userRoleMapSlimDTO = new UserRoleMapSlimDTO();
        userRoleMapSlimDTO.setRoleId("R01");
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
        userId = userDto.getUserId();
    }

        @Test
    @Order(2)
    public void createUserSkillTest() throws Exception {
        final UserSkillDTO userSkillDto;

        userSkillDto = new UserSkillDTO();
        userSkillDto.setUserId("U05");
        userSkillDto.setSkillId(6);
        userSkillDto.setMonths(20);

        String jsonRequest = obj.writeValueAsString(userSkillDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/userSkill/create").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());


        UserSkillDTO userskillDto = obj.readValue(jsonResponse, UserSkillDTO.class);
        assertEquals("U05", userskillDto.getUserId());
        assertEquals(6, userskillDto.getSkillId());
        assertEquals(20, userskillDto.getMonths());

        // Retrieve the userSkillDTO with the generated ID
        userskillDto = obj.readValue(jsonResponse, UserSkillDTO.class);
        assertNotNull(userskillDto.getUserSkillId(), "UserSkillId is null");
        userSkillId = userskillDto.getUserSkillId();

    }


    @Test
    @Order(3)
    public void testGetAllUserSkills() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/userSkill").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(4)
    public void testGetUserSkillForUser() throws Exception {
        String userId = userSkillRepository.findAll().get(0).getUser().getUserId();
        final MvcResult mvcResult = mockMvc.perform(get("/lms/userSkill/", userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(5)
    public void testUpdateUserSkill() throws Exception {

        final UserSkillDTO updateUserSkillDto = new UserSkillDTO();
        updateUserSkillDto.setUserSkillId(userSkillId);
        updateUserSkillDto.setUserId("U05");
        updateUserSkillDto.setSkillId(6);
        updateUserSkillDto.setMonths(30);

        String body = obj.writeValueAsString(updateUserSkillDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/userSkill/" + userSkillId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(body))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        UserSkillDTO dto = obj.readValue(jsonResponse, UserSkillDTO.class);
        assertEquals(30, dto.getMonths());
    }

    @Test
    @Order(6)
    public void UpdateUserSkillIfSkillNotExist() throws Exception {
        final UserSkillDTO userSkillDto;

        userSkillDto = new UserSkillDTO();
        userSkillDto.setUserId("U30");
        userSkillDto.setSkillName("Postgres");
        userSkillDto.setSkillId(6);
        userSkillDto.setMonths(20);

        String jsonRequest = obj.writeValueAsString(userSkillDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/userSkill/" + 100).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();
        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User" + "Id", "UserId");

    }

    @Test
    @Order(7)
    public void testGetUserSkillForUserIfUserNotExist() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(get("/lms/userSkill/user/{userId}", "U50")
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Id : U50 ", message);


    }

    @Test
    @Order(8)
    public void testDeleteUserSkillByUserSkillIdIfIdNotExist() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/userSkill/deleteByUserSkillId/" + "US90").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("UserSkill not found with Id : US90 ", message);
    }

    @Test
    @Order(9)
    @Transactional
    @Rollback(false)
    public void testDeleteUser() throws Exception {

        String deleteQuery = "DELETE FROM User e WHERE e.id = :value";
        Query query = entityManager.createQuery(deleteQuery);
        query.setParameter("value", userId);

        int deletedCount = query.executeUpdate();

        //you can assert that the expected number of rows were deleted.
        assertEquals(1, deletedCount);
    }
    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteUserSkillByUserSkillId() throws Exception {

        final MvcResult mvcResult = mockMvc.perform(delete("/lms/userSkill/deleteByUserSkillId/" + userSkillId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }


}
