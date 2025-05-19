package org.example.book.persistence;

import org.example.book.model.Book;
import org.example.client.model.Client;
import org.example.client.persistence.ClientDAO;
import org.example.client.persistence.ClientDAOImpl;
import org.example.connection.DatabaseConnection;
import org.example.room.model.Room;
import org.example.room.persistence.RoomDAO;
import org.example.room.persistence.RoomDAOImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookDAOImpl implements BookDAO {

    private final Connection connection;

    public BookDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Book book) throws SQLException {
        String sql = "INSERT INTO reserva(cliente_id, habitacion_id, fecha_entrada, fecha_salida) VALUES (?,?,?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, book.getClient().getId());
            ps.setLong(2, book.getRoom().getId());
            ps.setDate(3, Date.valueOf(book.getCheckIn()));
            ps.setDate(4, Date.valueOf(book.getCheckOut()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    book.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void delete(Book book) throws SQLException {
        String sql = "DELETE FROM reserva WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, book.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateCheckIn(Book book) throws SQLException {
        String sql = "UPDATE reserva SET fecha_entrada = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(book.getCheckIn()));
            ps.setLong(2, book.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateCheckOut(Book book) throws SQLException {
        String sql = "UPDATE reserva SET fecha_salida = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(book.getCheckOut()));
            ps.setLong(2, book.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateRoom(Book book) throws SQLException {
        String sql = "UPDATE reserva SET habitacion_id = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, book.getRoom().getId());
            ps.setLong(2, book.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateClient(Book book) throws SQLException {
        String sql = "UPDATE reserva SET cliente_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, book.getClient().getId());
            ps.setLong(2, book.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        RoomDAO roomDAO = new RoomDAOImpl();
        ClientDAO clientDAO = new ClientDAOImpl();
        Map<Long, Room> rooms = roomDAO.getRoomsMap();
        Map<Long, Client> clients = clientDAO.getClientsMap();

        String sql = "SELECT * FROM reserva";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();

                book.setId(rs.getLong("id"));
                book.setCheckIn(rs.getDate("fecha_entrada").toLocalDate());
                book.setCheckOut(rs.getDate("fecha_salida").toLocalDate());
                book.setRoom(rooms.get(rs.getLong("habitacion_id")));
                book.setClient(clients.get(rs.getLong("cliente_id")));

                books.add(book);
            }
        }
        return books;
    }
}
