package org.example.client.repository;

import org.example.client.exception.ClientException;
import org.example.client.exception.ClientNotFoundException;
import org.example.client.model.Client;
import org.example.client.persistence.ClientDAO;
import org.example.client.persistence.ClientDAOImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ClientRepository {

    private final ClientDAO clientDAO;
    private final List<Client> clients;

    public ClientRepository() throws SQLException {
        this.clientDAO = new ClientDAOImpl();
        this.clients = clientDAO.findAll();
    }

    public void save(Client client) throws SQLException, ClientException {
        if (!clients.contains(client) && client != null) {
            clients.add(client);
            clientDAO.save(client);
        } else {
            throw new ClientException("El cliente ya se encuentra registrado");
        }
    }

    public List<Client> findAll() throws ClientException {
        if (clients.isEmpty()) {
            throw new ClientException("La lista de clientes se encuentra vacía.");
        }
        return clients;
    }

    public Optional<Client> findById(Long id) {
        return clients.stream().filter(client -> client.getId() == id).findFirst();
    }

    public void delete(Long id) throws ClientNotFoundException, SQLException {
        Optional<Client> client = findById(id);
        if (client.isPresent()) {
            clients.remove(client.get());
            clientDAO.delete(client.get());
        } else {
            throw new  ClientNotFoundException("Cliente no encontrado");
        }
    }

}
