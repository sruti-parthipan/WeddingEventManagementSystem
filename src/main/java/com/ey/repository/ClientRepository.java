package com.ey.repository;



import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ey.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);

	Optional<Client> findByName(String name);

	Optional<Client> findByPhone(String phone);

	Optional<Client> findByAddress(String address);

	Optional<Client> findByNameAndEmail(String name, String email);
}
