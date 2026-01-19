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
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
import com.ey.exception.AdminCreationException;
import com.ey.exception.AdminLoginException;
import com.ey.exception.AuthenticationFailedException;
import com.ey.exception.EmailAlreadyExistsException;
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
//    @Override
//    @Transactional
//    public ResponseEntity<?> createAdmin(AdminRegistrationRequest request) {
//        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
//            return ResponseEntity.badRequest().body("Email already exists");
//        }
//
//        Admin admin = new Admin();
//        admin.setName(request.getName());
//        admin.setEmail(request.getEmail());
//        admin.setPassword(passwordEncoder.encode(request.getPassword()));
//        admin.setRole(Role.ADMIN);
//
//        Admin saved = adminRepository.save(admin);
//
//        AdminRegistrationResponse resp = new AdminRegistrationResponse();
//        resp.setId(saved.getId());
//        resp.setName(saved.getName());
//        resp.setEmail(saved.getEmail());
//        resp.setRole(saved.getRole());
//        resp.setCreatedAt(saved.getCreatedAt() != null
//                ? saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
//        resp.setUpdatedAt(saved.getUpdatedAt() != null
//                ? saved.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
//
//        return ResponseEntity.ok(resp);
//    }

//--------------------------------------------------------------------------CREATE ADMIN
@Override
@Transactional
public ResponseEntity<?> createAdmin(AdminRegistrationRequest request) {

    logger.info("Admin registration request received for email=" + request.getEmail());

    // 1) Known validation: duplicate email
    if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
        logger.warn("Registration failed: Email already exists -> " + request.getEmail());
        throw new EmailAlreadyExistsException(request.getEmail());
    }

    try {
        Admin admin = new Admin();
        admin.setName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setRole(Role.ADMIN);

        Admin saved = adminRepository.save(admin);
        logger.info("Admin saved successfully. ID=" + saved.getId());

        AdminRegistrationResponse resp = new AdminRegistrationResponse();
        resp.setId(saved.getId());
        resp.setName(saved.getName());
        resp.setEmail(saved.getEmail());
        resp.setRole(saved.getRole());
        resp.setCreatedAt(saved.getCreatedAt() != null
                ? saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        resp.setUpdatedAt(saved.getUpdatedAt() != null
                ? saved.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);

        logger.info("Admin registration success for email=" + saved.getEmail());
        return ResponseEntity.ok(resp);

    } catch (org.springframework.dao.DataIntegrityViolationException ex) {
        // e.g., DB constraints (unique index, nulls, length)
        logger.warn("Data integrity violation while creating admin for email="
                + request.getEmail() + " -> " + ex.getMessage());
        throw new AdminCreationException("Data integrity violation while creating admin");

    } catch (Exception ex) {
        // Any unknown error
        logger.error("Unexpected error while creating admin for email="
                + request.getEmail() + " -> " + ex.getMessage(), ex);
        throw new AdminCreationException("Failed to create admin");
    }
}

//-----------------------------------------------------------------------------------------------------LOGIN
//    @Override
//    public ResponseEntity<?> loginAdmin(AdminLoginRequest request) {
//    	String email = request.getEmail();
//    	String rawPassword = request.getPassword();
//       Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(email, rawPassword)
//        );
//
//        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
//        String token = jwtUtil.generateToken(principal.getUsername());
//
//        AdminLoginResponse resp = new AdminLoginResponse();
//        resp.setToken(token);
//        resp.setId(principal.getId());
//        resp.setName(principal.getDisplayName());
//        resp.setEmail(principal.getUsername());
//        resp.setRole(principal.getAppRole().name());
//
//        return ResponseEntity.ok(resp);
//    }


@Override
public ResponseEntity<?> loginAdmin(AdminLoginRequest request) {
    String email = request.getEmail();
    String rawPassword = request.getPassword();

    logger.info("Admin login attempt for email=" + email);

    try {
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

        logger.info("Admin login success for email=" + email);
        return ResponseEntity.ok(resp);

    } catch (BadCredentialsException ex) {
        logger.warn("Admin login failed: Bad credentials -> " + email);
        throw new AuthenticationFailedException("Invalid email or password");

    } catch (DisabledException ex) {

logger.warn("Admin login failed: Account disabled -> " + email);
        throw new AuthenticationFailedException("Account disabled. Contact support");

    } catch (LockedException ex) {
        logger.warn("Admin login failed: Account locked -> " + email);
        throw new AuthenticationFailedException("Account locked. Contact support");

    } catch (AccountExpiredException ex) { // optional
        logger.warn("Admin login failed: Account expired -> " + email);
        throw new AuthenticationFailedException("Account has expired. Contact support");

    } catch (CredentialsExpiredException ex) { // optional
        logger.warn("Admin login failed: Credentials expired -> " + email);
        throw new AuthenticationFailedException("Password has expired. Reset your password");

} catch (Exception ex) {
        logger.error("Unexpected error during admin login for email=" + email + " -> " + ex.getMessage(), ex);
        throw new AdminLoginException("Login failed");
    }
}

//@Override
//public ResponseEntity<?> loginAdmin(AdminLoginRequest request) {
//    String email = request.getEmail();
//    String rawPassword = request.getPassword();
//
//    logger.info("Admin login attempt for email=" + email);
//
//    try {
//        Authentication auth = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(email, rawPassword)
//        );
//
//        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
//        String token = jwtUtil.generateToken(principal.getUsername());
//
//        AdminLoginResponse resp = new AdminLoginResponse();
//        resp.setToken(token);
//        resp.setId(principal.getId());
//        resp.setName(principal.getDisplayName());
//        resp.setEmail(principal.getUsername());
//        resp.setRole(principal.getAppRole().name());
//
//        logger.info("Admin login success for email=" + email);
//        return ResponseEntity.ok(resp);
//
//    } catch (org.springframework.security.authentication.BadCredentialsException ex) {
//        logger.warn("Admin login failed: Bad credentials -> " + email);
//        throw new AuthenticationFailedException("Invalid email or password");
//
//    } catch (org.springframework.security.authentication.DisabledException ex) {
//        logger.warn("Admin login failed: Account disabled -> " + email);
//        throw new AuthenticationFailedException("Invalid email or password");
//
//    } catch (org.springframework.security.authentication.LockedException ex) {
//        logger.warn("Admin login failed: Account locked -> " + email);
//        throw new AuthenticationFailedException("Invalid email or password");
//
//    } catch (Exception ex) {
//        logger.error("Unexpected error during admin login for email="
//                + email + " -> " + ex.getMessage(), ex);
//        // keep it simple now; your global handler can map this to 500
//        throw new AdminLoginException("Login failed");
//    }
//}

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
