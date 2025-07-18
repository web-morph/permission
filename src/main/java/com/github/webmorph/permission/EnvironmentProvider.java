package com.github.webmorph.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnvironmentProvider {
    private static Environment INSTANCE;

    public EnvironmentProvider(Environment environment) {
        INSTANCE = environment;
    }

    public static Environment getInstance() {
        return INSTANCE;
    }
}
