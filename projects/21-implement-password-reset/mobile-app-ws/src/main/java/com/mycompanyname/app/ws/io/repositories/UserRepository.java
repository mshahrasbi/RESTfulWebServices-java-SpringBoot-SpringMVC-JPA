package com.mycompanyname.app.ws.io.repositories;

import org.springframework.data.repository.*;
import org.springframework.stereotype.Repository;

import com.mycompanyname.app.ws.io.entity.UserEntity;

@Repository
//public interface UserRepository extends CrudRepository<UserEntity, Long> {
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity findUserByEmailVerificationToken(String token);
}
