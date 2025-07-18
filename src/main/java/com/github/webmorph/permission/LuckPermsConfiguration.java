package com.github.webmorph.permission;

import com.github.webmorph.permission.api.LuckPermsApiProvider;
import me.lucko.luckperms.standalone.loader.StandaloneLoader;
import net.luckperms.api.LuckPerms;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * Spring configuration class responsible for bootstrapping LuckPerms in a standalone context
 * and exposing its API as a Spring-managed bean.
 *
 * <p>This configuration initializes {@link me.lucko.luckperms.standalone.loader.StandaloneLoader},
 * which is the entry point for the standalone version of LuckPerms. The {@code start()} method
 * is invoked with an empty argument list to trigger internal setup.</p>
 *
 * <p>However, due to the use of a Mixin (e.g., {@code MixinStandaloneLoader}), the terminal-based
 * input loop is disabled, allowing the LuckPerms instance to initialize without blocking
 * the main thread. This makes it safe and compatible for use inside Spring Boot or other
 * managed environments.</p>
 *
 * <p>The loader is cast to {@link LuckPermsApiProvider},
 * which is expected to be implemented via mixin injection. This exposes the {@link net.luckperms.api.LuckPerms}
 * API instance for dependency injection throughout the application.</p>
 *
 * <p>This bean allows other components to inject and interact with LuckPerms API in a standard,
 * decoupled Spring way.</p>
 */
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
@ComponentScan("com.github.webmorph.permission")
public class LuckPermsConfiguration {
    @Bean
    public LuckPerms getLuckPerms(EnvironmentProvider provider) {
        Objects.requireNonNull(provider);
        StandaloneLoader loader = new StandaloneLoader();
        loader.start(new String[0]);
        return ((LuckPermsApiProvider) loader).getLuckPerms();
    }
}
