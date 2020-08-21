package com.mycompanyname.app.ws.ui.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import com.mycompanyname.app.ws.exception.UserServiceException;
import com.mycompanyname.app.ws.service.AddressService;
import com.mycompanyname.app.ws.service.UserService;
import com.mycompanyname.app.ws.shared.Roles;
import com.mycompanyname.app.ws.shared.dto.AddressDto;
import com.mycompanyname.app.ws.shared.dto.UserDto;
import com.mycompanyname.app.ws.ui.model.request.PasswordResetModel;
import com.mycompanyname.app.ws.ui.model.request.PasswordResetRequestModel;
import com.mycompanyname.app.ws.ui.model.request.UserDetailsRequestModel;
import com.mycompanyname.app.ws.ui.model.response.*;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


@RestController	
@RequestMapping("/users")	//http://localhost:8080/users/
// @CrossOrigin(origins="*")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	AddressService addressesService;

	@PostAuthorize("hasRole('ADMIN') or returnObject.userId == principal.userId")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@ApiOperation(value="The Get User Details Web Service Endpoint", 
				  notes="${userController.GetUser.ApiOperation.Notes}")
	@GetMapping(path="/{id}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	// @CrossOrigin(origins= {"http://localhost:8088", "http://localhost:8083"})
	public  UserRest getUser(@PathVariable String id) {

		UserDto userDto = userService.getUserByUserId(id);
		//BeanUtils.copyProperties(userDto, returnValue);
		ModelMapper modelMapper = new ModelMapper();
		UserRest returnValue = modelMapper.map(userDto, UserRest.class);
		
		return returnValue;
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@PostMapping(
			consumes= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails ) throws Exception {
		UserRest returnValue = new UserRest();
		
		if (userDetails == null) throw new NullPointerException("The Object is null"); // it trigger the handleOtherException

		// if (userDetails.getFirstName().isEmpty()) throw new Exception(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		if (userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		
		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails,  userDto);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		userDto.setRoles(new HashSet<>(Arrays.asList(Roles.ROLE_USER.name())));
		
		UserDto createdUser = userService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		return returnValue;
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@PutMapping(path="/{id}",
			consumes= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails ) {
		UserRest returnValue = new UserRest();
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails,  userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);

		return returnValue;
	}
	
	// @Secured("ROLE_ADMIN")
	// @PreAuthorize("hasAuthority('DELETE_AUTHORITY')") //OR @PreAuthorize("hasRole('ADMIN')")
	@PreAuthorize("hasROle('ROLE_ADMIN') or #id == principal.userId")
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@DeleteMapping(path="/{id}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@GetMapping(produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUsers(@RequestParam(value="page", defaultValue="0")int page, @RequestParam(value="limit", defaultValue="25")int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		
		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto,  userModel);
			
			returnValue.add(userModel);
		}
		return returnValue;
	}
	
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	// http://localhost:8080/mobile-app-ws/users/<user-id>/addresses
	@GetMapping(path="/{id}/addresses", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public  Resources<AddressesRest> getUserAddresses(@PathVariable String id) {
		
		List<AddressesRest> addressesListRestModel = new ArrayList<>();
		List<AddressDto> addressesDto = addressesService.getAddresses(id);

		if (addressesDto != null && !addressesDto.isEmpty()) {
			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDto, listType);
			
			for (AddressesRest addressRest : addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				addressRest.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}
		return new Resources<AddressesRest>(addressesListRestModel);
	}
	
	// http://localhost:8080/mobile-app-ws/users/<user-id>/addresses/<address-id>
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@GetMapping(path="/{userId}/addresses/{addressId}", produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
	public  Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDto addressDto = addressService.getAddress(addressId);
		
		ModelMapper modelMapper = new ModelMapper();
		
		/*
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("addresses");
		Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
		*/
		Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
		Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		
		
		AddressesRest addressesRestModel = modelMapper.map(addressDto, AddressesRest.class);
		
		addressesRestModel.add(userLink);
		addressesRestModel.add(addressesLink);
		addressesRestModel.add(addressLink);
		
		return new Resource<>(addressesRestModel);
	}
	
	/*
	 * http://localhost:8080/mobile-app-ws/users/email-verification?token=sdfsdf
	 */
	@GetMapping(path = "/email-verification", produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel verifyEmailToken(@RequestParam(value = "token")String token) {
		
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		
		boolean isVerified = userService.verifyEmailToken(token);
		
		if (isVerified) {
			returnValue.setOperationName(RequestOperationStatus.SUCCESS.name());
		} else {
			returnValue.setOperationName(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}

	/*
	 * http://localhost:808/mobile-app-ws/users/password-reset-request
	 */
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@PostMapping(path = "/password-reset-request",
			produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
			consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationalResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		if (operationalResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}


	/*
	 * http://localhost:808/mobile-app-ws/users/password-reset
	 */
	@ApiImplicitParams({
		@ApiImplicitParam(name="authrization", value="${userController.authorization.description}", paramType="header")
	})
	@PostMapping(path = "/password-reset",
			consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})	
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		
		boolean operationResult = userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword());
		
		returnValue.setOperationName(RequestOperationStatus.PASSWORD_RESET.name());
		returnValue.setOperationName(RequestOperationStatus.ERROR.name());
		
		if (operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		
		return returnValue;
	}
}
