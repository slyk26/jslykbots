package com.slykbots.components.db.service;

import java.util.List;

public interface IDao<T> {
    int create(T t);
    T read(int id);
    void update(T t);
    T delete(int id);
    List<T> list();
}
