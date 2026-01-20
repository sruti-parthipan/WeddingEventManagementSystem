package com.ey.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ey.dto.response.ReviewResponse;
import com.ey.dto.response.VendorRegistrationResponse;
import com.ey.entities.Review;
import com.ey.entities.Vendor;
import com.ey.enums.ServiceType;
import com.ey.exception.InvalidServiceTypeException;
import com.ey.exception.NoRatingsFoundException;
import com.ey.exception.NoReviewsFoundException;
import com.ey.exception.NoVendorsFoundException;
import com.ey.exception.VendorFetchException;
import com.ey.exception.VendorNotFoundException;
import com.ey.mapper.ReviewMapper;
import com.ey.mapper.VendorMapper;
import com.ey.repository.ReviewRepository;
import com.ey.repository.VendorRepository;

@Service
public class ClientViewVendorServiceImpl implements ClientViewVendorService {
	@Autowired
	private VendorRepository vendorRepository;
	@Autowired
	private ReviewRepository reviewRepository;

	private static final Logger logger = LoggerFactory.getLogger(ClientViewVendorServiceImpl.class);

//-----------------------------------------------------------------------------------------------------------------------------

	@Override
	public ResponseEntity<?> getAllVendors() {
		logger.info("Get all vendors request started");
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
			logger.info("Fetched vendors successfully");
			return ResponseEntity.ok(vendorList);

		} catch (NoVendorsFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Failed to fetch vendors", ex);
			throw new VendorFetchException("Failed to fetch vendors");
		}
	}

	@Override
	public ResponseEntity<?> getVendorById(Long id) {
		logger.info("Get vendor by id request started");
		Optional<Vendor> opt = vendorRepository.findById(id);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}
		logger.info("Vendor fetched successfully");
		return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
	}

	@Override
	public ResponseEntity<?> getVendorByName(String name) {
		logger.info("Get vendor by name request started");
		Optional<Vendor> opt = vendorRepository.findByName(name);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}
		logger.info("Vendor fetched successfully");
		return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
	}

	@Override
	public ResponseEntity<?> getVendorByEmail(String email) {
		logger.info("Get vendor by email request started");
		Optional<Vendor> opt = vendorRepository.findByContactEmail(email);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}
		logger.info("Vendor fetched successfully");
		return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
	}

	@Override
	public ResponseEntity<?> getVendorByContactPhone(String phone) {
		logger.info("Get vendor by phone request started");
		Optional<Vendor> opt = vendorRepository.findByContactPhone(phone);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}
		logger.info("Vendor fetched successfully");
		return ResponseEntity.ok(VendorMapper.vendorToResponse(opt.get()));
	}

	@Override
	public ResponseEntity<?> getVendorsByBasePriceGreaterThan(Double amount) {
		logger.info("Get vendors with base price greater than request started");
		List<Vendor> vendors = vendorRepository.findByBasePriceGreaterThan(amount);
		if (vendors == null || vendors.isEmpty()) {
			logger.warn("No vendors found for base price greater than");
			throw new NoVendorsFoundException("No vendors found for the given base price (greater than)");
		}
		List<VendorRegistrationResponse> vendorList = new ArrayList<>();
		for (Vendor vendor : vendors) {
			vendorList.add(VendorMapper.vendorToResponse(vendor));
		}
		logger.info("Vendors fetched successfully");
		return ResponseEntity.ok(vendorList);
	}

	@Override
	public ResponseEntity<?> getVendorsByBasePriceLessThan(Double amount) {
		logger.info("Get vendors with base price less than request started");
		List<Vendor> vendors = vendorRepository.findByBasePriceLessThan(amount);
		if (vendors == null || vendors.isEmpty()) {
			logger.warn("No vendors found for base price less than");
			throw new NoVendorsFoundException("No vendors found for the given base price (less than)");
		}
		List<VendorRegistrationResponse> result = vendors.stream().map(VendorMapper::vendorToResponse).toList();
		logger.info("Vendors fetched successfully");
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getVendorsByBasePriceBetween(Double min, Double max) {
		logger.info("Get vendors with base price between request started");
		List<Vendor> vendors = vendorRepository.findByBasePriceBetween(min, max);
		if (vendors == null || vendors.isEmpty()) {
			logger.warn("No vendors found for base price range");
			throw new NoVendorsFoundException("No vendors found for the given base price range");
		}
		List<VendorRegistrationResponse> result = vendors.stream().map(VendorMapper::vendorToResponse).toList();
		logger.info("Vendors fetched successfully");
		return ResponseEntity.ok(result);
	}

	@Override
	public ResponseEntity<?> getVendorsByServiceType(String serviceType) {
		logger.info("Get vendors by service type request started");

		if (serviceType == null || serviceType.isBlank()) {
			logger.warn("serviceType is required");
			throw new InvalidServiceTypeException("serviceType is required");
		}

		String normalized = serviceType.trim().toUpperCase().replace('-', '_').replace(' ', '_');

		final ServiceType type;
		try {
			type = ServiceType.valueOf(normalized);
		} catch (IllegalArgumentException ex) {
			logger.warn("Invalid serviceType");
			throw new InvalidServiceTypeException(
					"Invalid serviceType. Allowed values: " + java.util.Arrays.toString(ServiceType.values()));
		}

		List<Vendor> vendors = vendorRepository.findByServiceType(type);
		if (vendors == null || vendors.isEmpty()) {
			logger.warn("No vendors found for serviceType");
			throw new NoVendorsFoundException("No vendors found for serviceType");
		}
		List<VendorRegistrationResponse> vendorList = new ArrayList<>();
		for (Vendor vendor : vendors) {
			vendorList.add(VendorMapper.vendorToResponse(vendor));
		}
		logger.info("Vendors fetched successfully");
		return ResponseEntity.ok(vendorList);
	}

	@Override
	public ResponseEntity<?> getVendorsByServiceTypeAndBasePrice(String serviceType, Double basePrice) {
		logger.info("Get vendors by service type and base price request started");

		ServiceType type = parseServiceType(serviceType);
		if (type == null) {
			logger.warn("Invalid serviceType");
			throw new InvalidServiceTypeException(
					"Invalid serviceType. Allowed values: " + java.util.Arrays.toString(ServiceType.values()));
		}
		if (basePrice == null) {
			logger.warn("basePrice is required");
			throw new InvalidServiceTypeException("basePrice is required");
		}
		if (basePrice < 0) {
			logger.warn("basePrice cannot be negative");
			throw new InvalidServiceTypeException("basePrice cannot be negative");
		}

		List<Vendor> vendors = vendorRepository.findByServiceTypeAndBasePrice(type, basePrice);
		if (vendors == null || vendors.isEmpty()) {
			logger.warn("No vendors found for serviceType and basePrice");
			throw new NoVendorsFoundException("No vendors found for the given filter");
		}
		List<VendorRegistrationResponse> vendorList = new ArrayList<>();
		for (Vendor vendor : vendors) {
			vendorList.add(VendorMapper.vendorToResponse(vendor));
		}
		logger.info("Vendors fetched successfully");
		return ResponseEntity.ok(vendorList);
	}

	private ServiceType parseServiceType(String input) {
		if (input == null)
			return null;
		String normalized = input.trim().replace('-', ' ').replace('_', ' ').replaceAll("\\s+", " ").toUpperCase()
				.replace(" ", "");
		for (ServiceType t : ServiceType.values()) {
			String enumKey = t.name().toUpperCase().replace("_", "");
			if (enumKey.equals(normalized)) {
				return t;
			}
		}
		return null;
	}

	@Override
	public ResponseEntity<?> listReviewsForVendor(Long vendorId, String email) {
		logger.info("List reviews for vendor request started");

		Optional<Vendor> opt = vendorRepository.findById(vendorId);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}

		List<Review> reviews = reviewRepository.findByVendor_IdOrderByCreatedAtDesc(vendorId);
		if (reviews == null || reviews.isEmpty()) {
			logger.warn("No reviews found for this vendor");
			throw new NoReviewsFoundException("No reviews found for this vendor");
		}

		List<ReviewResponse> body = reviews.stream().map(ReviewMapper::toDto).toList();

		logger.info("Reviews fetched successfully");
		return ResponseEntity.ok(body);
	}

	@Override
	public ResponseEntity<?> getRatingsForVendor(Long vendorId, String email) {
		logger.info("Get ratings for vendor request started");

		Optional<Vendor> opt = vendorRepository.findById(vendorId);
		if (opt.isEmpty()) {
			logger.warn("Vendor not found");
			throw new VendorNotFoundException("Vendor not found");
		}

		List<Review> reviews = reviewRepository.findByVendor_IdOrderByCreatedAtDesc(vendorId);
		if (reviews == null || reviews.isEmpty()) {
			logger.warn("No ratings found for this vendor");
			throw new NoRatingsFoundException("No ratings found for this vendor");
		}

		double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

		Map<String, Object> resp = new HashMap<>();
		resp.put("vendorId", vendorId);
		resp.put("totalReviews", reviews.size());
		resp.put("averageRating", avg);

		logger.info("Ratings fetched successfully");
		return ResponseEntity.ok(resp);
	}

	@Override
	public ResponseEntity<?> listAllVendorReviews(String email) {
		logger.info("List all vendor reviews request started");

		List<Review> reviews = reviewRepository.findAllByOrderByCreatedAtDesc();
		if (reviews == null || reviews.isEmpty()) {
			logger.warn("No reviews found");
			throw new NoReviewsFoundException("No reviews found");
		}

		List<ReviewResponse> body = reviews.stream().map(ReviewMapper::toDto).toList();

		logger.info("Reviews fetched successfully");
		return ResponseEntity.ok(body);
	}
}
