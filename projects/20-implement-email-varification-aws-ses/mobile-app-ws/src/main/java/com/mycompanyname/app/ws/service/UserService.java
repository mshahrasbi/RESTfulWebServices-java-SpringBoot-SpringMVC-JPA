package com.mycompanyname.app.ws.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.mycompanyname.app.ws.shared.dto.UserDto;

public interface UserService extends UserDetailsService {
	
	UserDto createUser(UserDto user);
	UserDto updateUser(String userId, UserDto user);
	void deleteUser(String userId);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	List<UserDto> getUsers(int page, int limit);
	boolean verifyEmailToken(String token);
}
