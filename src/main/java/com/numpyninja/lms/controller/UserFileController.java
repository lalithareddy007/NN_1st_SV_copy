package com.numpyninja.lms.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.config.MessageResponse;
import com.numpyninja.lms.dto.UserFileEntityDTO;

import com.numpyninja.lms.services.UserFileService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/file")

@Api(tags="User File Controller", description="User File CRUD Operations")
public class UserFileController {
	
	
	@Autowired
    private UserFileService userfileservice;

	@PostMapping(path = "/userfile", consumes = "application/json", produces = "application/json")
	@ApiOperation("Create New User File")
	 public ResponseEntity<UserFileEntityDTO> Save(@RequestBody UserFileEntityDTO userfiledto) throws IOException{
		
		
		UserFileEntityDTO newFile = userfileservice.uploadtoDB(userfiledto);
		return ResponseEntity.status(HttpStatus.CREATED).body(newFile);
	}

	
	@GetMapping(path="/userfile/{userid}", produces = "application/json")
	@ApiOperation("Get User File by User ID")
	public ResponseEntity<UserFileEntityDTO> download(@PathVariable (value="userid")String  userid, @RequestParam (value="userfiletype") String userfiletype) throws IOException{
		UserFileEntityDTO downloadfile=	userfileservice.download(userid, userfiletype);
		return ResponseEntity.ok(downloadfile);
	//return	ResponseEntity.status(HttpStatus.OK).body(downloadFile);
		//return ResponseEntity.ok(this.userFileservice.download(userid, userfiletype));
	}	
	
	
	@DeleteMapping("/userfile/{userid}")
	@ApiOperation("Delete User File")
    public ResponseEntity<MessageResponse> Delete(@PathVariable String userid,@RequestParam String userfiletype ) throws IOException
	{
     this.userfileservice.DeleteFile(userid, userfiletype);
     return new ResponseEntity<MessageResponse>(new MessageResponse("file deleted successfully",true) , HttpStatus.OK);
	 
	}
	
	@PutMapping("/userfile/{userid}")
	@ApiOperation("Update User File")
    public ResponseEntity<UserFileEntityDTO> update(@RequestBody UserFileEntityDTO userfiledto , @PathVariable String userid) throws IOException
   {
  	UserFileEntityDTO updatedFile=this.userfileservice.updateFile(userfiledto,userid);
  	return ResponseEntity.ok(updatedFile);
   }
	
	
	
	
	
	
}
//public class UserFileController {
//
//	@Autowired
//	private UserFileService userFileservice;
//	
//	    
//    @PostMapping("/userFile")
//    public ResponseEntity<String> uploadDB(@RequestParam("file")MultipartFile multipartFile,
//    		@RequestParam User Uid,
//    		@RequestParam String filetype) throws IOException
//    
//    {
//    	//String uploadFolderpath1 = new ClassPathResource("static/logo/").getFile().getAbsolutePath();     
//       String downloadUrl = " ";
//       userFileservice.uploadtoDB(multipartFile,Uid,filetype);
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
//     @GetMapping("/userFile/{userid}")
//     public UrlResource downloadFile(@PathVariable User userid,
//    		@RequestParam String filetype) throws IOException
//     {
//      	UserFileEntity downloadedfile =	userFileservice.GetFile(userid,filetype);
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
//     /*  @GetMapping("/userFile/{userid}")
//     public void downloadImage(@PathVariable User userid , 
//     		@RequestParam String filetype ,HttpServletResponse response) throws IOException
//     {
//     	InputStream resource =this.userFileservice.getResource(userid, filetype);
//     	
//     	response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//     	
//     	StreamUtils.copy(resource, response.getOutputStream()); */
//
//    
//    @PutMapping("/userFile/{userid}")
//    public ResponseEntity<UserFileEntity> update(HttpServletRequest request,@RequestParam("file") MultipartFile multipartFile ,@PathVariable User userid,@RequestParam(required=false,name="FileType") String FileType) throws IOException
//    {
//    	UserFileEntity updatedFile=this.userFileservice.updateFile(multipartFile, userid, FileType);
//    	return ResponseEntity.ok(updatedFile);
//    }
//       
//	
//  @DeleteMapping("/userFile/{userid}")
//	public ResponseEntity<MessageResponse> Delete(@PathVariable User userid,@RequestParam String filetype ) throws IOException
//	{
//     this.userFileservice.DeleteFile(userid, filetype);
//     return new ResponseEntity<MessageResponse>(new MessageResponse("file deleted successfully",true) , HttpStatus.OK);
//	 
//	}
//
//}	
//	
	
  
