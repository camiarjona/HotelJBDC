package org.example.room.model;

import java.util.Objects;

public class Room {

    private Long id;
    private Integer number;
    private Type type;
    private Double price;

    public Room(Integer number, Type type, Double price) {
        this.number = number;
        this.type = type;
        this.price = price;
    }

    public Room(Long id, Integer number, Type type, Double price) {
        this.id = id;
        this.number = number;
        this.type = type;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(number, room.number);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }

    @Override
    public String toString() {
        return "Room{" +
                "number=" + number +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
}
