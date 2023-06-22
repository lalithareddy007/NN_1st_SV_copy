package com.numpyninja.lms.services;
import java.io.IOException;

//import org.eclipse.jdt.internal.compiler.classfmt.NonNullDefaultAwareTypeAnnotationWalker;
import com.numpyninja.lms.entity.UserFileEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.numpyninja.lms.dto.UserFileEntityDTO;
//import com.numpyninja.lms.dto.UserFileEntityDto;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserFileMapper;
import com.numpyninja.lms.repository.UserFileRepository;
import com.numpyninja.lms.repository.UserRepository;

@Service
public class UserFileService {

	
	
	@Autowired
	private UserFileRepository userfilerepo;
		
		@Autowired
		private UserFileMapper userFileMapper;
		
		@Autowired
		private UserRepository userRepository;
		


//save files to DB
public UserFileEntityDTO uploadtoDB(UserFileEntityDTO userFiledto) throws IOException
{
	UserFileEntity savedFile = userfilerepo.
	findByuserAnduserFileType(userFiledto.getUserId(),userFiledto.getUserFileType());
			System.out.println(savedFile);
	if(savedFile != null)
		throw new DuplicateResourceFoundException("UserFileEntity", "UserId", userFiledto.getUserId());
    String  filetype = userFiledto.getUserFileType();
	if(!(filetype.equalsIgnoreCase("Resume") || filetype.equalsIgnoreCase("ProfilePic")))
	throw new 	ResourceNotFoundException("UserFileEntity","Userfiletype",userFiledto.getUserFileType());
	
    UserFileEntity userFile = userFileMapper.toUserFileEntity(userFiledto);
	UserFileEntity newFile = this.userfilerepo.save(userFile);
	return userFileMapper.toUserFileEntityDto(newFile);
	
}


//download file
public UserFileEntityDTO download(String userid, String filetype) {
	
	
UserFileEntity Fileindb =userfilerepo.findByuserAnduserFileType(userid, filetype);
	
if(Fileindb== null)
	throw new ResourceNotFoundException("user Id " + userid+ " not found");

return userFileMapper.toUserFileEntityDto(Fileindb);
	
}


//delete file
public void DeleteFile(String userid,String filetype) throws IOException
{
	User user = userRepository.findById(userid)
		       .orElseThrow(() -> new ResourceNotFoundException("user Id " + userid + " not found"));
	
     UserFileEntity ToDeleteFile	=	userfilerepo.findByuserAnduserFileType(userid, filetype);
	 System.out.println(ToDeleteFile);

	  if(ToDeleteFile == null)
		  throw new ResourceNotFoundException("user Id " + userid+ " not found");
	  else
	 //delete from db
		userfilerepo.deleteById(ToDeleteFile.getUserFileId());
	 
}

  
//update file
public UserFileEntityDTO updateFile(UserFileEntityDTO userFiledto , String userid ) throws IOException
  {
	
      UserFileEntity savedFile=	userfilerepo.findByuserAnduserFileType(userFiledto.getUserId(), userFiledto.getUserFileType());
      System.out.println(savedFile);
      
//      if(savedFile==null)
//    	 throw new ResourceNotFoundException("UserFileEntity" + userFiledto.getUserId() + " not found");

      User user = userRepository.findById(userFiledto.getUserId())
       .orElseThrow(() -> new ResourceNotFoundException("user Id " + userFiledto.getUserId() + " not found"));
      
      String  filetype = userFiledto.getUserFileType();
  	  if(!(filetype.equalsIgnoreCase("Resume") || filetype.equalsIgnoreCase("ProfilePic")))
  	  throw new 	ResourceNotFoundException("UserFileEntity","Userfiletype",userFiledto.getUserFileType());
  	
      UserFileEntity userFile = userFileMapper.toUserFileEntity(userFiledto);

      userFile.setUserFileId(savedFile.getUserFileId());
    //  userFile.setUser(savedFile.getUser());
     userFile.setUser(user);
      if(StringUtils.hasLength(userFiledto.getUserFileType()))
    	 userFile.setUserFileType(userFiledto.getUserFileType());
      else 
    	 userFile.setUserFileType(savedFile.getUserFileType());
	
     if(StringUtils.hasLength(userFiledto.getUserFilePath()))
    	 userFile.setUserFilePath(userFiledto.getUserFilePath());
     else 
    	 userFile.setUserFilePath(savedFile.getUserFilePath());
	//userpicture.setUserFilePath(userpicturedto.getUserFilePath());
     UserFileEntity saveuserfile  = this.userfilerepo.save(userFile);
     UserFileEntityDTO userdtoEntity =  userFileMapper.toUserFileEntityDto(saveuserfile);

     return userdtoEntity;
     


}
}
	





//@Service
//public class UserFileService {
//	
//	@Autowired
//	private UserFileRepository userpicturerepo;
//	
//	
//	
//	private final String uploadFolderpath = new ClassPathResource("static/logo/").getFile().getAbsolutePath();
//	
//	
//	public UserFileService() throws IOException
//	{
//		
//	}
//	
//	//save files to DB
//	public void uploadtoDB(MultipartFile file ,User Uid,String filetype) throws IOException
//	{
//		UserFileEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype);
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
//		UserFileEntity userpicture = new UserFileEntity();
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
//	public UserFileEntity GetFile(User Uid,String filetype) throws IOException
//	{	
//		UserFileEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype);
//		
//	    return savedpicture;
//	}	
//		
//	
//	
//	
////	public InputStream getResource(User Uid,String filetype) throws FileNotFoundException
////	{
////		UserFileEntity savedpicture = this.userpicturerepo.findByuserAnduserFileType(Uid,filetype);
////		InputStream is = new FileInputStream(savedpicture.getUserFilePath());
////		return is;
////	}
//	
//	
//	
//     //update file  
//   public UserFileEntity updateFile( MultipartFile updatedmultipartFile , User userid,String FileType) throws IOException
//   {
//    	
//	UserFileEntity savedpicture=	userpicturerepo.findByuserAnduserFileType(userid, FileType);
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
//    	       UserFileEntity picture = new UserFileEntity();
//    	       picture.setUserFileId(savedpicture.getUserFileId());
//    	       picture.setUser(userid);
//    	       picture.setUserFileType(FileType);
//    	       picture.setUserFilePath(uploadFolderpath+File.separator+ updatedmultipartFile.getOriginalFilename());
//    		   userpicturerepo.save(picture);
//    		}
//    	else
//    		{
//    			UserFileEntity picture = new UserFileEntity();
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
//         UserFileEntity ToDeletePicture	=	userpicturerepo.findByuserAnduserFileType(userid, filetype);
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


