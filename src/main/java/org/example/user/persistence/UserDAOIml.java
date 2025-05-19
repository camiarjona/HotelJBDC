package org.example.user.persistence;

import org.example.connection.DatabaseConnection;
import org.example.user.model.Rol;
import org.example.user.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOIml implements UserDAO {

    private final Connection connection;

    public UserDAOIml() throws SQLException {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, email, rol) VALUES (?, ?, ?);";

        try (PreparedStatement ps = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRol().toString());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void delete(User user) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateName(User user) throws SQLException {
        String sql = "UPDATE usuarios SET nombre = ? WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateEmail(User user) throws SQLException {
        String sql = "UPDATE usuarios SET email = ? WHERE id = ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateRol(User user) throws SQLException {
        String sql = "UPDATE usuarios SET rol = ? WHERE id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getRol().toString());
            ps.setLong(2, user.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT id, nombre, email, rol FROM usuarios;";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getLong("id"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        Rol.valueOf(rs.getString("rol"))
                ));
            }
        }
        return users;
    }

}
