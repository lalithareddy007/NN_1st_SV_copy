package com.numpyninja.lms.services;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.auth.x500.X500Principal;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.bridge.context.PinpointingMessageHandler;
//import org.eclipse.jdt.internal.compiler.classfmt.NonNullDefaultAwareTypeAnnotationWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.dto.UserPictureEntityDTO;
//import com.numpyninja.lms.dto.UserPictureEntityDto;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserPictureMapper;
import com.numpyninja.lms.repository.UserPictureRepository;
import com.numpyninja.lms.repository.UserRepository;

@Service
public class UserPictureService {

	
	
	@Autowired
	private UserPictureRepository userpicturerepo;
		
		@Autowired
		private UserPictureMapper userPictureMapper;
		
		@Autowired
		private UserRepository userRepository;
		


//save files to DB
public UserPictureEntityDTO uploadtoDB(UserPictureEntityDTO userpicturedto) throws IOException
{
	UserPictureEntity savedPicture = userpicturerepo.
	findByuserAnduserFileType(userpicturedto.getUserId(),userpicturedto.getUserFileType());
			System.out.println(savedPicture);
	if(savedPicture != null) 
		throw new DuplicateResourceFoundException("UserPictureEntity", "UserId", userpicturedto.getUserId());
    String  filetype = userpicturedto.getUserFileType();
	if(!(filetype.equalsIgnoreCase("Resume") || filetype.equalsIgnoreCase("ProfilePic")))
	throw new 	ResourceNotFoundException("UserPictureEntity","Userfiletype",userpicturedto.getUserFileType());
	
    UserPictureEntity userpicture = userPictureMapper.toUserPictureEntity(userpicturedto);
	UserPictureEntity newpicture = this.userpicturerepo.save(userpicture);
	return userPictureMapper.toUserPictureEntityDto(newpicture);
	
}


//download file
public UserPictureEntityDTO download(String userid,String filetype) {
	
	
UserPictureEntity pictureindb =userpicturerepo.findByuserAnduserFileType(userid, filetype);
	
if(pictureindb== null)
	throw new ResourceNotFoundException("user Id " + userid+ " not found");

return userPictureMapper.toUserPictureEntityDto(pictureindb);
	
}


//delete file
public void DeleteFile(String userid,String filetype) throws IOException
{
	User user = userRepository.findById(userid)
		       .orElseThrow(() -> new ResourceNotFoundException("user Id " + userid + " not found"));
	
     UserPictureEntity ToDeletePicture	=	userpicturerepo.findByuserAnduserFileType(userid, filetype);
	 System.out.println(ToDeletePicture);

	  if(ToDeletePicture == null)
		  throw new ResourceNotFoundException("user Id " + userid+ " not found");
	  else
	 //delete from db
		userpicturerepo.deleteById(ToDeletePicture.getUserFileId());
	 // userpicturerepo.deleteByuserAnduserFiletype(userid, filetype);
	 // userpicturerepo.deleteByuserAnduserFiletype(user, filetype);
}

  
//update file
public UserPictureEntityDTO updateFile( UserPictureEntityDTO userpicturedto , String userid ) throws IOException
  {
	
      UserPictureEntity savedpicture=	userpicturerepo.findByuserAnduserFileType(userpicturedto.getUserId(), userpicturedto.getUserFileType());
      System.out.println(savedpicture); 
      
//      if(savedpicture==null)
//    	 throw new ResourceNotFoundException("UserPictureEntity" + userpicturedto.getUserId() + " not found");

      User user = userRepository.findById(userpicturedto.getUserId())
       .orElseThrow(() -> new ResourceNotFoundException("user Id " + userpicturedto.getUserId() + " not found"));
      
      String  filetype = userpicturedto.getUserFileType();
  	  if(!(filetype.equalsIgnoreCase("Resume") || filetype.equalsIgnoreCase("ProfilePic")))
  	  throw new 	ResourceNotFoundException("UserPictureEntity","Userfiletype",userpicturedto.getUserFileType());
  	
      UserPictureEntity userpicture = userPictureMapper.toUserPictureEntity(userpicturedto);

      userpicture.setUserFileId(savedpicture.getUserFileId());
      userpicture.setUser(savedpicture.getUser());
     
      if(StringUtils.hasLength(userpicturedto.getUserFileType()))
    	 userpicture.setUserFileType(userpicturedto.getUserFileType());
      else 
    	 userpicture.setUserFileType(savedpicture.getUserFileType());
	
     if(StringUtils.hasLength(userpicturedto.getUserFilePath()))
    	 userpicture.setUserFilePath(userpicturedto.getUserFilePath());
     else 
    	 userpicture.setUserFilePath(savedpicture.getUserFilePath());	
	//userpicture.setUserFilePath(userpicturedto.getUserFilePath());
     UserPictureEntity saveuserpicture  = this.userpicturerepo.save(userpicture);
     UserPictureEntityDTO  userdtoEntity =  userPictureMapper.toUserPictureEntityDto(saveuserpicture);

     return userdtoEntity;
     


}
}
	





