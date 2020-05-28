package com.mycompanyname.app.ws.io.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mycompanyname.app.ws.io.entity.AddressEntity;
import com.mycompanyname.app.ws.io.entity.UserEntity;
import com.mycompanyname.app.ws.io.repositories.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;
	
	static boolean recordsCreated = false;
	
	@BeforeEach
	void setUp() throws Exception {
		if (!recordsCreated) {
			createRecords();
		}
	}

	@Test
	void testGetVerifiedUsers() {

		//Pageable pageableRequest = PageRequest.of(0, 2);
		//Pageable pageableRequest = PageRequest.of(0, 1);
		Pageable pageableRequest = PageRequest.of(1, 1);
		Page<UserEntity> page = userRepository.findAllUsersWithConfirmedEmailAddress(pageableRequest);
		
		assertNotNull(page);
		
		List<UserEntity> userEntities = page.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}
	
	
	@Test
	void testFindUserByFirstName() {
		
		String firstName = "john";
		List<UserEntity> users = userRepository.findUserByFirstName(firstName);
		
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getFirstName().equals(firstName));
	}

	
	@Test
	void testFindUserByLastName() {
		
		String lastName = "john";
		List<UserEntity> users = userRepository.findUserByLastName(lastName);
		
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().equals(lastName));
	}
	
	
	@Test
	void testFindUserByKeyword() {
		
		String keyword = "jo";
		List<UserEntity> users = userRepository.findUserByKeyword(keyword);
		
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		UserEntity user = users.get(0);
		assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
	}
	
	
	@Test
	void testFindUserFirstNameAndLastNameByKeyword() {
		
		String keyword = "jo";
		List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);
		
		assertNotNull(users);
		assertTrue(users.size() == 2);
		
		Object[] user = users.get(0);
		String userFirstName = String.valueOf(user[0]);
		String userLastName = String.valueOf(user[1]);
		
		assertNotNull(userFirstName);
		assertNotNull(userLastName);

		assertTrue(userLastName.contains(keyword) || userFirstName.contains(keyword));
	}
	
	
	@Test
	void testUpdateUserEmailVerificationStatus() {
		
		boolean newEmailVerificationStatus = false;
		userRepository.updateUserEmailVerificationStatus(newEmailVerificationStatus, "1a2a3ds");
		
		UserEntity storedUserDetails = userRepository.findByUserId("1a2a3ds");
		
		boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();
		
		assertTrue(storedEmailVerificationStatus == newEmailVerificationStatus);
	}

	
	private void createRecords() {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("john");
		userEntity.setUserId("1a2a3ds");
		userEntity.setEncryptedPassword("abcd");
		userEntity.setEmail("test@test.com");
		userEntity.setEmailVerificationStatus(true);

		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setType("shipping");
		addressEntity.setAddressId("jfhsjfhaskf");
		addressEntity.setCity("Tucson");
		addressEntity.setCountry("USA");
		addressEntity.setPostalCode("85714");
		addressEntity.setStreetName("123 street address");
		
		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(addressEntity);
		
		userEntity.setAddresses(addresses);
		
		userRepository.save(userEntity);
		
		
		UserEntity userEntity2 = new UserEntity();
		userEntity2.setFirstName("john");
		userEntity2.setUserId("1a2a223ds");
		userEntity2.setEncryptedPassword("abcd");
		userEntity2.setEmail("test@test.com");
		userEntity2.setEmailVerificationStatus(true);

		AddressEntity addressEntity2 = new AddressEntity();
		addressEntity2.setType("shipping");
		addressEntity2.setAddressId("dddsdsjfhsjfhaskf");
		addressEntity2.setCity("Tucson");
		addressEntity2.setCountry("USA");
		addressEntity2.setPostalCode("85714");
		addressEntity2.setStreetName("123 street address");
		
		List<AddressEntity> addresses2 = new ArrayList<>();
		addresses.add(addressEntity2);
		
		userEntity2.setAddresses(addresses2);
		
		userRepository.save(userEntity2);
		
		recordsCreated = true;
	}
}
