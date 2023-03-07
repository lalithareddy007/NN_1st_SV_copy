package com.numpyninja.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
//@ComponentScan(basePackages="com.ninja.lms")
@Configuration
public class LmsServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsServicesApplication.class, args);
    }

    @Bean
    public Docket lmsApi() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("LMS Phase 2").apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.numpyninja.lms"))
                .paths(PathSelectors.any())
                .build();


    }
    
    private ApiInfo apiInfo(){
    	
        return new ApiInfoBuilder().title("Learning Management System Phase 2")
        		.description("Documentation generated using Swagger UI").build();
        }
}