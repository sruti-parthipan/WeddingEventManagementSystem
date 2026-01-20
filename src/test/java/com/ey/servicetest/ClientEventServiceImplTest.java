
package com.ey.servicetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ey.dto.response.EventRegistrationResponse;
import com.ey.entities.Client;
import com.ey.entities.Event;
import com.ey.enums.EventStatus;
import com.ey.repository.ClientRepository;
import com.ey.repository.EventRepository;
import com.ey.service.ClientEventServiceImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class ClientEventServiceImplTest {

    @Autowired
    private ClientEventServiceImpl clientEventService; 

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EventRepository eventRepository;

    private Client clientA;
    private Client clientB;

    @BeforeEach
    void setUp() {
        
        clientA = new Client();
        clientA.setName("Alice");
        clientA.setEmail("alice@example.com");
                clientA.setPassword("x");
        clientA = clientRepository.saveAndFlush(clientA);

        clientB = new Client();
        clientB.setName("Bob");
        clientB.setEmail("bob@example.com");
        clientB.setPassword("xx"); 
        clientB = clientRepository.saveAndFlush(clientB);

        
        Event e1 = new Event();
        e1.setTitle("Wedding");
        e1.setVenue("Hall A");
        e1.setCapacity(100);
        e1.setStatus(EventStatus.PLANNED);
        e1.setClient(clientA);

        Event e2 = new Event();
        e2.setTitle("Wedding");
        e2.setVenue("Hall B");
        e2.setCapacity(200);
        e2.setStatus(EventStatus.PLANNED);
        e2.setClient(clientA);

        Event e3 = new Event();
        e3.setTitle("Wedding");
        e3.setVenue("Hall C");
        e3.setCapacity(150);
        e3.setStatus(EventStatus.PLANNED);
        e3.setClient(clientB);

        Event e4 = new Event();
        e4.setTitle("Reception");
        e4.setVenue("Hall D");
        e4.setCapacity(120);
        e4.setStatus(EventStatus.PLANNED);
        e4.setClient(clientA);

        eventRepository.saveAllAndFlush(Arrays.asList(e1, e2, e3, e4));
    }

    @Test
    void listMyEventsByTitle_clientFlow_returnsOnlyMyEventsWithMatchingTitle_ignoreCase() {
      
        String title = "wedding";
        String email = "alice@example.com";

        ResponseEntity<?> response = clientEventService.listMyEventsByTitle(title, email);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        List<EventRegistrationResponse> list = (List<EventRegistrationResponse>) response.getBody();

       
        assertEquals(2, list.size());
        for (EventRegistrationResponse r : list) {
            assertEquals("Wedding", r.getTitle());
        }
    }

    @Test
    void listMyEventsByTitle_clientFlow_noMatches_throwsNoEventsFoundException() {
        String title = "NonExistingTitle";
        String email = "alice@example.com";

        assertThrows(
            com.ey.exception.NoEventsFoundException.class,
            () -> clientEventService.listMyEventsByTitle(title, email)
        );
    }
}

