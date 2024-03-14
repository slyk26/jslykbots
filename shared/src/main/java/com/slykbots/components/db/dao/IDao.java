package com.slykbots.components.db.dao;

import java.util.List;

@SuppressWarnings("unused")
public interface IDao<T, U> {
    default int create(T t) {
        throw new UnsupportedOperationException();
    }

    default T read(U id) {
       throw new UnsupportedOperationException();
    }

    default void update(T t) {
        throw new UnsupportedOperationException();
    }

    default T delete(int id) {
       throw new UnsupportedOperationException();
    }

    default List<T> list() {
        throw new UnsupportedOperationException();
    }
}
