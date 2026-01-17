package com.ey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.response.VendorRegistrationResponse;
import com.ey.entities.Vendor;
import com.ey.enums.ServiceType;
import com.ey.mapper.VendorMapper;
import com.ey.repository.VendorRepository;
@Service
public class ClientViewVendorServiceImpl implements ClientViewVendorService{
	@Autowired
	private VendorRepository vendorRepository;
	
	@Override
	public ResponseEntity<?> getAllVendors() {
		// TODO Auto-generated method stub
		
		List<Vendor> vendors = vendorRepository.findAll();
		if(vendors.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No vendors present");
		}
		List<VendorRegistrationResponse> vendorList  = new ArrayList<>();
		for(Vendor vendor : vendors)
		{
			vendorList.add(VendorMapper.vendorToResponse(vendor));
		}
		return ResponseEntity.ok(vendorList);
	}



//	@Override
//	public ResponseEntity<?> getClientByName(String name) {
//		// TODO Auto-generated method stub
//		Optional<Client> optdata = clientRepository.findByName(name);
//		if (optdata.isEmpty()) {
//			//logger.warn("no student with id"+id);
//			return new ResponseEntity<>(" Client Name you entered is not present", HttpStatus.NOT_FOUND);
//		}
//		List<ClientResponse> studentList = new ArrayList<>();
//		studentList.add(ClientMapper.clientToResponse(optdata.get()));
//
//		// return list of studentResponse
//		return ResponseEntity.ok(studentList);
//
//	}
@Override
public ResponseEntity<?> getVendorById(Long id) {
	Optional<Vendor>opt = vendorRepository.findById(id);
    if (opt.isEmpty()) {
      //  logger.warn("no vendor with id {}", id);
        return new ResponseEntity<>("VendorId you entered is not present", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
}

@Override
public ResponseEntity<?> getVendorByName(String name) {
    Optional<Vendor> optdata = vendorRepository.findByName(name);
    if (optdata.isEmpty()) {
      //  logger.warn("no vendor with id {}", id);
        return new ResponseEntity<>("Vendor name you entered is not present", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(VendorMapper.vendorToResponse(optdata.get()));
}
@Override
public ResponseEntity<?> getVendorByEmail(String email) {
    Optional<Vendor> optdata = vendorRepository.findByContactEmail(email);
    if (optdata.isEmpty()) {
      //  logger.warn("no vendor with id {}", id);
        return new ResponseEntity<>("Vendor email you entered is not present", HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(VendorMapper.vendorToResponse(optdata.get()));
}



//public ResponseEntity<?> getVendorByServiceType(String serviceType) {
//    Optional<Vendor> optdata = vendorRepository.findByServiceTypeIgnoreCase(serviceType);
//    if (optdata.isEmpty()) {
//      //  logger.warn("no vendor with id {}", id);
//        return new ResponseEntity<>("Vendor serviceType you entered is not present", HttpStatus.NOT_FOUND);
//    }
//    return ResponseEntity.ok(VendorMapper.vendorToResponse(optdata.get()));
//}

@Override
   public ResponseEntity<?> getVendorByContactPhone(String phone) {
       Optional<Vendor> opt = vendorRepository.findByContactPhone(phone);
       if (opt.isEmpty()) {
          // logger.warn("no vendor with contactPhone {}", phone);
           return new ResponseEntity<>("Vendor phone you entered is not present", HttpStatus.NOT_FOUND);
       }
       return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
   }


@Override
    public ResponseEntity<?> getVendorsByBasePriceGreaterThan(Double amount) {
        List<Vendor> vendors = vendorRepository.findByBasePriceGreaterThan(amount);
        if (vendors == null || vendors.isEmpty()) {
          //  logger.warn("no vendors with basePrice > {}", amount);
            return new ResponseEntity<>("No vendors found for the given base price (greater than)", HttpStatus.NOT_FOUND);
        }
        List<VendorRegistrationResponse> result = vendors.stream()
                .map(VendorMapper::vendorToResponse)
                .toList();
        return ResponseEntity.ok(result);
}

@Override
    public ResponseEntity<?> getVendorsByBasePriceLessThan(Double amount) {
        List<Vendor> vendors = vendorRepository.findByBasePriceLessThan(amount);
        if (vendors == null || vendors.isEmpty()) {
            //logger.warn("no vendors with basePrice < {}", amount);
            return new ResponseEntity<>("No vendors found for the given base price (less than)", HttpStatus.NOT_FOUND);
        }
        List<VendorRegistrationResponse> result = vendors.stream()
                .map(VendorMapper::vendorToResponse)
                .toList();
        return ResponseEntity.ok(result);
    }

@Override
    public ResponseEntity<?> getVendorsByBasePriceBetween(Double min, Double max) {
        List<Vendor> vendors = vendorRepository.findByBasePriceBetween(min, max);
        if (vendors == null || vendors.isEmpty()) {
            //logger.warn("no vendors with basePrice between {} and {}", min, max);
            return new ResponseEntity<>("No vendors found for the given base price range", HttpStatus.NOT_FOUND);
        }
        List<VendorRegistrationResponse> result = vendors.stream()
                .map(VendorMapper::vendorToResponse)
                .toList();
        return ResponseEntity.ok(result);
    }






}
