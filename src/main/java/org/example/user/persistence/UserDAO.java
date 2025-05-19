package org.example.user.persistence;

import org.example.user.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface UserDAO {
    void save(User user) throws SQLException;
    void delete(User user) throws SQLException;
    void updateName(User user) throws SQLException;
    void updateEmail(User user) throws SQLException;
    void updateRol(User user) throws SQLException;
    List<User> findAll() throws SQLException;

}
