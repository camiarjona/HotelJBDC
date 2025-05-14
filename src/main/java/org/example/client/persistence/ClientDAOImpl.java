package org.example.client.persistence;

import org.example.client.model.Client;
import org.example.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientDAOImpl implements ClientDAO {

    private final Connection connection;

    public ClientDAOImpl(Connection connection) throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Client client) {

    }

    @Override
    public Optional<Client> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Client> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void updateName(Client client) {

    }

    @Override
    public void updateEmail(Client client) {

    }

    @Override
    public List<Client> findAll() {
        return List.of();
    }
}
