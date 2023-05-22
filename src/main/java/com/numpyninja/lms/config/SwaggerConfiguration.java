package com.numpyninja.lms.config;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import java.util.Collections;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
	public static final String AUTHORIZATION_HEADER = "Authorization";

	private ApiKey apiKeys() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}

	private SecurityContext securityContext() {
	    return SecurityContext.builder()
	        .securityReferences(sf())
	        .build();
	  }

	private List<SecurityReference> sf() {
		AuthorizationScope scope = new AuthorizationScope("global", "accessEverything");
		 AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
	     authorizationScopes[0] = scope;
		 return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				//.apiInfo(apiInfo())
				//.securityContexts(Arrays.asList(securityContext()))
				//.securitySchemes(Arrays.asList(apiKeys()))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.numpyninja.lms.controller")).paths(PathSelectors.any())
				.paths(PathSelectors.any())
				.build()
				.securitySchemes(Arrays.asList(apiKeys()))
				.securityContexts(Arrays.asList(securityContext()))
				.apiInfo(apiInfo())
				.pathMapping("/")
	            .useDefaultResponseMessages(false)
	            .directModelSubstitute(LocalDate.class, String.class)
	            .genericModelSubstitutes(ResponseEntity.class);
	}

	
	 private ApiInfo apiInfo(){
	        return new ApiInfo(
	                "Learning Management System Phase 2",
	                "LMS REST API Documentation",
	                "1",
	                "Terms of service",
	                new Contact("LMs", "www.lms.org", "lms@gmail.com"),
	                "License of API",
	                "API license URL",
	                Collections.emptyList()
	        );
	    }

	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}
	
	

	 
	
}
