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
        List<VendorRegistrationResponse> vendorList  = new ArrayList<>();
		for(Vendor vendor : vendors)
		{
			vendorList.add(VendorMapper.vendorToResponse(vendor));
		}
		return ResponseEntity.ok(vendorList);
        
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








//Service impl: parse String -> Enum (case-insensitive, tolerant of spaces/hyphens/underscores)
@Override
public ResponseEntity<?> getVendorsByServiceType(String serviceType) {
 if (serviceType == null || serviceType.isBlank()) {
     return new ResponseEntity<>("serviceType is required", HttpStatus.BAD_REQUEST);
 }

 // Normalize: trim, upper, replace spaces/hyphens with underscore
 String normalized = serviceType.trim()
                               .toUpperCase()
                               .replace('-', '_')
                               .replace(' ', '_');

 final ServiceType type;
 try {
     type = ServiceType.valueOf(normalized);
 } catch (IllegalArgumentException ex) {
     return new ResponseEntity<>(
         "Invalid serviceType. Allowed values: " + java.util.Arrays.toString(ServiceType.values()),
         HttpStatus.BAD_REQUEST
     );
 }

 List<Vendor> vendors = vendorRepository.findByServiceType(type);
 if (vendors == null || vendors.isEmpty()) {
     return new ResponseEntity<>("No vendors found for serviceType: " + type, HttpStatus.NOT_FOUND);
 }
 List<VendorRegistrationResponse> vendorList  = new ArrayList<>();
 for(Vendor vendor : vendors)
	{
		vendorList.add(VendorMapper.vendorToResponse(vendor));
	}
	return ResponseEntity.ok(vendorList);

}




@Override
public ResponseEntity<?> getVendorsByServiceTypeAndBasePrice(String serviceType, Double basePrice) {
    // --- Parse & validate inputs here (both serviceType and basePrice) ---

    // 1) Parse serviceType String -> Enum (case-insensitive, tolerant of spaces, hyphens, underscores)
    ServiceType type = parseServiceType(serviceType);
    if (type == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid serviceType: '" + serviceType + "'. Allowed: " +
                        java.util.Arrays.toString(ServiceType.values()));
    }

    // 2) Validate basePrice (Double) is present and non-negative (you can relax this if not needed)
    if (basePrice == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("basePrice is required.");
    }
    if (basePrice < 0) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("basePrice cannot be negative.");
    }

    // --- Repository call ---
    List<Vendor> vendors = vendorRepository.findByServiceTypeAndBasePrice(type, basePrice);
    if (vendors == null || vendors.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No vendors found for serviceType '" + type + "' with basePrice " + basePrice);
    }

    // --- Map to response DTOs ---
    List<VendorRegistrationResponse> vendorList = new ArrayList<>();
    for (Vendor vendor : vendors) {
        vendorList.add(VendorMapper.vendorToResponse(vendor));
    }
    return ResponseEntity.ok(vendorList);
}

/* ---- helpers (keep in the same service impl) ---- */
private ServiceType parseServiceType(String input) {
    if (input == null) return null;

    // Normalize: trim, unify separators, collapse spaces, uppercase, remove spaces/underscores
    String normalized = input.trim()
            .replace('-', ' ')
            .replace('_', ' ')
            .replaceAll("\\s+", " ")
            .toUpperCase()
            .replace(" ", ""); // e.g., "Photo Graphy" -> "PHOTOGRAPHY"

    for (ServiceType t : ServiceType.values()) {
        String enumKey = t.name().toUpperCase().replace("_", "");
        if (enumKey.equals(normalized)) {
            return t;
        }
    }
    return null;
}







   
}