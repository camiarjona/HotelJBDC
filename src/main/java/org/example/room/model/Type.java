package org.example.room.model;

public enum Type {
    SIMPLE(1),
    DOUBLE(2),
    SUITE(3);

    private final int id;

    Type(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static Type fromId(int id) {
        for (Type type : values()) {
            if (type.getId() == id) return type;
        }
        throw new IllegalArgumentException("Tipo inválido: " + id);
    }

}
