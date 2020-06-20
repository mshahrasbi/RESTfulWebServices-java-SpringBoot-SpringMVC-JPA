package com.mycompanyname.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mycompanyname.app.ws.io.entity.UserEntity;

@Repository
public interface UserRepository extends PagingAndSortingRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	UserEntity findUserByEmailVerificationToken(String token);
	
	// In our JPA repository we are going to have a method which is going to use Native SQL query
	// what do they do? Well first of all we have added this method here earlier in our example we were using spring data JPA query
	// method for example to fetch a user details from a db by thier email address we would write a query method that reads findNyEmail,
	// this time we are no going to follow the pattern of query method although we could but we can name our methods whatever we want, so
	// I am adding a new method that will return all the users from our database that have email address verified. But the list of users
	// on a db will be returned by an SQL query that is provided inside of @Query.
	// @Query takes 'value', 'nativequery'. These two are very important. If we don't provide nativeQuery=true and if we don't provide
	// 'value' but simply use an query and inside of query we provide the sql query, then that is going to be treated as Java persisted query
	// language (JPA)
	// 'countQuery' is being used by spring data JPA to figure out the total number of records that is needs to split in two different pages
	@Query(value="select * from Users u where u.EMAIL_VERIFICATION)STATUS = 'true'", 
			countQuery="select count(*) from Users u where u.EMAIL_VERIFICATION)STATUS = 'true'",
			nativeQuery = true)
	Page<UserEntity> findAllUsersWithConfirmedEmailAddress(Pageable pageableRequest);
	
	// in the above native SQL query we used for the find all users with confirmed email address the value of clarification status
	// we hard coded in the SQL query itself. for this particular example it might work but in most of the cases the value will need
	// to be passed in the parameter. For example let's say we need to create the method that finds all users by their first name and
	// the value of the first name can be different, can be my name can be bell, can be joe so we cannot really hard code the value 
	// the first name right into their SQL query.
	// so lets learn how to pass in a parameter so that we do not need to hard code the value. There are a couple of ways to do this.
	// lets consider the first one:
	
	@Query(value="select * from Users u where u.first_name = ?1", nativeQuery=true)
	List<UserEntity> findUserByFirstName(String firstName);
	
	
	// Now we will do, how to pass parameters to our SQL query using named parameters. Lets we need to find a user by thier last name
	// thn instead of question mark and position of the parameter we will use a colon and lastName (name of the parameter to our method.
	// here the order of method arguments it doesn't matter as long as they use correct parameter name.

	@Query(value="select * from Users u where u.last_name = :lastName", nativeQuery=true)
	List<UserEntity> findUserByLastName(@Param("lastName") String lastName);
	
	
	
	// using the like keyword in native SQL query
	
	@Query(value="select * from Users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery=true)
	List<UserEntity> findUserByKeyword(@Param("keyword") String keyword);
	
	
	// we want to return certain fields from the SQL query 
	
	@Query(value="select u.first_name, u.last_name from Users u where u.first_name LIKE %:keyword% or u.last_name LIKE %:keyword%", nativeQuery=true)
	List<Object[]> findUserFirstNameAndLastNameByKeyword(@Param("keyword") String keyword);
	
	
	// UPDATE SQL query, This query is going to update record in DB and in case of modifying SQL queries we will need to use another
	// annotation which is called 'Modifying'. you will use 'Modifying' for delete SQL query as well. Since this is a Modifying query we need
	// to use another annotationwhich is called 'Transactional'. It is help us to perform this kind of modifying SQL queries like delete
	// and update. And if an error takes place in the record after we have a update DB record the DB record changes made within the transactions
	// will be rolled back and our record in DB will remain unchanged.
	
	@Transactional
	@Modifying
	@Query(value="update users u set u.EMAI_VERIFICATIONSTATUS=:emailVerificationStatus where u.user_id=:userId", nativeQuery=true)
	void updateUserEmailVerificationStatus(@Param("emailVerificationStatus") boolean emailVerificationSTatus, @Param("userId")String userId);
	
	
	/*
	 * create a new JPQL query and use our userEntity class instead of database table name and database column names to select our record
	 * So lets assume we need to create an SQL query to select one record from our database that matches userId, because this is going to 
	 * be a single record, it will return userEntity and we will call this method findUserEntityUserId, and this method will need to accept
	 * one method parameter which is going to be userId and we need anontated with @Param("userId").
	 * Now lets create the query itself using @Query() and when create JPQL queries thay are a little bit simpler. So here we use the class
	 * name as table 'UserEntity' and use class fields in where clause. This query is simpler and don't have the 'value', and nativeQuery=true
	 * 
	 */
	
	
	@Query("select user from UserEntity user where user.userId=:userId")
	UserEntity findUserEntityByUserId(@Param("userId") String userId);
	
	@Query("select user.firstName, user.lastName from UserEntity user where user.userId=:userId")
	List<Object[]> getUserEntityFullNameById(@Param("userId")String userId);
	
	@Modifying
	@Transactional
	@Query("update UserEntity u set u.emailVerificationStatus =:emailVerificationStatus where u.userId=:userId")
	void updateUserEntityEmailVerificationStatus(@Param("emailVerificationStatus")boolean emailVerificationStatus, @Param("userId")String userId);	
}
