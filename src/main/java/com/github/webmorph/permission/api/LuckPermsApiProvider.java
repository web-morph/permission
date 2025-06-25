package com.github.webmorph.permission.api;

import net.luckperms.api.LuckPerms;

/**
 * Provides access to the LuckPerms API instance from embedded or standalone environments.
 *
 * <p>This interface is intended to be implemented by components that control or wrap the
 * initialization of {@link net.luckperms.api.LuckPerms}, such as when LuckPerms is embedded
 * inside a Spring Boot or other non-Minecraft Java application.</p>
 *
 * <p>The primary use case is to expose the LuckPerms API to the rest of the application
 * context without requiring direct access to the internal {@code LuckPermsApplication}
 * or its loader classes. For example, a Spring bean can depend on this interface to
 * retrieve the API instance without tightly coupling to the implementation.</p>
 *
 * <p>Typical implementations include mixins or wrappers around
 * {@link me.lucko.luckperms.standalone.loader.StandaloneLoader}.</p>
 */
public interface LuckPermsApiProvider {
    LuckPerms getLuckPerms();
}
