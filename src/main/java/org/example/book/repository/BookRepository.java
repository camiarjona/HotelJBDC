package org.example.book.repository;

import org.example.book.exception.BookException;
import org.example.book.exception.BookNotFoundException;
import org.example.book.model.Book;
import org.example.book.persistence.BookDAO;
import org.example.book.persistence.BookDAOImpl;
import org.example.client.model.Client;
import org.example.room.exception.RoomException;
import org.example.room.model.Room;
import org.example.room.repository.RoomRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookRepository {

    private final List<Book> bookings;
    private final BookDAO bookDAO;
    private final RoomRepository roomRepository;

    public BookRepository(RoomRepository roomRepository) throws SQLException {
        this.roomRepository = roomRepository;
        this.bookDAO = new BookDAOImpl();
        this.bookings = bookDAO.findAll();
    }

    public void save(Book book) throws SQLException, BookException {
        if (book != null) {
            bookings.add(book);
            bookDAO.save(book);
        } else {
            throw new BookException("Error al guardar la reserva.");
        }
    }

    public void delete(Book book) throws SQLException, BookException {
        if (book != null) {
            bookings.remove(book);
            bookDAO.delete(book);
        } else {
            throw new BookException("Error al eliminar la reserva.");
        }
    }

    public Optional<Book> findById(Long id) {
        return bookings.stream().
                filter(book -> book.getId().equals(id)).
                findFirst();
    }

    public Optional<Book> findByClientId(Long clientId) {
        return bookings.stream().
                filter(book -> book.getClient().getId().equals(clientId)).
                findFirst();
    }

    public List<Book> findAllByClientId(Long clientId) throws BookException {
        List<Book> clientBooks = bookings.stream().
                filter(book -> book.getClient().getId().equals(clientId)).
                toList();

        if (clientBooks.isEmpty()) {
            throw new BookException("El cliente no tiene reservas registradas.");
        }

        return clientBooks;
    }

    public Optional<Book> findByRoomNumber(Integer number) {
        return bookings.stream().
                filter(book -> book.getRoom().getNumber().equals(number)).
                findFirst();
    }

    //todos los roles
    public void updateCheckOut(Long bookId, LocalDate newCheckOut) throws SQLException, BookNotFoundException, BookException {
        Book book = findById(bookId).
                orElseThrow(() -> new BookNotFoundException("Reserva no encontrada."));

        if (newCheckOut.isBefore(book.getCheckIn())) {
            throw new BookException("La fecha de salida no puede ser anterior a la fecha de entrada.");
        }

        book.setCheckOut(newCheckOut);
        bookDAO.updateCheckOut(book);
    }

    //admin y recepcionista
    public void updateCheckIn(Long bookId, LocalDate newCheckIn) throws SQLException, BookNotFoundException, BookException {
        Book book = findById(bookId).
                orElseThrow(() -> new BookNotFoundException("Reserva no encontrada."));

        if (newCheckIn.isAfter(book.getCheckOut())) {
            throw new BookException("La fecha de entrada no puede ser posterior a la de salida.");
        }

        if (!roomRepository.isRoomAvailable(book.getRoom(), newCheckIn, book.getCheckOut(), bookings, book.getId())) {
            throw new BookException("La habitacion no se encuentra disponible para la fecha ingresada.");
        }

        book.setCheckIn(newCheckIn);
        bookDAO.updateCheckIn(book);
    }

    public void updateClient(Long bookId, Client newClient) throws SQLException, BookNotFoundException, BookException {
        Book book = findById(bookId).
                orElseThrow(() -> new BookNotFoundException("Reserva no encontrada."));
        book.setClient(newClient);
        bookDAO.updateClient(book);
    }

    //solo admin
    public void updateRoom(Long bookId, Room newRoom) throws SQLException, BookNotFoundException, BookException {
        Book book = findById(bookId).
                orElseThrow(() -> new BookNotFoundException("Reserva no encontrada."));

        if (!roomRepository.isRoomAvailable(newRoom, book.getCheckIn(), book.getCheckOut(), this.bookings, book.getId())) {
            throw new BookException("La habitación no está disponible para las fechas de la reserva.");
        }

        book.setRoom(newRoom);
        bookDAO.updateRoom(book);
    }

    public Double getTotalRevenue(LocalDate from, LocalDate to) {
        return bookings.stream().
                filter(book -> !book.getCheckIn().isBefore(from) && !book.getCheckOut().isAfter(to)).
                map(book -> book.getRoom().getPrice()).
                reduce(0.0, Double::sum);

    }

    public Double getMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);

        return bookings.stream()
                .filter(book -> YearMonth.from(book.getCheckIn()).equals(currentMonth))
                .map(book -> book.getRoom().getPrice())
                .reduce(0.0, Double::sum);
    }

    public Double getAverageOccupancy(LocalDate startDate, LocalDate endDate) throws RoomException {
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate.plusDays(1));
        long totalRooms = roomRepository.findAll().size();// todas las habitaciones del hotel

        if (totalDays <= 0 || totalRooms == 0) {
            return 0.0;
        }

        // Para cada día del rango, contamos cuántas habitaciones están ocupadas
        long totalOccupiedDays = startDate.datesUntil(endDate.plusDays(1))
                .mapToLong(date -> bookings.stream()
                        .filter(book -> !(book.getCheckOut().isBefore(date) || book.getCheckIn().isAfter(date)))
                        .count())
                .sum();

        // Cálculo del porcentaje de ocupación promedio
        double totalPossibleOccupancy = totalRooms * totalDays;

        return (totalOccupiedDays / totalPossibleOccupancy) * 100;
    }

    public Map<String, Long> getReservationCountByType(){
        return bookings.stream()
                .collect(Collectors.groupingBy(book -> book.getRoom().getType().name(), Collectors.counting()
                ));
    }

    // Método 9: Obtener la habitación más reservada
    public Optional<Room> getMostBookedRoom() {

        return bookings.stream()
                .collect(Collectors.groupingBy(Book::getRoom, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    // Método 10: Listar los clientes ordenados por la cantidad de reservas (desc)
    public List<Client> getClientsOrderedByBookings() {

        return bookings.stream()
                .collect(Collectors.groupingBy(Book::getClient, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


}
