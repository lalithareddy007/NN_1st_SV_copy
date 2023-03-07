package com.numpyninja.lms.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler.Builder;
import com.numpyninja.lms.config.MessageResponse;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.dto.UserPictureEntityDTO;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;

import com.numpyninja.lms.repository.UserPictureRepository;
import com.numpyninja.lms.services.UserPictureService;

import io.github.classgraph.Resource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.expr.NewArray;

@RestController
@RequestMapping("/file")

@Api(tags="User Picture Controller", description="User Picture CRUD Operations")
public class UserPictureController {
	
	
	@Autowired
    private UserPictureService userpictureservice;

	@PostMapping(path = "/userpicture", consumes = "application/json", produces = "application/json")
	@ApiOperation("Create New User Picture")
	 public ResponseEntity<UserPictureEntityDTO> Save(@RequestBody UserPictureEntityDTO userpicturedto) throws IOException{
		
		
		UserPictureEntityDTO newPicture = userpictureservice.uploadtoDB(userpicturedto);
		return ResponseEntity.status(HttpStatus.CREATED).body(newPicture);
	}

	
	@GetMapping("/userpicture/{userid}")
	@ApiOperation("Get User Picture by User ID")
	public ResponseEntity<UserPictureEntityDTO> download(@PathVariable String  userid,  @RequestParam String userfiletype) throws IOException{
		UserPictureEntityDTO downloadpicture=	userpictureservice.download(userid, userfiletype);
		return ResponseEntity.ok(downloadpicture);
	
	}	
	
	
	@DeleteMapping("/userpicture/{userid}")
	@ApiOperation("Delete User Picture")
public ResponseEntity<MessageResponse> Delete(@PathVariable String userid,@RequestParam String userfiletype ) throws IOException
	{
     this.userpictureservice.DeleteFile(userid, userfiletype);
     return new ResponseEntity<MessageResponse>(new MessageResponse("file deleted successfully",true) , HttpStatus.OK);
	 
	}
	
	@PutMapping("/userpicture/{userid}")
	@ApiOperation("Update User Picture")
 public ResponseEntity<UserPictureEntityDTO> update(@RequestBody  UserPictureEntityDTO userpicturedto ,@PathVariable String userid) throws IOException
  {
  	UserPictureEntityDTO updatedPicture=this.userpictureservice.updateFile(userpicturedto,userid);
  	return ResponseEntity.ok(updatedPicture);
  }
	
	
	
	
	
	
}
//public class UserPictureController {
//
//	@Autowired
//	private UserPictureService userpictureservice;
//	
//	    
//    @PostMapping("/userpicture")
//    public ResponseEntity<String> uploadDB(@RequestParam("file")MultipartFile multipartFile,
//    		@RequestParam User Uid,
//    		@RequestParam String filetype) throws IOException
//    
//    {
//    	//String uploadFolderpath1 = new ClassPathResource("static/logo/").getFile().getAbsolutePath();     
//       String downloadUrl = " ";
//       userpictureservice.uploadtoDB(multipartFile,Uid,filetype);
//       downloadUrl= ServletUriComponentsBuilder.fromCurrentContextPath()
//		   .path("/logo/")
//		   .path(multipartFile.getOriginalFilename())
//		   .toUriString();  
//   
//       System.out.println("download url is "+downloadUrl);	   
//       return new ResponseEntity<String>(downloadUrl,HttpStatus.CREATED);
//    
//  
//    }
//    
//    
//     @GetMapping("/userpicture/{userid}")
//     public UrlResource downloadFile(@PathVariable User userid,
//    		@RequestParam String filetype) throws IOException
//     {
//      	UserPictureEntity downloadedfile =	userpictureservice.GetFile(userid,filetype);
//    	System.out.println(downloadedfile);  
//    	if(downloadedfile != null)
//    	 try {
//    		 
//    		      Path path = Paths.get(downloadedfile.getUserFilePath());
//    		      UrlResource resource = new UrlResource(path.toUri());
//    		      System.out.println(resource);
//    		 //returning image to front end
//    		      if(resource.exists())
//    		      {
//    		          return resource;
//    		      } else {
//    		 
//    		                 throw new FileNotFoundException("File not found ");
//    		 
//    		             }
//    		 
//    		         } catch (MalformedURLException ex) {
//    		 
//    		             throw new FileNotFoundException("File not found ");
//    		 
//    		         }
//    		return (UrlResource) ResponseEntity.ok();
//     }
//
//     
//     
//     
//     
//     /*  @GetMapping("/userpicture/{userid}")
//     public void downloadImage(@PathVariable User userid , 
//     		@RequestParam String filetype ,HttpServletResponse response) throws IOException
//     {
//     	InputStream resource =this.userpictureservice.getResource(userid, filetype);
//     	
//     	response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//     	
//     	StreamUtils.copy(resource, response.getOutputStream()); */
//
//    
//    @PutMapping("/userpicture/{userid}")
//    public ResponseEntity<UserPictureEntity> update(HttpServletRequest request,@RequestParam("file") MultipartFile multipartFile ,@PathVariable User userid,@RequestParam(required=false,name="FileType") String FileType) throws IOException
//    {
//    	UserPictureEntity updatedPicture=this.userpictureservice.updateFile(multipartFile, userid, FileType);
//    	return ResponseEntity.ok(updatedPicture);
//    }
//       
//	
//  @DeleteMapping("/userpicture/{userid}")
//	public ResponseEntity<MessageResponse> Delete(@PathVariable User userid,@RequestParam String filetype ) throws IOException
//	{
//     this.userpictureservice.DeleteFile(userid, filetype);
//     return new ResponseEntity<MessageResponse>(new MessageResponse("file deleted successfully",true) , HttpStatus.OK);
//	 
//	}
//
//}	
//	
	
  
