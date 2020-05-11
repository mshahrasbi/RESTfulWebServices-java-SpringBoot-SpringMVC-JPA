package com.mycompanyname.app.ws.restassuredtest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


/**
 * Now we want to add getUserDetailsTest method and I am going to add it to the same clients right
 * after the testUserLogin method. So the first user needs to login and then the user logs in I will 
 * testUserDetailsTest and then that gives you the details, I will use the 'authorizationHedare' and 
 * 'UserId', so my getUserDetailsTest method will need to be run right after the testUserLogin, and
 * before I can start working on getUserDetailsTest method I need to make sure that my test methods 
 * inside of my test class ran in order when you run the test class that contains more than one test 
 * method and say you have 5 / 6 test methods these test methods they do not always run in order that 
 * we have put them in the class, but sometimes when we run an integration test like this one for example
 * we do need to make sure that our unit tests run in order because we cannot getUserDetailsTest unless
 * you are logged in.
 * So JUnit provides us with some means that allow us to run unit tests in the order that we want and 
 * to make our unit tests to run in the order that we want we need to annotate our class with a 
 * fixmethodorder, and this can take one of the methodsorder name_ascending.
 * So now we have to change our testUserLogin method so that I can achieve that ascending order and to do 
 * That I will rename it to 'a' and the next method we are going to create will be method 'b' etc...
 *
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class UserWebServiceEndpointTest {

	private final String CONTEXT_PATH = "/mobile-app-ws";
	private final String EMAIL_ADDRESS = "mo@mail.com";
	private final String JSON_CONTENTTYPE = "application/json";
	
	private static String authorizationHeader;
	private static String UserId;

	@BeforeEach
	void setUp() throws Exception {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}

	/*
	 * testUserLogin
	 */
	@Test
	void a() {
		
		Map<String, String> loginDetails = new HashMap<>();
		loginDetails.put("email", EMAIL_ADDRESS);
		loginDetails.put("password", "1234");
		
		Response response = given()
			.contentType(JSON_CONTENTTYPE)
			.accept(JSON_CONTENTTYPE)
			.body(loginDetails)
			.when()
			.post(CONTEXT_PATH + "/users/login")
			.then()
			.statusCode(200)
			.extract()
			.response();
		
		authorizationHeader = response.header("Authorization");
		UserId = response.header("UserID");
		
		assertNotNull(authorizationHeader);
		assertNotNull(UserId);
	}
	
	/*
	 * testUserDetails()
	 */
	@Test
	void b() {
		Response response = given()
				.pathParam("id", UserId)
				.header("Authorization", authorizationHeader)
				.accept(JSON_CONTENTTYPE)
				.when()
				.get(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.contentType(JSON_CONTENTTYPE)
				.extract()
				.response();
		
		String userPublicId = response.jsonPath().getString("userId");
		String userEmail = response.jsonPath().getString("email");
		String firstName = response.jsonPath().getString("firstName");
		String lastName = response.jsonPath().getString("lastName");
		List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
		String addressId = addresses.get(0).get("addressId");
		
		assertNotNull(userPublicId);
		assertNotNull(userEmail);
		assertNotNull(firstName);
		assertNotNull(lastName);
		assertEquals(EMAIL_ADDRESS, userEmail);
		
		assertTrue(addresses.size() == 2);
		assertTrue(addressId.length() == 30);
	}
	
	/*
	 * testUpdateUserDetails()
	 */
	@Test
	void c() {
		
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("firstName", "Mohammad");
		userDetails.put("lastName", "Shahrasbi");
		
		Response response = given()
				.contentType(JSON_CONTENTTYPE)
				.accept(JSON_CONTENTTYPE)
				.header("Authorization", authorizationHeader)
				.pathParam("id", UserId)
				.body(userDetails)
				.when()
				.put(CONTEXT_PATH + "/users/{id}")
				.then()
				.statusCode(200)
				.contentType(JSON_CONTENTTYPE)
				.extract()
				.response();
	}
	

}
