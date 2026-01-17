package com.ey.mapper;


import com.ey.dto.response.ClientResponse;
import com.ey.entities.Client;
import com.ey.enums.Role;

public class ClientMapper {

public static ClientResponse clientToResponse(Client client) {
       ClientResponse response = new ClientResponse();
       response.setId(client.getId());
       response.setName(client.getName());
       response.setEmail(client.getEmail());
      
       response.setPhone(client.getPhone());
       response.setAddress(client.getAddress());
       response.setRole(client.getRole());
       response.setCreatedAt(client.getCreatedAt());
       response.setUpdatedAt(client.getUpdatedAt());
          return response;
}
}
