package com.slykbots.components.db.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Dao<T> implements IDao<T> {
    protected static final Logger logger = LoggerFactory.getLogger(Dao.class);

    protected abstract T mapSingleRs(ResultSet rs) throws SQLException;

    protected List<T> mapRs(ResultSet rs) throws SQLException {
        List<T> l = new ArrayList<>();

        while(rs.next()){
            l.add(this.mapSingleRs(rs));
        }

        return l;
    }
}
