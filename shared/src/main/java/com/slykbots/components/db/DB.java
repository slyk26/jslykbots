package com.slykbots.components.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    private static final Logger logger = LoggerFactory.getLogger(DB.class);

    private DB() {
    }

    public static Connection connect() {
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String pw = DBConfig.getPw();

        try {
            return DriverManager.getConnection(url, user, pw);
        } catch (SQLException e) {
            logger.error("Cannot get Connection to Database: {}", e.getMessage());
            return null;
        }
    }

    public static void healthcheck() {
        String where = "[healthcheck]";

        try (var c = DB.connect()) {
            if (c != null) {
                logger.info("{} Database reachable at {}", where, DBConfig.getUrl());
                logger.debug("{} Driver: {}", where, c.getClientInfo());
            } else {
                logger.error("{} Can reach Database but cannot create Connection", where);
            }
        } catch (SQLException e) {
            logger.error("{} Cannot reach Database: {}", where, e.getMessage());
        }
    }
}
