package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.MessageResponse;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;
import com.numpyninja.lms.services.UserPictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/file")
public class UserPictureController {

	@Autowired
	private UserPictureService userpictureservice;
	
	    
    @PostMapping("/userpicture")
    public ResponseEntity<String> uploadDB(@RequestParam("file")MultipartFile multipartFile,
    		@RequestParam User Uid,
    		@RequestParam String filetype) throws IOException
    
    {
    	//String uploadFolderpath1 = new ClassPathResource("static/logo/").getFile().getAbsolutePath();     
       String downloadUrl = " ";
       userpictureservice.uploadtoDB(multipartFile,Uid,filetype);
       downloadUrl= ServletUriComponentsBuilder.fromCurrentContextPath()
		   .path("/logo/")
		   .path(multipartFile.getOriginalFilename())
		   .toUriString();  
   
       System.out.println("download url is "+downloadUrl);	   
       return new ResponseEntity<String>(downloadUrl,HttpStatus.CREATED);
    
  
    }
    
    
     @GetMapping("/userpicture/{userid}")
     public UrlResource downloadFile(@PathVariable User userid,
    		@RequestParam String filetype) throws IOException
     {
      	UserPictureEntity downloadedfile =	userpictureservice.GetFile(userid,filetype);
    	System.out.println(downloadedfile);  
    	if(downloadedfile != null)
    	 try {
    		 
    		      Path path = Paths.get(downloadedfile.getUserFilePath());
    		      UrlResource resource = new UrlResource(path.toUri());
    		      System.out.println(resource);
    		 //returning image to front end
    		      if(resource.exists())
    		      {
    		          return resource;
    		      } else {
    		 
    		                 throw new FileNotFoundException("File not found ");
    		 
    		             }
    		 
    		         } catch (MalformedURLException ex) {
    		 
    		             throw new FileNotFoundException("File not found ");
    		 
    		         }
    		return (UrlResource) ResponseEntity.ok();
     }

     
     
     
     
     /*  @GetMapping("/userpicture/{userid}")
     public void downloadImage(@PathVariable User userid , 
     		@RequestParam String filetype ,HttpServletResponse response) throws IOException
     {
     	InputStream resource =this.userpictureservice.getResource(userid, filetype);
     	
     	response.setContentType(MediaType.IMAGE_JPEG_VALUE);
     	
     	StreamUtils.copy(resource, response.getOutputStream()); */

    
    @PutMapping("/userpicture/{userid}")
    public ResponseEntity<UserPictureEntity> update(HttpServletRequest request,@RequestParam("file") MultipartFile multipartFile ,@PathVariable User userid,@RequestParam(required=false,name="FileType") String FileType) throws IOException
    {
    	UserPictureEntity updatedPicture=this.userpictureservice.updateFile(multipartFile, userid, FileType);
    	return ResponseEntity.ok(updatedPicture);
    }
       
	
  @DeleteMapping("/userpicture/{userid}")
	public ResponseEntity<MessageResponse> Delete(@PathVariable User userid,@RequestParam String filetype ) throws IOException
	{
     this.userpictureservice.DeleteFile(userid, filetype);
     return new ResponseEntity<MessageResponse>(new MessageResponse("file deleted successfully",true) , HttpStatus.OK);
	 
	}

}	
	
	
  
