package com.github.webmorph.permission;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentProvider {
    private static Environment INSTANCE;

    public EnvironmentProvider(Environment environment) {
        INSTANCE = environment;
    }

    public static Environment getInstance() {
        return INSTANCE;
    }
}
