package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.UserPictureEntityDTO;
import com.numpyninja.lms.services.UserPictureService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserPictureController.class)
@WithMockUser
public class UserPictureControllerTest extends AbstractTestController {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserPictureService userPictureService;

    UserPictureEntityDTO userPicEntityDto;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() {
        userPicEntityDto = new UserPictureEntityDTO(1L, "Resume", "U01", "C:/");
    }

    @Test
    void testDownload() throws Exception {
        final String userId = userPicEntityDto.getUserId();
        final String userFileType = userPicEntityDto.getUserFileType();

        given(userPictureService.download(userId, userFileType)).willReturn(userPicEntityDto);
        System.out.println(userPicEntityDto.toString());

        ResultActions response = mockMvc.perform(get("/file/userpicture/{userid}", userId)
                .param("userfiletype", userFileType).contentType(MediaType.APPLICATION_JSON));

        response.andDo(print()).andExpect(status().isOk());
        System.out.println("response : " + response.toString());

        response.andExpect(jsonPath("userId", is(userPicEntityDto.getUserId()), String.class))
                .andExpect(jsonPath("userFileType", is(userPicEntityDto.getUserFileType()), String.class))
                .andExpect(jsonPath("$.userFileId", is(userPicEntityDto.getUserFileId()), Long.class))
                .andExpect(jsonPath("$.userFilePath", is(userPicEntityDto.getUserFilePath()), String.class));

    }

    @Test
    public void testSave() throws Exception {

        given(userPictureService.uploadtoDB(ArgumentMatchers.any(UserPictureEntityDTO.class)))
                .willAnswer((i) -> i.getArgument(0));
        // when
        ResultActions resultActions = mockMvc.perform(post("/file/userpicture").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userPicEntityDto)));
        // then
        resultActions.andExpect(status().isCreated()).andDo(print())
                .andExpect((ResultMatcher) jsonPath("$.userId", equalTo(userPicEntityDto.getUserId()), String.class))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.userFilePath", equalTo(userPicEntityDto.getUserFilePath())));
    }

    @Test
    public void testUpdate() throws Exception {
        final String userId = userPicEntityDto.getUserId();
        UserPictureEntityDTO updatePicEntityDto = userPicEntityDto;
        when(userPictureService.updateFile(updatePicEntityDto, userId)).thenReturn(updatePicEntityDto);

        ResultActions response = mockMvc.perform(put("/file/userpicture/{userid}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePicEntityDto)));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", CoreMatchers.is(updatePicEntityDto.getUserId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userFilePath", CoreMatchers.is(updatePicEntityDto.getUserFilePath())));

    }

    @Test
    public void testdelete() throws Exception {

        final String userId = userPicEntityDto.getUserId();
        final String userFileType = userPicEntityDto.getUserFileType();

        doNothing().when(userPictureService).DeleteFile(userId, userFileType);

        ResultActions response = mockMvc.perform(delete("/file/userpicture/{userid}", userId).param("userfiletype", userFileType)
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());

    }


}
