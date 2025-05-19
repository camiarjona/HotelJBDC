package org.example.room.persistence;

import org.example.connection.DatabaseConnection;
import org.example.room.model.Room;
import org.example.room.model.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDAOImpl implements RoomDAO {

    private final Connection connection;

    public RoomDAOImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Room room) throws SQLException {
        String sql = "INSERT INTO habitacion(numero, tipo, precio) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, room.getNumber());
            ps.setString(2, room.getType().toString());
            ps.setDouble(3, room.getPrice());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    room.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void delete(Room room) throws SQLException {
        String sql = "DELETE FROM habitacion WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, room.getId());
            ps.executeUpdate();
        }

    }

    @Override
    public void updateType(Room room) throws SQLException {
        String sql = "UPDATE habitacion SET tipo = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, room.getType().toString());
            ps.setLong(2, room.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updatePrice(Room room) throws SQLException {
        String sql = "UPDATE habitacion SET precio = ? WHERE numero = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, room.getPrice());
            ps.setInt(2, room.getNumber());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT id, numero, tipo, precio FROM habitacion";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rooms.add(new Room(
                        rs.getLong("id"),
                        rs.getInt("numero"),
                        Type.valueOf(rs.getString("tipo")),
                        rs.getDouble("precio")
                ));
            }
        }
        return rooms;
    }

    @Override
    public Map<Long, Room> getRoomsMap() throws SQLException {
        List<Room> rooms = findAll();
        Map<Long, Room> roomsMap = new HashMap<>();

        for (Room room : rooms) {
            roomsMap.put(room.getId(), room);
        }
        return roomsMap;
    }
}
