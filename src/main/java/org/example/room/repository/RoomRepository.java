package org.example.room.repository;

import org.example.book.model.Book;
import org.example.room.exception.RoomException;
import org.example.room.exception.RoomNotFoundException;
import org.example.room.model.Room;
import org.example.room.model.Type;
import org.example.room.persistence.RoomDAO;
import org.example.room.persistence.RoomDAOImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoomRepository {

    private final RoomDAO roomDAO;
    private final List<Room> rooms;

    public RoomRepository() throws SQLException {
        this.roomDAO = new RoomDAOImpl();
        this.rooms = roomDAO.findAll();
    }

    public void save(Room room) throws SQLException, RoomException {
        if (!rooms.contains(room) && room != null) {
            rooms.add(room);
            roomDAO.save(room);
        } else {
            throw new RoomException("La habitación ya se encuentra cargada en el sistema.");
        }
    }

    public List<Room> findAll() throws RoomException {
        if (rooms.isEmpty()) {
            throw new RoomException("La lista de habitaciones se encuentra vacía.");
        }
        return rooms;
    }

    public Optional<Room> findByNumber(Integer number) {
        return rooms.stream().
                filter(room -> room.getNumber().equals(number)).
                findFirst();
    }

    public void delete(Integer number) throws SQLException, RoomNotFoundException {
        Optional<Room> room = findByNumber(number);
        if (room.isPresent()) {
            roomDAO.delete(room.get());
            rooms.remove(room.get());
        } else {
            throw new RoomNotFoundException("Habitación no encontrada.");
        }
    }

    public void updateType(int type_id, Integer number) throws SQLException, RoomNotFoundException {
        Optional<Room> roomOptional = findByNumber(number);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setType(Type.fromId(type_id));
            roomDAO.updateType(room);
        } else {
            throw new RoomNotFoundException("Habitación no encontrada.");
        }
    }

    public void updatePrice(Double price, Integer number) throws SQLException, RoomNotFoundException {
        Optional<Room> roomOptional = findByNumber(number);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            room.setPrice(price);
            roomDAO.updatePrice(room);
        } else {
            throw new RoomNotFoundException("Habitación no encontrada.");
        }
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, Type type, List<Book> bookings){
        return rooms.stream().
                filter(room -> room.getType() == type).
                filter(room -> isRoomAvailable(room, checkIn, checkOut, bookings)).
                collect(Collectors.toList());
    }

    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut, List<Book> bookings) {
        return bookings.stream().
                filter(book -> book.getRoom().equals(room)).
                allMatch(book ->
                        book.getCheckOut().isBefore(checkIn) || book.getCheckIn().isAfter(checkOut));
    }

    // Para operaciones de actualización (ignora una reserva específica)
    public boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut, List<Book> bookings, Long ignoreBookId) {
        return bookings.stream()
                .filter(book -> book.getRoom().equals(room))
                .filter(book -> !book.getId().equals(ignoreBookId)) // <-- ignora esa reserva
                .allMatch(book ->
                        book.getCheckOut().isBefore(checkIn) || book.getCheckIn().isAfter(checkOut));
    }

}
