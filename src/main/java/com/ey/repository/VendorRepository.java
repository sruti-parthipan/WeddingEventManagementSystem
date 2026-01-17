package com.ey.repository;



import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Vendor;
import com.ey.enums.ServiceType;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByContactEmail(String contactEmail); // or findByEmail(...)

	Optional<Vendor> findByName(String name);

	//Optional<Vendor> findByServiceTypeIgnoreCase(String email);

	Optional<Vendor> findByContactPhone(String phone);

	List<Vendor> findByBasePriceGreaterThan(Double amount);

	List<Vendor> findByBasePriceLessThan(Double amount);

	List<Vendor> findByBasePriceBetween(Double min, Double max);

	List<Vendor> findByServiceType(ServiceType type);

	
}
