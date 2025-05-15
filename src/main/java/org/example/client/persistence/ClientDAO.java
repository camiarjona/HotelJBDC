package org.example.client.persistence;

import org.example.client.model.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClientDAO {
        void save(Client client) throws SQLException;
        void delete(Client client) throws SQLException;
        void updateName(Client client) throws SQLException;
        void updateEmail(Client client) throws SQLException;
        List<Client> findAll() throws SQLException;
        Map<Long, Client> getClientsMap() throws SQLException;

}
