
package com.ey.servicetest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.transaction.Transactional;

import com.ey.dto.response.ClientResponse;
import com.ey.entities.Client;
import com.ey.repository.ClientRepository;
import com.ey.service.AdminService;
import com.ey.exception.ClientNotFoundException;

@SpringBootTest
@Transactional
public class AdminServiceImplTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    void setup() {
        
        testClient = new Client();
        testClient.setName("Alice");
        testClient.setEmail("alice@example.com");
        testClient.setPassword("password");
        
        testClient = clientRepository.saveAndFlush(testClient);
    }

    @Test
    void testGetClientById_Success() {
        ResponseEntity<?> response = adminService.getClientById(testClient.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        ClientResponse dto = (ClientResponse) response.getBody();
        assertEquals(testClient.getId(), dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@example.com", dto.getEmail());
    }

    @Test
    void testGetClientById_NotFound() {
        Long missingId = testClient.getId() + 999L;

        assertThrows(
            ClientNotFoundException.class,
            () -> adminService.getClientById(missingId)
        );
    }
}

