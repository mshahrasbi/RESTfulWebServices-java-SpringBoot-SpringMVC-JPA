package com.mycompanyname.app.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	
	Contact contact = new Contact(
			"XXX YYYY",
			"http://www.myapp.com",
			"XXX.YYYY@company.com"
			);
	
	List<VendorExtension> vendorExtensions = new ArrayList<>();
	
	ApiInfo apiInfo = new ApiInfo(
			"Photo app RESTful Web Service documentation",
			"This page documents Photo add RESTful Web Service endpoints",
			"1.0",
			"http://www.myapp.com/service.html",
			contact,
			"Apache 2.0",
			"http://www.myapp.com/licenses/LICNSE-2.0",
			vendorExtensions
			);
	
	/*
	 * This method will return Bean, and inside this method we are configuring and object which is
	 * called docket, so in this object we specify that this is going to be a documentation Swagger
	 * and there are a couple of important details that are provided in the API and in path for API
	 * Here we specify classes which need to be included in Swagger, we provided the base package 
	 * of our project and thus we delegated the job of finding this needed classes to the framework
	 * The base package here we provide will be scanned by the framewor and all the needed classes 
	 * that it finds will be included based on the annotation that we have, and in the path we specify
	 * the methods which have this annotation path and this method are found in the REST controllers
	 * that we have, so all the path select those that we provide they will be included into our API
	 * That is going to be automatically generated for us, and since they want all the methods to be 
	 * included in our documentation, we simply provide PathSelectors.any(). 
	 */
	@Bean
	public Docket apiDocket() {
		
		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.protocols(new HashSet<>(Arrays.asList("HTTP", "HTTPS")))
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.mycompanyname.app.ws"))
				.paths(PathSelectors.any())
				.build();
		
		return docket;
	}
}
