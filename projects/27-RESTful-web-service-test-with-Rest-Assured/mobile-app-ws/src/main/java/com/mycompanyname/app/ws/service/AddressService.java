package com.mycompanyname.app.ws.service;

import java.util.List;

import com.mycompanyname.app.ws.shared.dto.AddressDto;

public interface AddressService {
	
	List<AddressDto> getAddresses(String userId);
	AddressDto getAddress(String addressId);

}
