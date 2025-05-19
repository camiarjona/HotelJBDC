package org.example.room.persistence;

import org.example.room.model.Room;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface RoomDAO {
    void save(Room room) throws SQLException;
    void delete(Room room) throws SQLException;
    void updateType(Room room) throws SQLException;
    void updatePrice(Room room) throws SQLException;
    List<Room> findAll() throws SQLException;
    Map<Long, Room> getRoomsMap() throws SQLException;
}
