package com.slykbots.components.db;

import com.slykbots.components.util.EnvLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DBConfig extends EnvLoader {

    private static final Logger logger = LoggerFactory.getLogger(DBConfig.class);

    private static final String CONFIG = "POSTGRES";
    private static final String USER = "POSTGRES_USER";
    private static final String PW = "POSTGRES_PASSWORD";
    private static final String HOST = "POSTGRES_HOST";
    private static final String PORT = "POSTGRES_PORT";
    private static final String URL = "POSTGRES_URL";

    private static final List<String> requiredKeys = List.of(
            USER,
            PW,
            HOST,
            PORT,
            URL
    );

    protected static final Map<String, String> dbVars = getVarsFor(CONFIG, requiredKeys);

    static {
        logger.debug("db env vars: {}", dbVars);
    }

    public static String getUser() {
        return dbVars.get(USER);
    }

    public static String getPw() {
        return dbVars.get(PW);
    }

    public static String getUrl() {
        return dbVars.get(URL);
    }
}
