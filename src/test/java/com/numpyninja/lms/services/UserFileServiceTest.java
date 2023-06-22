package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserFileEntityDTO;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserFileEntity;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserFileMapper;

import com.numpyninja.lms.repository.UserFileRepository;
import com.numpyninja.lms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFileServiceTest {

    @Mock
    private UserFileRepository userFileRepository;
    private UserFileEntity mockUserFileEntity;

    @Mock
    private UserFileMapper userFileMapper;
    private User user;

    private UserDto userDto;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserFileService userFileService;

    private UserFileEntityDTO mockUserFileEntityDTO;

    @BeforeEach
    public void setup() {
        setupUserFileAndUserFileDTO();
    }


    public UserFileEntityDTO setupUserFileAndUserFileDTO() {


        mockUserFileEntity = new UserFileEntity(1L, "Resume", new User(), "/path/to/File.jpg");

        mockUserFileEntityDTO = new UserFileEntityDTO(1L, "Resume", "U02", "/path/to/File.jpg");
        user = new User("U02", "John", "K", " ", 1234123456L, "USA", "EST", "www.linkedin.com/John",
                "MS", "MBA", "Professor", "GC", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userDto = new UserDto("U02", "Lily", "K", " ", 8765987623L, "USA", "CST", "www.linkedin.com/Lily",
                "MCA", "MBA", "scientist", "Citizen", "lily.k@gmail.com");
        return mockUserFileEntityDTO;
    }

    @DisplayName("test for uploadToDB method")
    @Test
    public void uploadToDB_shouldReturnSavedUserFileEntityDto_whenSavedSuccessfully() throws IOException {
        // given
        UserFileEntity userFileEntity = new UserFileEntity();
        userFileEntity.setUserFileId(1L);
        userFileEntity.setUserFileType("ProfilePic");

        UserFileEntity savedUserFileEntity = new UserFileEntity();
        savedUserFileEntity.setUserFilePath("/path/to/saved/File.jpg");
        savedUserFileEntity.setUserFileType("ProfilePic");
        savedUserFileEntity.setUserFileId(1L);


        UserFileEntityDTO savedUserFileEntityDto = new UserFileEntityDTO();
        savedUserFileEntityDto.setUserFilePath("/path/to/saved/File.jpg");


        when(userFileRepository.findByuserAnduserFileType(mockUserFileEntityDTO.getUserId(), mockUserFileEntityDTO.getUserFileType()))
                .thenReturn(null);
        when(userFileMapper.toUserFileEntity(mockUserFileEntityDTO)).thenReturn(userFileEntity);
        when(userFileRepository.save(userFileEntity)).thenReturn(savedUserFileEntity);
        when(userFileMapper.toUserFileEntityDto(savedUserFileEntity)).thenReturn(savedUserFileEntityDto);

        // when
        UserFileEntityDTO result = userFileService.uploadtoDB(mockUserFileEntityDTO);

        // then
        assertNotNull(result);
        assertEquals(savedUserFileEntityDto.getUserFilePath(), result.getUserFilePath());
        verify(userFileRepository, times(1)).findByuserAnduserFileType(mockUserFileEntityDTO.getUserId(), mockUserFileEntityDTO.getUserFileType());
        verify(userFileMapper, times(1)).toUserFileEntity(mockUserFileEntityDTO);
        verify(userFileRepository, times(1)).save(userFileEntity);
        verify(userFileMapper, times(1)).toUserFileEntityDto(savedUserFileEntity);
    }

    @DisplayName("test for uploadToDB-DuplicateResourceFound method")
    @Test
    public void uploadToDB_shouldThrowDuplicateResourceFoundException_whenUserFileEntityAlreadyExists() throws IOException {
        // given
        UserFileEntity savedUserFileEntity = new UserFileEntity();
        when(userFileRepository.findByuserAnduserFileType(mockUserFileEntityDTO.getUserId(), mockUserFileEntityDTO.getUserFileType()))
                .thenReturn(savedUserFileEntity);

        // when, then
        assertThrows(DuplicateResourceFoundException.class, () -> userFileService.uploadtoDB(mockUserFileEntityDTO));
        verify(userFileRepository, times(1)).findByuserAnduserFileType(mockUserFileEntityDTO.getUserId(), mockUserFileEntityDTO.getUserFileType());
        verify(userFileMapper, never()).toUserFileEntity(mockUserFileEntityDTO);
        verify(userFileRepository, never()).save(any(UserFileEntity.class));
        verify(userFileMapper, never()).toUserFileEntityDto(any(UserFileEntity.class));
    }

    @DisplayName("test for uploadToDB-ResourceNotFoundException method")
    @Test
    public void uploadtoDB_shouldThrowResourceNotFoundException_whenInvalidFileType() throws IOException {
        // given
        UserFileEntityDTO userFiledto = new UserFileEntityDTO();
        userFiledto.setUserFileId(1L);
        userFiledto.setUserFileType("InvalidType");
        userFiledto.setUserId("U01");
        userFiledto.setUserFilePath("path/to/file.png");

        // when
        assertThrows(ResourceNotFoundException.class, () -> userFileService.uploadtoDB(userFiledto));

        // then
        verify(userFileRepository, never()).save(Mockito.any(UserFileEntity.class));
    }

    @DisplayName("test for Download method")
    @Test
    public void testDownload() {
        // Given
        String userId = "U01";
        String userFileType = "ProfilePic";

        UserFileEntity Fileindb = new UserFileEntity();
        Fileindb.setUserFileId(1L);
        Fileindb.setUserFileType("Resume");


        when(userFileRepository.findByuserAnduserFileType(userId, userFileType)).thenReturn(Fileindb);
        when(userFileMapper.toUserFileEntityDto(Fileindb)).thenReturn(new UserFileEntityDTO());

        // When
        UserFileEntityDTO result = userFileService.download(userId, userFileType);

        // Then
        verify(userFileRepository).findByuserAnduserFileType(userId, userFileType);
        verify(userFileMapper).toUserFileEntityDto(Fileindb);
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
        UserFileEntity toDeleteFile = new UserFileEntity(1L, fileType, user, "/path/to/File.jpg");

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userFileRepository.findByuserAnduserFileType(userId, fileType)).thenReturn(toDeleteFile);

        // When
        userFileService.DeleteFile(userId, fileType);


        // Then
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userFileRepository, Mockito.times(1)).findByuserAnduserFileType(userId, fileType);
        Mockito.verify(userFileRepository, Mockito.times(1)).deleteById(toDeleteFile.getUserFileId());
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
            userFileService.updateFile(mockUserFileEntityDTO, userId);
        });
    }


    @DisplayName("test for UpdateFile method")
    @Test
    void testUpdateFile() throws IOException {
        // Given
        UserFileEntityDTO userFileDto = new UserFileEntityDTO(3L, "ProfilePic", "U02", "/path/to/new/File.jpg");
        UserFileEntity savedFile = new UserFileEntity(3L, "ProfilePic", user, "/path/to/old/File.jpg");
        UserFileEntity newFile = new UserFileEntity(3L, "Resume", user, "/path/to/new/Resume.dc");
        UserFileEntityDTO expectedDto = new UserFileEntityDTO(3L, "Profile", "U02", "/path/to/new/File.jpg");
        String userId = "U02";

        Mockito.when(userRepository.findById((userId))).thenReturn(Optional.of(user));
        Mockito.when(userFileRepository.findByuserAnduserFileType(Mockito.anyString(), Mockito.anyString())).thenReturn(savedFile);
        Mockito.when(userFileMapper.toUserFileEntity(userFileDto)).thenReturn(newFile);
        Mockito.when(userFileMapper.toUserFileEntityDto(newFile)).thenReturn(expectedDto);
        Mockito.when(userFileRepository.save(newFile)).thenReturn(newFile);

// When
        UserFileEntityDTO actualDto = userFileService.updateFile(userFileDto, userId);

// Then
        assertEquals(expectedDto, actualDto);
        Mockito.verify(userFileRepository, Mockito.times(1)).findByuserAnduserFileType(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(userFileMapper, Mockito.times(1)).toUserFileEntity(userFileDto);
        Mockito.verify(userFileMapper, Mockito.times(1)).toUserFileEntityDto(newFile);
        Mockito.verify(userFileRepository, Mockito.times(1)).save(newFile);}
}


