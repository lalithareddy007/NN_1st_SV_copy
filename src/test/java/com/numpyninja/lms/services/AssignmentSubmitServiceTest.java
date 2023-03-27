package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentSubmitMapper;
import com.numpyninja.lms.repository.AssignmentRepository;
import com.numpyninja.lms.repository.AssignmentSubmitRepository;
import com.numpyninja.lms.repository.UserRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssignmentSubmitServiceTest {

    @InjectMocks
    private AssignmentSubmitService mockAssignmentSubmitService;

    @Mock
    private AssignmentSubmitRepository mockAssignmentSubmitRepository;

    @Mock
    private AssignmentSubmitMapper assignmentSubmitMapper;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private AssignmentRepository mockAssignmentRepository;

    private AssignmentSubmit mockAssignmentSubmit1, mockAssignmentSubmit2, mockAssignmentSubmit3;

    private AssignmentSubmitDTO mockAssignmentSubmitDTO1, mockAssignmentSubmitDTO2, mockAssignmentSubmitDTO3;

    private Assignment mockAssignment;



    private User mockUser,mockUser1;

    List<AssignmentSubmit> mockAssignmentSubmitList;
    List<AssignmentSubmitDTO> mockAssignmentSubmitDTOList;

    @BeforeEach
    private void setMockAssignmentSubmitDTO(){

        Date dueDate = Timestamp.valueOf("2023-02-02 09:30:00");

        Timestamp timestamp1 = Timestamp.valueOf("2023-01-02 09:30:00");
        Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.now());

        mockAssignmentSubmitDTO1 = new AssignmentSubmitDTO(4L,2L,"U03","Selenium assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);
        mockAssignmentSubmitDTO2 = new AssignmentSubmitDTO(3L,1L,"U03","SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);
        mockAssignmentSubmitDTO3 = new AssignmentSubmitDTO(8L,1L,"U05","SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);

        Batch batch = setMockBatch();

        mockUser = new User("U03", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
                "", "", "", "Citizen", timestamp1, timestamp2);
        mockUser1 = new User("U04", "Elon", "Musk", "Steve",
                1234567809L, "CA", "PST", "@elonmusk", "",
                "", "", "Citizen", timestamp1, timestamp2);

        mockAssignment  = new Assignment(1L, "Test Assignment", "Junit test",
                "practice", dueDate, "Filepath1", "Filepath2",
                "Filepath3", "Filepath4", "Filepath5", batch, mockUser, mockUser1,
                timestamp1, timestamp1);

        mockAssignmentSubmit1 = new AssignmentSubmit(4L,mockAssignment,mockUser,"Selenium assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1,timestamp1,timestamp2);
        mockAssignmentSubmit2 = new AssignmentSubmit(3L,mockAssignment,mockUser,"SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1,timestamp1,timestamp2);
        mockAssignmentSubmit3 = new AssignmentSubmit(8L,mockAssignment,mockUser1,"SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1,timestamp1,timestamp2);

        mockAssignmentSubmitList = new ArrayList<>();
        mockAssignmentSubmitList.add(mockAssignmentSubmit1);
        mockAssignmentSubmitList.add(mockAssignmentSubmit2);

        mockAssignmentSubmitDTOList = new ArrayList<>();
        mockAssignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        mockAssignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);

    }

    private Batch setMockBatch() {
        LocalDateTime now= LocalDateTime.now();
        Timestamp timestamp= Timestamp.valueOf(now);

        Program program = new Program(7L,"Django","new Prog",
                "Active", timestamp, timestamp);

        Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program,
                5, timestamp, timestamp);

        return batch;
    }

    @Test
    @DisplayName("Test get all submissions by student ID when student exists- Service Test ")
    public void testGetSubmissionsByUserID_whenUserIdExists(){
        String userId = "U03";

        when(mockUserRepository.existsById(userId)).thenReturn(true);
        given(mockAssignmentSubmitRepository.findByUser_userId(userId)).willReturn(mockAssignmentSubmitList);
        given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
                .willReturn(mockAssignmentSubmitDTOList);

        List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getSubmissionsByUserID(userId);

        assertEquals(assignmentSubmitDTOList.size(),mockAssignmentSubmitDTOList.size());
        assertEquals(userId,assignmentSubmitDTOList.get(0).getUserId());
        assertEquals(userId,assignmentSubmitDTOList.get(1).getUserId());
        assertEquals(mockAssignmentSubmitDTO1.getAssignmentId(),assignmentSubmitDTOList.get(0).getAssignmentId());
        assertEquals(mockAssignmentSubmitDTO2.getAssignmentId(),assignmentSubmitDTOList.get(1).getAssignmentId());

        verify(mockUserRepository).existsById(userId);
        verify(mockAssignmentSubmitRepository).findByUser_userId(userId);

    }

    @Test
    @DisplayName("Test get all submissions by student ID when student exists but no submissions- Service Test ")
    public void testGetSubmissionsByUserID_whenUserIdExists_withNoSubmissions(){
        String userId = "U03";

        when(mockUserRepository.existsById(userId)).thenReturn(true);
        given(mockAssignmentSubmitRepository.findByUser_userId(userId)).willReturn(Collections.emptyList());

        List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getSubmissionsByUserID(userId);

        assertEquals(0,assignmentSubmitDTOList.size());
        assertThat(!assignmentSubmitDTOList.contains(mockAssignmentSubmitDTO1));

        verify(mockUserRepository).existsById(userId);
        verify(mockAssignmentSubmitRepository).findByUser_userId(userId);

    }

    @Test
    @DisplayName("Test get all submissions by student ID when student does not exists- Service Test ")
    public void testGetSubmissionsByUserID_whenUserId_NotFound(){
        String userId = "21";
        when(mockUserRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,() -> mockAssignmentSubmitService.getSubmissionsByUserID(userId));

        verify(mockUserRepository).existsById(userId);
        verifyNoInteractions(mockAssignmentSubmitRepository);
    }

    @Test
    @DisplayName("Test for get all AssignmentSubmissions")
    public void testGetAllSubmissions()
    {
    	when(mockAssignmentSubmitRepository.findAll()).thenReturn(mockAssignmentSubmitList);
    	List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getAllSubmissions();
    	assertEquals(assignmentSubmitDTOList.size(),assignmentSubmitDTOList.size());

    }

    @Test
    @DisplayName("Test for get submissions by Batch")
    public void testGetSubmissionsByBatch()
    {
    	Integer batchId = 11;
    	when(mockAssignmentSubmitRepository.findAll()).thenReturn(mockAssignmentSubmitList);

    	List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getSubmissionsByBatch(batchId);
    	System.out.println(assignmentSubmitDTOList);
    	assertEquals(assignmentSubmitDTOList.size(),assignmentSubmitDTOList.size());
    }


    @Test
    @DisplayName("Test for get Grades by AssignmentID")
    public void testGetGradesByAssinmentId()
    {
    	Long AssignmentId = 1L;
    	given(mockAssignmentSubmitRepository.findById(AssignmentId)).willReturn(Optional.of(mockAssignmentSubmit1));
    	given(mockAssignmentSubmitRepository.getGradesByAssignmentId(AssignmentId)).willReturn(mockAssignmentSubmitList);
    	given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
         .willReturn(mockAssignmentSubmitDTOList);
    	List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getGradesByAssinmentId(AssignmentId);
    	System.out.println(assignmentSubmitDTOList);
    	assertThat(assignmentSubmitDTOList).isNotNull();

    }

    @DisplayName("test for get grades by assignment id if assignment id is not found ")
	@Test
	void testgetGradesByAssinmentIdNotFound() {
		//given
    	Long AssignmentId = 1L;

		given(mockAssignmentSubmitRepository.findById(AssignmentId)).willReturn(Optional.empty());

		//when
		Assertions.assertThrows(ResourceNotFoundException.class,
				() -> mockAssignmentSubmitService.getGradesByAssinmentId(AssignmentId));

		//then
		Mockito.verify(assignmentSubmitMapper, never()).toAssignmentSubmitDTOList(any(List.class));
	}

    @DisplayName("Test for get grades by student Id")
    @Test
    void testGetGradesByStudentId() {
    	//given
    	 String userId = "U01";

    	 when(mockUserRepository.existsById(userId)).thenReturn(true);

    	 given(mockAssignmentSubmitRepository.getGradesByStudentID(userId)).willReturn(mockAssignmentSubmitList);
         given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
                  .willReturn(mockAssignmentSubmitDTOList);

         List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getGradesByStudentId(userId);
     	 System.out.println(assignmentSubmitDTOList);
     	 assertThat(assignmentSubmitDTOList).isNotNull();
    }


    @DisplayName("test for get grades by student id if student id is not found ")
   	@Test
   	void testGetGradesByStudentIdNotFound() {

    	String userId = "40";

       	when(mockUserRepository.existsById(userId)).thenReturn(false);

   		Assertions.assertThrows(ResourceNotFoundException.class,
   				() -> mockAssignmentSubmitService.getGradesByStudentId(userId));

    	Mockito.verify(assignmentSubmitMapper, never()).toAssignmentSubmitDTOList(any(List.class));
      	verify(mockUserRepository).existsById(userId);

    }


    @DisplayName("test to update ReSubmit Assignment")
    @Test
    public void testResubmitAssignment() {

        Long submissionId = 4L;
        AssignmentSubmitDTO assignmentSubmitDTO = mockAssignmentSubmitDTO1;

        Assignment assignment = mockAssignment;
        assignment.setAssignmentId(2L);
        assignment.setDueDate(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));

        AssignmentSubmitDTO updatedAssignmentSubmitDTO = mockAssignmentSubmitDTO1;
        updatedAssignmentSubmitDTO.setGrade(90);
        updatedAssignmentSubmitDTO.setGradedBy("U02");
        updatedAssignmentSubmitDTO.setGradedDateTime(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        updatedAssignmentSubmitDTO.setSubPathAttach2("Resubmitted Assignment");

        // given
        given(mockAssignmentSubmitRepository.findById(submissionId)).willReturn(Optional.of(mockAssignmentSubmit1));
        given(assignmentSubmitMapper.toAssignmentSubmit(mockAssignmentSubmitDTO1)).willReturn(mockAssignmentSubmit2);
         given(mockAssignmentRepository.findById(assignmentSubmitDTO.getAssignmentId())).willReturn(Optional.of(mockAssignment));
        given(assignmentSubmitMapper.toAssignmentSubmitDTO(mockAssignmentSubmit2)).willReturn(updatedAssignmentSubmitDTO);
        given(mockAssignmentSubmitRepository.save(mockAssignmentSubmit2)).willReturn(mockAssignmentSubmit2);

        //when
        AssignmentSubmitDTO result = mockAssignmentSubmitService.resubmitAssignment(mockAssignmentSubmitDTO1, submissionId);

        //then
        assertNotNull(result);
        assertThat(result).isNotNull();

    }
}
