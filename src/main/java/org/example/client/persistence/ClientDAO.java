package org.example.client.persistence;

import org.example.client.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {
        void save(Client client);
        Optional<Client> findById(Long id);
        Optional<Client> findByEmail(String email);
        void deleteById(Long id);
        void updateName(Client client);
        void updateEmail(Client client);
        List<Client> findAll();


}
