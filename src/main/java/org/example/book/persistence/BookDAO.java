package org.example.book.persistence;

import org.example.book.model.Book;
import org.example.client.model.Client;
import org.example.room.model.Room;

import java.sql.SQLException;
import java.util.List;

public interface BookDAO {
    void save(Book book) throws SQLException;
    void delete(Book book) throws SQLException;
    void updateCheckIn(Book book) throws SQLException;
    void updateCheckOut(Book book) throws SQLException;
    void updateRoom(Book book) throws SQLException;
    void updateClient(Book book) throws SQLException;
    List<Book> findAll() throws SQLException;
}
