//
//
//package com.ey.service;
//
//import com.ey.dto.request.AdminRegistrationRequest;
//import com.ey.dto.response.AdminLoginResponse;
//import com.ey.entities.Admin;
//import com.ey.enums.Role;
//import com.ey.repository.AdminRepository;
//import com.ey.service.AdminService;
//import jakarta.transaction.Transactional;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.format.DateTimeFormatter;
//
//@Service
//public class AdminServiceImpl implements AdminService {
//
//    private final AdminRepository adminRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
//        this.adminRepository = adminRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @Override
//    @Transactional
//    public AdminLoginResponse createAdmin(AdminRegistrationRequest request) {
//        // uniqueness validation here if required (or keep in controller)
//        Admin admin = new Admin();
//        admin.setName(request.getName());
//        admin.setEmail(request.getEmail());
//        admin.setPassword(passwordEncoder.encode(request.getPassword()));
//        admin.setRole(Role.ADMIN);
//
//        Admin saved = adminRepository.save(admin);
//
//        AdminLoginResponse resp = new AdminLoginResponse();
//        resp.setId(saved.getId());
//        resp.setName(saved.getName());
//        resp.setEmail(saved.getEmail());
//        resp.setRole(saved.getRole().name());
//        resp.setCreatedAt(saved.getCreatedAt() != null
//                ? saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
//        resp.setUpdatedAt(saved.getUpdatedAt() != null
//                ? saved.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
//
//        return resp;
//    }
//}




package com.ey.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ey.config.UserPrincipal;
import com.ey.dto.request.AdminLoginRequest;
import com.ey.dto.request.AdminRegistrationRequest;
import com.ey.dto.response.AdminLoginResponse;
import com.ey.dto.response.AdminRegistrationResponse;
import com.ey.dto.response.ClientResponse;
import com.ey.dto.response.VendorRegistrationResponse;
import com.ey.entities.Admin;
import com.ey.entities.Client;
import com.ey.entities.Vendor;
import com.ey.enums.Role;
import com.ey.mapper.ClientMapper;
import com.ey.mapper.VendorMapper;
import com.ey.repository.AdminRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.VendorRepository;
import com.ey.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class AdminServiceImpl implements AdminService {
	Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
	@Autowired
private AdminRepository adminRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
    private  PasswordEncoder passwordEncoder;
	@Autowired
    private AuthenticationManager authenticationManager;

@Autowired
private JwtUtil jwtUtil;
    @Override
    @Transactional
    public ResponseEntity<?> createAdmin(AdminRegistrationRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);

        Admin saved = adminRepository.save(admin);

        AdminRegistrationResponse resp = new AdminRegistrationResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());
        resp.setEmail(saved.getEmail());
        resp.setRole(saved.getRole());
        resp.setCreatedAt(saved.getCreatedAt() != null
                ? saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        resp.setUpdatedAt(saved.getUpdatedAt() != null
                ? saved.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);

        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<?> loginAdmin(AdminLoginRequest request) {
    	String email = request.getEmail();
    	String rawPassword = request.getPassword();
       Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, rawPassword)
        );

        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtUtil.generateToken(principal.getUsername());

        AdminLoginResponse resp = new AdminLoginResponse();
        resp.setToken(token);
        resp.setId(principal.getId());
        resp.setName(principal.getDisplayName());
        resp.setEmail(principal.getUsername());
        resp.setRole(principal.getAppRole().name());

        return ResponseEntity.ok(resp);
    }


	@Override
	public ResponseEntity<?> getAllClients() {
		// Fetch the entities from table
//		List<Client> clients = clientRepository.findAll();
//
//		if (clients.isEmpty()) {
//			return ResponseEntity.status(HttpStatus.OK).body("No clients present");
//		}
//		return ResponseEntity.ok(clients);//returning entity itself or we can cut the passwords
//		
		List<Client> clients = clientRepository.findAll();
		if (clients.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No clients present");
		}
		List<ClientResponse> clientList  = new ArrayList<>();
		for(Client client : clients)
		{
			clientList.add(ClientMapper.clientToResponse(client));
		}
		return ResponseEntity.ok(clientList);
	}

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

@Override
public ResponseEntity<?> getClientById(Long id) {

    Optional<Client> opt = clientRepository.findById(id);

    if (opt.isEmpty()) {
//        logger.warn("no client with id {}", id);
        return new ResponseEntity<>("ClientId you entered is not present",
                                    HttpStatus.NOT_FOUND);
    }
     ClientResponse response = ClientMapper.clientToResponse(opt.get());
    return ResponseEntity.ok(response);
}


@Override
public ResponseEntity<?> getClientByName(String name) {
	// TODO Auto-generated method stub
	Optional<Client> optdata = clientRepository.findByName(name);
	if (optdata.isEmpty()) {
		//logger.warn("no student with id"+id);
		return new ResponseEntity<>(" Client Name you entered is not present", HttpStatus.NOT_FOUND);
	}
	List<ClientResponse> studentList = new ArrayList<>();
	studentList.add(ClientMapper.clientToResponse(optdata.get()));

	// return list of studentResponse
	return ResponseEntity.ok(studentList);

}

@Override
public ResponseEntity<?> getClientByEmail(String email) {
	// TODO Auto-generated method stub
	Optional<Client> optdata = clientRepository.findByEmail(email);
	if (optdata.isEmpty()) {
		//logger.warn("no student with id"+id);
		return new ResponseEntity<>(" Client email you entered is not present", HttpStatus.NOT_FOUND);
	}
	List<ClientResponse> studentList = new ArrayList<>();
	studentList.add(ClientMapper.clientToResponse(optdata.get()));

	// return list of studentResponse
	return ResponseEntity.ok(studentList);
}

@Override
public ResponseEntity<?> getClientByPhone(String phone) {
	// TODO Auto-generated method stub
	Optional<Client> optdata = clientRepository.findByPhone(phone);
	if (optdata.isEmpty()) {
		//logger.warn("no student with id"+id);
		return new ResponseEntity<>(" Client phone no you entered is not present", HttpStatus.NOT_FOUND);
	}
	List<ClientResponse> studentList = new ArrayList<>();
	studentList.add(ClientMapper.clientToResponse(optdata.get()));

	// return list of studentResponse
	return ResponseEntity.ok(studentList);
}

@Override
public ResponseEntity<?> getClientByAddress(String address) {
	// TODO Auto-generated method stub
	Optional<Client> optdata = clientRepository.findByAddress(address);
	if (optdata.isEmpty()) {
		//logger.warn("no student with id"+id);
		return new ResponseEntity<>(" Client phone no you entered is not present", HttpStatus.NOT_FOUND);
	}
	List<ClientResponse> studentList = new ArrayList<>();
	studentList.add(ClientMapper.clientToResponse(optdata.get()));

	// return list of studentResponse
	return ResponseEntity.ok(studentList);
}

@Override
public ResponseEntity<?> getClientByNameAndEmail(String name, String email) {

    Optional<Client> opt = clientRepository.findByNameAndEmail(name, email);

    if (opt.isEmpty()) {
        //logger.warn("no client with name {} AND address {}", name, email);
        return new ResponseEntity<>(
                "No client found with given name AND email",
                HttpStatus.NOT_FOUND
        );
    }

    return ResponseEntity.ok(ClientMapper.clientToResponse(opt.get()));
}


}
