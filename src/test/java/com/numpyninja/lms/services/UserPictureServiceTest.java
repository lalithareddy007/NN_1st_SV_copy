package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserPictureEntityDTO;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserPictureMapper;

import com.numpyninja.lms.repository.UserPictureRepository;
import com.numpyninja.lms.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserPictureServiceTest {

    @Mock
    private UserPictureRepository userPictureRepository;
    private UserPictureEntity mockUserPictureEntity;

    @Mock
    private UserPictureMapper userPictureMapper;
    private User user;

    private UserDto userDto;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserPictureService userPictureService;

    private UserPictureEntityDTO mockUserPictureEntityDTO;

    @BeforeEach
    public void setup() {
        setupUserPictureAndUserPictureDTO();
    }


    public UserPictureEntityDTO setupUserPictureAndUserPictureDTO() {


        mockUserPictureEntity = new UserPictureEntity(1L, "Resume", new User(), "/path/to/picture.jpg");

        mockUserPictureEntityDTO = new UserPictureEntityDTO(1L, "Resume", "U02", "/path/to/picture.jpg");
        user = new User("U02", "John", "K", " ", 1234123456L, "USA", "EST", "www.linkedin.com/John",
                "MS", "MBA", "Professor", "GC", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userDto = new UserDto("U02", "Lily", "K", " ", 8765987623L, "USA", "CST", "www.linkedin.com/Lily",
                "MCA", "MBA", "scientist", "Citizen", "lily.k@gmail.com");
        return mockUserPictureEntityDTO;
    }

    @DisplayName("test for uploadToDB method")
    @Test
    public void uploadToDB_shouldReturnSavedUserPictureEntityDto_whenSavedSuccessfully() throws IOException {
        // given
        UserPictureEntity userPictureEntity = new UserPictureEntity();
        userPictureEntity.setUserFileId(1L);
        userPictureEntity.setUserFileType("ProfilePic");

        UserPictureEntity savedUserPictureEntity = new UserPictureEntity();
        savedUserPictureEntity.setUserFilePath("/path/to/saved/picture.jpg");
        savedUserPictureEntity.setUserFileType("ProfilePic");
        savedUserPictureEntity.setUserFileId(1L);


        UserPictureEntityDTO savedUserPictureEntityDto = new UserPictureEntityDTO();
        savedUserPictureEntityDto.setUserFilePath("/path/to/saved/picture.jpg");


        when(userPictureRepository.findByuserAnduserFileType(mockUserPictureEntityDTO.getUserId(), mockUserPictureEntityDTO.getUserFileType()))
                .thenReturn(null);
        when(userPictureMapper.toUserPictureEntity(mockUserPictureEntityDTO)).thenReturn(userPictureEntity);
        when(userPictureRepository.save(userPictureEntity)).thenReturn(savedUserPictureEntity);
        when(userPictureMapper.toUserPictureEntityDto(savedUserPictureEntity)).thenReturn(savedUserPictureEntityDto);

        // when
        UserPictureEntityDTO result = userPictureService.uploadtoDB(mockUserPictureEntityDTO);

        // then
        assertNotNull(result);
        assertEquals(savedUserPictureEntityDto.getUserFilePath(), result.getUserFilePath());
        verify(userPictureRepository, times(1)).findByuserAnduserFileType(mockUserPictureEntityDTO.getUserId(), mockUserPictureEntityDTO.getUserFileType());
        verify(userPictureMapper, times(1)).toUserPictureEntity(mockUserPictureEntityDTO);
        verify(userPictureRepository, times(1)).save(userPictureEntity);
        verify(userPictureMapper, times(1)).toUserPictureEntityDto(savedUserPictureEntity);
    }

    @DisplayName("test for uploadToDB-DuplicateResourceFound method")
    @Test
    public void uploadToDB_shouldThrowDuplicateResourceFoundException_whenUserPictureEntityAlreadyExists() throws IOException {
        // given
        UserPictureEntity savedUserPictureEntity = new UserPictureEntity();
        when(userPictureRepository.findByuserAnduserFileType(mockUserPictureEntityDTO.getUserId(), mockUserPictureEntityDTO.getUserFileType()))
                .thenReturn(savedUserPictureEntity);

        // when, then
        assertThrows(DuplicateResourceFoundException.class, () -> userPictureService.uploadtoDB(mockUserPictureEntityDTO));
        verify(userPictureRepository, times(1)).findByuserAnduserFileType(mockUserPictureEntityDTO.getUserId(), mockUserPictureEntityDTO.getUserFileType());
        verify(userPictureMapper, never()).toUserPictureEntity(mockUserPictureEntityDTO);
        verify(userPictureRepository, never()).save(any(UserPictureEntity.class));
        verify(userPictureMapper, never()).toUserPictureEntityDto(any(UserPictureEntity.class));
    }

    @DisplayName("test for uploadToDB-ResourceNotFoundException method")
    @Test
    public void uploadtoDB_shouldThrowResourceNotFoundException_whenInvalidFileType() throws IOException {
        // given
        UserPictureEntityDTO userpicturedto = new UserPictureEntityDTO();
        userpicturedto.setUserFileId(1L);
        userpicturedto.setUserFileType("InvalidType");
        userpicturedto.setUserId("U01");
        userpicturedto.setUserFilePath("path/to/file.png");

        // when
        assertThrows(ResourceNotFoundException.class, () -> userPictureService.uploadtoDB(userpicturedto));

        // then
        verify(userPictureRepository, never()).save(Mockito.any(UserPictureEntity.class));
    }

    @DisplayName("test for Download method")
    @Test
    public void testDownload() {
        // Given
        String userId = "U01";
        String userFileType = "ProfilePic";

        UserPictureEntity pictureindb = new UserPictureEntity();
        pictureindb.setUserFileId(1L);
        pictureindb.setUserFileType("Resume");


        when(userPictureRepository.findByuserAnduserFileType(userId, userFileType)).thenReturn(pictureindb);
        when(userPictureMapper.toUserPictureEntityDto(pictureindb)).thenReturn(new UserPictureEntityDTO());

        // When
        UserPictureEntityDTO result = userPictureService.download(userId, userFileType);

        // Then
        verify(userPictureRepository).findByuserAnduserFileType(userId, userFileType);
        verify(userPictureMapper).toUserPictureEntityDto(pictureindb);
        assertNotNull(result);
    }

    @DisplayName("test for DeleteFile method")
    @Test
    public void testDeleteFile() throws Exception {
        // Given
        String userId = "U01";
        String fileType = "Resume";
        User user = new User(userId, "John", "Doe", " ", 1234567890L, "USA", "EST", "www.linkedin.com/John",
                "MS", "MBA", "Engineer", "GC", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        UserPictureEntity toDeletePicture = new UserPictureEntity(1L, fileType, user, "/path/to/picture.jpg");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userPictureRepository.findByuserAnduserFileType(userId, fileType)).thenReturn(toDeletePicture);

        // When
        userPictureService.DeleteFile(userId, fileType);


        // Then
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userPictureRepository, Mockito.times(1)).findByuserAnduserFileType(userId, fileType);
        Mockito.verify(userPictureRepository, Mockito.times(1)).deleteById(toDeletePicture.getUserFileId());
    }


    @DisplayName("test for UpdateFile-ResourceNotFound method")
    @Test
    public void testUpdateFileResourceNotFound() {
        //given
        String userId = "U02";
        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        //Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userPictureService.updateFile(mockUserPictureEntityDTO, userId);
        });
    }


    @DisplayName("test for UpdateFile method")
    @Test
    void testUpdateFile() throws IOException {
        // Given
        UserPictureEntityDTO userPictureDto = new UserPictureEntityDTO(3L, "ProfilePic", "U02", "/path/to/new/picture.jpg");
        UserPictureEntity savedPicture = new UserPictureEntity(3L, "ProfilePic", user, "/path/to/old/picture.jpg");
        UserPictureEntity newPicture = new UserPictureEntity(3L, "Resume", user, "/path/to/new/Resume.dc");
        UserPictureEntityDTO expectedDto = new UserPictureEntityDTO(3L, "Profile", "U02", "/path/to/new/picture.jpg");
        String userId = "U02";

        Mockito.when(userRepository.findById((userId))).thenReturn(Optional.of(user));
        Mockito.when(userPictureRepository.findByuserAnduserFileType(Mockito.anyString(), Mockito.anyString())).thenReturn(savedPicture);
        Mockito.when(userPictureMapper.toUserPictureEntity(userPictureDto)).thenReturn(newPicture);
        Mockito.when(userPictureMapper.toUserPictureEntityDto(newPicture)).thenReturn(expectedDto);
        Mockito.when(userPictureRepository.save(newPicture)).thenReturn(newPicture);

// When
        UserPictureEntityDTO actualDto = userPictureService.updateFile(userPictureDto, userId);

// Then
        assertEquals(expectedDto, actualDto);
        Mockito.verify(userPictureRepository, Mockito.times(1)).findByuserAnduserFileType(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(userPictureMapper, Mockito.times(1)).toUserPictureEntity(userPictureDto);
        Mockito.verify(userPictureMapper, Mockito.times(1)).toUserPictureEntityDto(newPicture);
        Mockito.verify(userPictureRepository, Mockito.times(1)).save(newPicture);}
}


