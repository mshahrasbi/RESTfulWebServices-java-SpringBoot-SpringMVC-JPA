package com.mycompanyname.app.ws.shared;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)	// load then spring context, which it means that this is an integration test
									// and we can use it to access property files for example in our project.
@SpringBootTest
class UtilsTest {
	
	@Autowired
	Utils utils;

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	final void testGenerateUserId() {
		String userId = utils.generateUserId(30);
		String userId2 = utils.generateUserId(30);
		
		assertNotNull(userId);
		assertTrue(userId.length() == 30);
		
		assertTrue(!userId.equalsIgnoreCase(userId2));
	}

	@Test
	//@Disabled
	final void testHasTokenNotExpired() {
		
		String token = utils.generateEmailVerificationToken("3djshdkj23");
		assertNotNull(token);
		
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		
		assertFalse(hasTokenExpired);
	}

	@Test
	final void testHasTokenExpired() {
		String expiredtoken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1Z1VRVE1vRGYxVzh5azJzYmhHb0p0ZmtWVU5CYlRWNCIsImV4cCI6MTU4MjE3NDc0OH0.KYhYFG-v_WvZ39kEVjFRqUaAhx3Pn-FrjRRYbjBCQJynuZuXkcpzNk31Me1g57zHLx974jio3pXcel2GsVtixA";
		
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredtoken);
		
		assertTrue(hasTokenExpired);
	}
}
