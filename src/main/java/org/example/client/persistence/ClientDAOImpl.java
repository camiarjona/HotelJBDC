package org.example.client.persistence;

import org.example.client.model.Client;
import org.example.connection.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ClientDAOImpl implements ClientDAO {

    private final Connection connection;

    public ClientDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, email) VALUES (?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getName());
            ps.setString(2, client.getEmail());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    client.setId(rs.getLong(1));
                }
            }
        }
    }


    @Override
    public void delete(Client client) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, client.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateName(Client client) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ? WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setLong(2, client.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateEmail(Client client) throws SQLException {
        String sql = "UPDATE clientes SET email = ? WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getEmail());
            ps.setLong(2, client.getId());
            ps.executeUpdate();
        }
    }

    public boolean updateClient(Client client) throws SQLException {
        boolean actualizado;
        String sql = "UPDATE clientes SET nombre = ?, email = ? WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getEmail());
            ps.setLong(3, client.getId());
            actualizado = ps.executeUpdate() > 0;
        }
        return actualizado;
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();

        String sql = "SELECT id, nombre, email FROM clientes;";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                clients.add(new Client(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("email")
                ));
            }
        }
        return clients;
    }

    @Override
    public Map<Long, Client> getClientsMap() throws SQLException {
        List<Client> clients = findAll();
        Map<Long, Client> clientsMap = new HashMap<>();

        for (Client client : clients) {
            clientsMap.put(client.getId(), client);
        }
        return clientsMap;
    }
}
