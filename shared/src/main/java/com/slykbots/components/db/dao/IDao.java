package com.slykbots.components.db.dao;

import java.util.List;

@SuppressWarnings("unused")
public interface IDao<T> {
    int create(T t);

    default T read(int id) {
        return null;
    }

    void update(T t);

    default T delete(int id) {
        return null;
    }

    List<T> list();
}
