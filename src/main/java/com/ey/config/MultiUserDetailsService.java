
package com.ey.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ey.entities.Admin;
import com.ey.entities.Client;
import com.ey.entities.Vendor;
import com.ey.enums.Role;
import com.ey.repository.AdminRepository;
import com.ey.repository.ClientRepository;
import com.ey.repository.VendorRepository;

@Service
public class MultiUserDetailsService implements UserDetailsService {

	private final AdminRepository adminRepo;
	private final ClientRepository clientRepo;
	private final VendorRepository vendorRepo;

	public MultiUserDetailsService(AdminRepository adminRepo, ClientRepository clientRepo,
			VendorRepository vendorRepo) {
		this.adminRepo = adminRepo;
		this.clientRepo = clientRepo;
		this.vendorRepo = vendorRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Check Admin
		Admin admin = adminRepo.findByEmail(email).orElse(null);
		if (admin != null) {
			return new UserPrincipal(admin.getId(), admin.getEmail(), admin.getPassword(), Role.ADMIN, admin.getName());
		}

		// Check Client
		Client client = clientRepo.findByEmail(email).orElse(null);
		if (client != null) {
			return new UserPrincipal(client.getId(), client.getEmail(), client.getPassword(), Role.CLIENT,
					client.getName());
		}

		// Check Vendor
		Vendor vendor = vendorRepo.findByContactEmail(email).orElse(null); // Adjust if vendor uses 'email' field
		if (vendor != null) {
			return new UserPrincipal(vendor.getId(), vendor.getContactEmail(), vendor.getPassword(), Role.VENDOR,
					vendor.getName());
		}

		throw new UsernameNotFoundException("No user found with email: " + email);
	}
}
