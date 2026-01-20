package com.ey.mapper;

import com.ey.dto.response.VendorRegistrationResponse;
import com.ey.entities.Vendor;

public class VendorMapper {

	public static VendorRegistrationResponse vendorToResponse(Vendor vendor) {
		// TODO Auto-generated method stub
		VendorRegistrationResponse response = new VendorRegistrationResponse();
		response.setId(vendor.getId());
		response.setName(vendor.getName());
		response.setServiceType(vendor.getServiceType());
		response.setContactEmail(vendor.getContactEmail());
		response.setContactPhone(vendor.getContactPhone());

		response.setBasePrice(vendor.getBasePrice());
		response.setRole(vendor.getRole());

		response.setCreatedAt(vendor.getCreatedAt());
		response.setUpdatedAt(vendor.getUpdatedAt());
		return response;

	}

}
