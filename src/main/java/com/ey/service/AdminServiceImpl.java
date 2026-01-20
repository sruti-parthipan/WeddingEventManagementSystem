
package com.ey.service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ey.exception.ClientFetchException;
import com.ey.exception.ClientNotFoundException;
import com.ey.exception.EmailAlreadyExistsException;
import com.ey.exception.NoClientsFoundException;
import com.ey.exception.NoVendorsFoundException;
import com.ey.exception.VendorFetchException;
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
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	@Transactional
	public ResponseEntity<?> createAdmin(AdminRegistrationRequest request) {

		logger.info("Admin registration request received for email=" + request.getEmail());

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
			resp.setCreatedAt(saved.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			resp.setUpdatedAt(saved.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

			logger.info("Admin registration success for email=" + saved.getEmail());
			return ResponseEntity.ok(resp);

		} catch (Exception ex) {
			
			logger.error(
					"Unexpected error while creating admin for email=" + request.getEmail() + " -> " + ex.getMessage(),
					ex);
			throw new AdminCreationException("Failed to create admin");
		}
	}

	@Override
	public ResponseEntity<?> loginAdmin(AdminLoginRequest request) {
		String email = request.getEmail();
		String rawPassword = request.getPassword();

		logger.info("Admin login attempt for email=" + email);

		try {
			Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));

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
			logger.error("Unexpected error during admin login for email=" + email);
			throw new AdminLoginException("Login failed");
		}
	}

	@Override
	public ResponseEntity<?> getAllClients() {

		logger.info("Get all clients request received");

		try {
			List<Client> clients = clientRepository.findAll();

			if (clients == null || clients.isEmpty()) {
				logger.warn("No clients present");
				throw new NoClientsFoundException("No clients present");
			}

			List<ClientResponse> clientList = new ArrayList<>();
			for (Client client : clients) {
				clientList.add(ClientMapper.clientToResponse(client));
			}

			logger.info("Fetched clients successfully. count=" + clientList.size());
			return ResponseEntity.ok(clientList);

		} catch (NoClientsFoundException ex) {

			throw ex;

		} catch (Exception ex) {
			logger.error("Unexpected error while fetching clients: " + ex.getMessage(), ex);
			throw new ClientFetchException("Failed to fetch clients");
		}
	}

	@Override
	public ResponseEntity<?> getAllVendors() {

		logger.info("Get all vendors request received");

		try {
			List<Vendor> vendors = vendorRepository.findAll();

			if (vendors == null || vendors.isEmpty()) {
				logger.warn("No vendors present");
				throw new NoVendorsFoundException("No vendors present");
			}

			List<VendorRegistrationResponse> vendorList = new ArrayList<>();
			for (Vendor vendor : vendors) {
				vendorList.add(VendorMapper.vendorToResponse(vendor));
			}

			logger.info("Fetched vendors successfully. count=" + vendorList.size());
			return ResponseEntity.ok(vendorList);

		} catch (NoVendorsFoundException ex) {
			// Known case, rethrow so your global handler can map status later
			throw ex;

		} catch (Exception ex) {
			logger.error("Unexpected error while fetching vendors: ");
			throw new VendorFetchException("Failed to fetch vendors");
		}
	}

	@Override
	public ResponseEntity<?> getClientById(Long id) {
		logger.info("Get client by id request received id=" + id);

		try {
			Client client = clientRepository.findById(id).orElseThrow(() -> {
				logger.warn("Client not found by id=" + id);
				return new ClientNotFoundException("Client not found for id: " + id);
			});

			ClientResponse response = ClientMapper.clientToResponse(client);
			logger.info("Client fetched successfully id=" + id);
			return ResponseEntity.ok(response);

		} catch (ClientNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Unexpected error in getClientById id=" + id + " -> " + ex.getMessage(), ex);
			throw ex; // let global handler return 500 later
		}
	}

	@Override
	public ResponseEntity<?> getClientByName(String name) {
		logger.info("Get client by name request received name=" + name);

		Client client = clientRepository.findByName(name).orElseThrow(() -> {
			logger.warn("Client not found by name=" + name);
			return new ClientNotFoundException("Client not found for name: " + name);
		});

		List<ClientResponse> result = new ArrayList<>();
		result.add(ClientMapper.clientToResponse(client));

		logger.info("Client fetched by name successfully name=" + name);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getClientByEmail(String email) {
		logger.info("Get client by email request received email=" + email);

		Client client = clientRepository.findByEmail(email).orElseThrow(() -> {
			logger.warn("Client not found by email=" + email);
			return new ClientNotFoundException("Client not found for email: " + email);
		});

		List<ClientResponse> result = new ArrayList<>();
		result.add(ClientMapper.clientToResponse(client));

		logger.info("Client fetched by email successfully email=" + email);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getClientByPhone(String phone) {
		logger.info("Get client by phone request received phone=" + phone);

		Client client = clientRepository.findByPhone(phone).orElseThrow(() -> {
			logger.warn("Client not found by phone=" + phone);
			return new ClientNotFoundException("Client not found for phone: " + phone);
		});

		List<ClientResponse> result = new ArrayList<>();
		result.add(ClientMapper.clientToResponse(client));

		logger.info("Client fetched by phone successfully phone=" + phone);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getClientByAddress(String address) {
		logger.info("Get client by address request received address=" + address);

		Client client = clientRepository.findByAddress(address).orElseThrow(() -> {
			logger.warn("Client not found by address=" + address);
			return new ClientNotFoundException("Client not found for address: " + address);
		});

		List<ClientResponse> result = new ArrayList<>();
		result.add(ClientMapper.clientToResponse(client));

		logger.info("Client fetched by address successfully address=" + address);
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getClientByNameAndEmail(String name, String email) {
		logger.info("Get client by name & email request received name=" + name + ", email=" + email);

		Client client = clientRepository.findByNameAndEmail(name, email).orElseThrow(() -> {
			logger.warn("Client not found by name=" + name + " and email=" + email);
			return new ClientNotFoundException("No client found with given name and email");
		});

		logger.info("Client fetched by name & email successfully name=" + name + ", email=" + email);
		return ResponseEntity.ok(ClientMapper.clientToResponse(client));
	}

}
