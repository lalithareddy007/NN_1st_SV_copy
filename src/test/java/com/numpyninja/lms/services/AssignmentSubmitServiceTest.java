package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.AssignmentSubmitMapper;
import com.numpyninja.lms.repository.AssignmentRepository;
import com.numpyninja.lms.repository.AssignmentSubmitRepository;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.UserRepository;

import com.numpyninja.lms.repository.UserRoleMapRepository;
import lombok.SneakyThrows;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private UserRoleMapRepository mockUserRoleMapRepository;

    @Mock
    private AssignmentRepository mockAssignmentRepository;
    
    @Mock
    private ProgBatchRepository batchRepository;

    private AssignmentSubmit mockAssignmentSubmit1, mockAssignmentSubmit2, mockAssignmentSubmit3,
                        mockAssignmentSubmit4;

    private AssignmentSubmitDTO mockAssignmentSubmitDTO1, mockAssignmentSubmitDTO2, mockAssignmentSubmitDTO3,
                        mockAssignmentSubmitDTO4;

    private Assignment mockAssignment;



    private User mockUser,mockUser1;

    List<AssignmentSubmit> mockAssignmentSubmitList;
    List<AssignmentSubmitDTO> mockAssignmentSubmitDTOList;

    @BeforeEach
    private void setMockAssignmentSubmitDTO(){

        Date dueDate = Timestamp.valueOf("2023-02-02 09:30:00");

        Timestamp timestamp1 = Timestamp.valueOf("2023-01-02 09:30:00");
        Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.now());
        Timestamp timestamp3 = Timestamp.valueOf(LocalDateTime.now().plusDays(3));

        mockAssignmentSubmitDTO1 = new AssignmentSubmitDTO(4L,2L,"U03","Selenium assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);
        mockAssignmentSubmitDTO2 = new AssignmentSubmitDTO(3L,1L,"U03","SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);
        mockAssignmentSubmitDTO3 = new AssignmentSubmitDTO(8L,1L,"U05","SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp1,null,null,-1);
        mockAssignmentSubmitDTO4 = new AssignmentSubmitDTO(8L,1L,"U05","SQL assignment Submission",
                "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp2,"U03",timestamp3,80);

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
        mockAssignmentSubmit4 = new AssignmentSubmit(8L,mockAssignment,mockUser1,"second Submission", "Second submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                timestamp3,"U03",timestamp2,80, timestamp1, timestamp2);

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
///
    @Test
    @DisplayName("Test get all submissions by student ID when student exists but no submissions- Service Test ")
    public void testGetSubmissionsByUserID_whenUserIdExists_withNoSubmissions(){
        String userId = "U03";

        when(mockUserRepository.existsById(userId)).thenReturn(true);
        given(mockAssignmentSubmitRepository.findByUser_userId(userId)).willReturn(Collections.emptyList());

        List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getSubmissionsByUserID(userId);

        assertEquals(0,assignmentSubmitDTOList.size());
       // assertThat(!assignmentSubmitDTOList.contains(mockAssignmentSubmitDTO1));

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
    	Integer batchId =mockAssignment.getBatch().getBatchId();
    	when(mockAssignmentSubmitRepository.findByAssignment_Batch_BatchId(batchId)).thenReturn(mockAssignmentSubmitList);
    	given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
         .willReturn(mockAssignmentSubmitDTOList);

    	List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getSubmissionsByBatch(batchId);
    	assertEquals(assignmentSubmitDTOList.size(),assignmentSubmitDTOList.size());
    }

    
    
    
//test for submissions by batch to check on heroku
    
    
//	@Test
//	@DisplayName("test for getting submissions by BatchId")
//	void testGetSubmissionsByBatchHeroku() {
//Integer batchidInteger =1;
//    	
//    	List<Long> assignmentIds = new ArrayList<Long>();
//		Batch batch = setMockBatch();
//		
//		Assignment mockassignment1= mockAssignment;
//		mockassignment1.setAssignmentId(7L);
//		mockassignment1.setAssignmentName("SQL");
//		
//		List<Assignment> assignmentList = new ArrayList<Assignment>();
//		assignmentList.add(mockassignment1);
//		
//		AssignmentSubmit mockAssignmentSubmit = mockAssignmentSubmit1;
//		mockAssignmentSubmit.setGrade(1);
//		mockAssignmentSubmit.setGradedBy("U02");
//		
//		List<AssignmentSubmit> assignmentSubmitList = mockAssignmentSubmitList;
//		assignmentSubmitList.add(mockAssignmentSubmit);
//		
//		given(batchRepository.findById(batchidInteger)).willReturn(Optional.of(batch));
//	    given(mockAssignmentRepository.findByBatch(batch)).willReturn(assignmentList);
//	    given(mockAssignmentSubmitRepository.findByAssignment_AssignmentIdIn(assignmentIds)).willReturn(assignmentSubmitList);
//	    given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList)).willReturn(mockAssignmentSubmitDTOList);
//		
//		//when
//		List<AssignmentSubmitDTO> assignmentSubmitDTOs = mockAssignmentSubmitService.getSubmissionsByBatchHeroku(batch.getBatchId());
//		
//		//then
//		assertThat(assignmentSubmitDTOs).isNotNull();
//		
//		
//		
//	}

    @Test
    @DisplayName("Test for get Grades by AssignmentID")
    public void testGetGradesByAssinmentId()
    {
    	Long AssignmentId = 1L;
    	given(mockAssignmentRepository.findById(AssignmentId)).willReturn(Optional.of(mockAssignment));
    	given(mockAssignmentSubmitRepository.getGradesByAssignmentId(AssignmentId)).willReturn(mockAssignmentSubmitList);
    	given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
         .willReturn(mockAssignmentSubmitDTOList);
    	List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getGradesByAssinmentId(AssignmentId);
    	assertThat(assignmentSubmitDTOList).isNotNull();

    }

    @DisplayName("test for get grades by assignment id if assignment id is not found ")
	@Test
	void testgetGradesByAssinmentIdNotFound() {
		//given
    	Long AssignmentId = 1L;

		given(mockAssignmentRepository.findById(AssignmentId)).willReturn(Optional.empty());

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
    	 String userId = mockUser.getUserId();

    	 when(mockUserRepository.existsById(userId)).thenReturn(true);

    	 given(mockAssignmentSubmitRepository.getGradesByStudentID(userId)).willReturn(mockAssignmentSubmitList);
         given(assignmentSubmitMapper.toAssignmentSubmitDTOList(mockAssignmentSubmitList))
                  .willReturn(mockAssignmentSubmitDTOList);

         List<AssignmentSubmitDTO> assignmentSubmitDTOList = mockAssignmentSubmitService.getGradesByStudentId(userId);
         
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
      //assertThat(result).isNotNull();

    }

    @Test
    @DisplayName("Test to grade submissions")
    @SneakyThrows
    public void testGradeSubmissions() {
        Long submissionId = 8L;
        String gradedBy = "U03";

        given(mockAssignmentSubmitRepository.findById(submissionId)).willReturn(Optional.of(mockAssignmentSubmit4));
        given(mockAssignmentSubmitRepository.save(mockAssignmentSubmit4)).willReturn(mockAssignmentSubmit4);
        given(assignmentSubmitMapper.toAssignmentSubmitDTO(mockAssignmentSubmit4)).willReturn(mockAssignmentSubmitDTO4);
        when(mockUserRepository.existsById(gradedBy)).thenReturn(true);
        when(mockUserRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase(
                gradedBy,"R03","Active")).thenReturn(true);

        mockAssignmentSubmit4.setSubComments("second Submission");
        mockAssignmentSubmit4.setGrade(80);
        mockAssignmentSubmit4.setGradedBy(gradedBy);

        AssignmentSubmitDTO savedAssignmentSubmitDTO = mockAssignmentSubmitService.gradeAssignmentSubmission(mockAssignmentSubmitDTO4, submissionId);

        assertNotNull(savedAssignmentSubmitDTO);
        verify(mockUserRepository).existsById(gradedBy);
        verify(mockUserRoleMapRepository).existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase(
                gradedBy,"R03","Active");
    }

    @Test
    @DisplayName("Test to grade assignment when submission doesn't exist")
    public void testGradeSubmission_WhenSubmission_DoesNotExist(){
        Long submissionId = 8L;
        given(mockAssignmentSubmitRepository.findById(submissionId)).willReturn(Optional.empty());
        Assertions.assertThrows(ResourceNotFoundException.class,
                ()->mockAssignmentSubmitService.gradeAssignmentSubmission(mockAssignmentSubmitDTO4,submissionId));

        verify(mockUserRepository,never()).existsById(any(String.class));
        verify(mockAssignmentSubmitRepository, never()).save(any(AssignmentSubmit.class));
        verify(assignmentSubmitMapper,never()).toAssignmentSubmitDTO(any(AssignmentSubmit.class));
        verifyNoInteractions(mockUserRoleMapRepository);
    }

    @Test
    @DisplayName("Test to grade assignment when Grader with given ID doesn't exist")
    @SneakyThrows
    public void testGradeSubmission_WhenGrader_DoesNotExist(){
        Long submissionId = 8L;
        given(mockAssignmentSubmitRepository.findById(submissionId)).willReturn(Optional.of(mockAssignmentSubmit4));
        when(mockUserRepository.existsById(mockAssignmentSubmit4.getGradedBy())).thenReturn(false);
        ResourceNotFoundException e = Assertions.assertThrows(ResourceNotFoundException.class,
                ()->mockAssignmentSubmitService.gradeAssignmentSubmission(mockAssignmentSubmitDTO4,submissionId));

        verifyNoInteractions(mockUserRoleMapRepository);
        verify(mockAssignmentSubmitRepository, never()).save(any(AssignmentSubmit.class));
        verify(assignmentSubmitMapper,never()).toAssignmentSubmitDTO(any(AssignmentSubmit.class));

    }

    @Test
    @DisplayName("Test to grade assignment when Grader isn't admin/staff ")
    @SneakyThrows
    public void testGradeSubmission_WhenGrader_NotAllowedToGrade(){
        Long submissionId = 8L;
        String gradedBy = "U03";
        String message = "User "+gradedBy+" is not allowed to grade the submission";
        given(mockAssignmentSubmitRepository.findById(submissionId)).willReturn(Optional.of(mockAssignmentSubmit4));
        when(mockUserRepository.existsById(mockAssignmentSubmit4.getGradedBy())).thenReturn(true);
        lenient().when(mockUserRoleMapRepository.
                existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase(
                        gradedBy,"R03","Active")).thenReturn(false);
        InvalidDataException e = Assertions.assertThrows(InvalidDataException.class,
                ()->mockAssignmentSubmitService.gradeAssignmentSubmission(mockAssignmentSubmitDTO4,submissionId));

        assertNotNull(e.getMessage(),message);
        verify(mockAssignmentSubmitRepository, never()).save(any(AssignmentSubmit.class));
        verify(assignmentSubmitMapper,never()).toAssignmentSubmitDTO(any(AssignmentSubmit.class));

    }
}
