package com.mycompanyname.app.ws.ui.contriller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.mycompanyname.app.ws.service.impl.UserServiceImpl;
import com.mycompanyname.app.ws.shared.dto.AddressDto;
import com.mycompanyname.app.ws.shared.dto.UserDto;
import com.mycompanyname.app.ws.ui.controller.UserController;
import com.mycompanyname.app.ws.ui.model.response.UserRest;

class UserControllerTest {
	
	@InjectMocks
	UserController userController;
	
	@Mock
	UserServiceImpl userService;
	
	UserDto userDto;
	
	final String USER_ID = "jsfskjdf353vdg";
	final String encryptedPassword = "74rtetwre5473jhg";
	

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		userDto = new UserDto();
		userDto.setFirstName("Mo");
		userDto.setLastName("Smith");
		userDto.setPassword("8131832");
		userDto.setEmail("test@test.com");
		userDto.setUserId(USER_ID);
		userDto.setAddresses(getAddressesDto());
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken(null);
		userDto.setEncryptedPassword(encryptedPassword);
	}

	
	@Test
	final void testGetUser() {
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserRest userRest = userController.getUser(USER_ID);
		
		assertNotNull(userRest);
		assertEquals(USER_ID, userRest.getUserId());
		assertEquals(userDto.getFirstName(), userRest.getFirstName());
		assertEquals(userDto.getLastName(), userRest.getLastName());
		assertTrue(userDto.getAddresses().size() == userRest.getAddresses().size());
	}

	
	private List<AddressDto> getAddressesDto() {
		AddressDto shippingAddressDto = new AddressDto();
		shippingAddressDto.setType("shipping");
		shippingAddressDto.setCity("Winnpeg");
		shippingAddressDto.setCountry("Canada");
		shippingAddressDto.setPostalCode("ABC123");
		shippingAddressDto.setStreetName("123 street name");
		

		AddressDto billingAddressDto = new AddressDto();
		billingAddressDto.setType("billing");
		billingAddressDto.setCity("Winnpeg");
		billingAddressDto.setCountry("Canada");
		billingAddressDto.setPostalCode("ABC123");
		billingAddressDto.setStreetName("123 street name");
		
		
		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(shippingAddressDto);
		addresses.add(billingAddressDto);
		
		return addresses;
	}
	
}
