package com.slykbots.components.util;

import com.slykbots.components.exceptions.MissingEnvVarsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnvLoader {

    protected static final Map<String, String> vars = System.getenv();
    private static final Logger logger = LoggerFactory.getLogger(EnvLoader.class);

    static {
        logger.debug("env vars: {}", vars);
    }


    protected EnvLoader() {
    }

    public static String getVar(String key) {
        var value = vars.getOrDefault(key, null);

        if (value == null)
            throw new MissingEnvVarsException(String.format("Invalid Env Var %s, check your variables", key));
        return value;
    }

    protected static Map<String, String> getVarsFor(String config, List<String> requiredKeys) {
        var set = vars.entrySet().stream()
                .filter(v -> v.getKey().startsWith(config)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!Helper.mapHasKeys(vars, requiredKeys)) {
            logger.error("{} is missing keys. Make sure these Environment Variables are set: {}", config, requiredKeys);
            throw new MissingEnvVarsException(String.format("Could not find all required Env variables. Needed: %s Found: %s", requiredKeys.size(), set.size()));
        }

        return set;
    }
}