//@Service
//public class UserPictureService {
//	
//	@Autowired
//	private UserPictureRepository userpicturerepo;
//	
//	
//	
//	private final String uploadFolderpath = new ClassPathResource("static/logo/").getFile().getAbsolutePath();
//	
//	
//	public UserPictureService() throws IOException
//	{
//		
//	}
//	
//	//save files to DB
//	public void uploadtoDB(MultipartFile file ,User Uid,String filetype) throws IOException
//	{
//		UserPictureEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype); 
//				
//		if(savedpicture != null)
//		 {
//			 savedpicture.setUserFileType("filetype");
//			
//			userpicturerepo.save(savedpicture);
//			                
//		 }
//		 
//		else
//		
//		if(savedpicture == null)
//		
//		Files.copy(file.getInputStream(), Paths.get(uploadFolderpath+File.separator+file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
//		
//		
//		UserPictureEntity userpicture = new UserPictureEntity();
//		
//		
//		if(filetype.equalsIgnoreCase("Resume"))
//		userpicture.setUserFileType("Resume");
//		
//		else if(filetype.equalsIgnoreCase("ProfilePic")){
//			userpicture.setUserFileType("ProfilePic");
//		}
//		userpicture.setUserFilePath(uploadFolderpath +File.separator+ file.getOriginalFilename());
//		userpicture.setUser(Uid);
//	    userpicturerepo.save(userpicture);
//		
//		
//		
//	}
//	
//	//get file
//	public UserPictureEntity GetFile(User Uid,String filetype) throws IOException 
//	{	
//		UserPictureEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype); 
//		
//	    return savedpicture;
//	}	
//		
//	
//	
//	
////	public InputStream getResource(User Uid,String filetype) throws FileNotFoundException
////	{
////		UserPictureEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype); 
////		InputStream is = new FileInputStream(savedpicture.getUserFilePath());
////		return is;
////	}
//	
//	
//	
//     //update file  
//   public UserPictureEntity updateFile( MultipartFile updatedmultipartFile , User userid,String FileType) throws IOException
//   {
//    	
//	UserPictureEntity savedpicture=	userpicturerepo.findByuserAnduserFileType(userid, FileType);
//    System.out.println(savedpicture); 
//    if(savedpicture != null)
//    {  
//    		 //delete old file
//    		 Path path = Paths.get(savedpicture.getUserFilePath() );
//    		 Files.delete(path);
//    		 System.out.println("savedpicture Image Deleted !!!");
//    		 
//    		 //update new photo
//    	      File saveFile = new ClassPathResource("static/logo/").getFile();
//    	      Path path1 = Paths.get(saveFile.getAbsolutePath() +File.separator +updatedmultipartFile.getOriginalFilename());
//    	      Files.copy(updatedmultipartFile.getInputStream(), path1,StandardCopyOption.REPLACE_EXISTING);   
//    	       
//    	      //save into db
//    	       UserPictureEntity picture = new UserPictureEntity();
//    	       picture.setUserFileId(savedpicture.getUserFileId());
//    	       picture.setUser(userid);
//    	       picture.setUserFileType(FileType);
//    	       picture.setUserFilePath(uploadFolderpath+File.separator+ updatedmultipartFile.getOriginalFilename());
//    		   userpicturerepo.save(picture);
//    		}
//    	else
//    		{
//    			UserPictureEntity picture = new UserPictureEntity();
//    		    picture.setUser(savedpicture.getUser());
//    		    picture.setUserFilePath(savedpicture.getUserFilePath());
//    		    picture.setUserFileType(savedpicture.getUserFileType());
//    			userpicturerepo.save(savedpicture);
//    			
//    		}
//    			
//    			return savedpicture;
//} 
//
//
//		
//	//Delete File	
//	public void DeleteFile(User userid,String filetype) throws IOException
//	{
//         UserPictureEntity ToDeletePicture	=	userpicturerepo.findByuserAnduserFileType(userid, filetype);
//		 System.out.println(ToDeletePicture);
//		  if(ToDeletePicture != null)
//		  {	  
//			  //delete from path
//			  Path path = Paths.get(ToDeletePicture.getUserFilePath() );
//	 
//		      Files.delete(path);
//		 
//	      	 System.out.println("file/Image Deleted !!!");
//		 
//		 //delete from db
//			 userpicturerepo.deleteById(ToDeletePicture.getUserFileId());
//		
//		  }
//		
//	  
//	} 
//	  
//}


