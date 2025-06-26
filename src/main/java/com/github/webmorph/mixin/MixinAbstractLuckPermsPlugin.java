package com.github.webmorph.mixin;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * A Mixin that hides luckperms banner, replaces the default configuration directory used by LuckPerms with a
 * temporary directory and programmatically overrides the contents of {@code config.yml}
 * based on values defined in {@code application.properties}.
 *
 * <p>This mixin targets the standalone or embedded use case of LuckPerms where manual
 * configuration editing is undesired or not viable. By redirecting the config directory
 * to a temporary location and rewriting the configuration file at runtime, it eliminates
 * the need for human intervention during setup and deployment.</p>
 *
 * <p>Its core responsibilities are:</p>
 * <ul>
 *     <li>Redirecting LuckPerms to use a temporary config directory instead of a persistent {@code data} folder.</li>
 *     <li>Parsing the auto-generated {@code config.yml} and injecting database/storage-related configuration
 *         based on the Spring {@code application.properties} file.</li>
 *     <li>Auto-detecting the database driver (MongoDB, MySQL, PostgreSQL, MariaDB, or H2) and modifying
 *         the config accordingly.</li>
 *     <li>Avoiding the need to pre-configure {@code config.yml} manually by dynamically applying
 *         all necessary connection parameters (URL, credentials, SSL flags, etc.).</li>
 * </ul>
 *
 * <p>This mixin is especially useful in dynamic or containerized environments, where the application
 * config is managed externally (e.g., via Spring Boot) and filesystem-based static configuration
 * is discouraged.</p>
 *
 * <p><strong>Note:</strong> This mixin must be used with a valid Mixin configuration and classpath
 * setup to ensure it is injected properly during LuckPerms initialization.</p>
 *
 * @see <a href="https://github.com/LuckPerms/LuckPerms/blob/b8d1f52d7de9cb5a28404468b5facfb9ae7b6968/common/src/main/java/me/lucko/luckperms/common/plugin/AbstractLuckPermsPlugin.java#L84">
 * LuckPerms AbstractLuckPermsPlugin source</a>
 */
@Slf4j
@Mixin(targets = "me.lucko.luckperms.common.plugin.AbstractLuckPermsPlugin")
public class MixinAbstractLuckPermsPlugin {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory()).configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    private Path configPath;

    @Redirect(method = "enable", at = @At(value = "INVOKE", target = "Lme/lucko/luckperms/common/locale/Message$Args1;send(Lme/lucko/luckperms/common/sender/Sender;Ljava/lang/Object;)V"))
    public final void hideStartupBanner(Object message, Object sender, Object bootstrap) {
    }

    @SneakyThrows
    @Redirect(method = "resolveConfig", at = @At(value = "INVOKE", target = "Lme/lucko/luckperms/common/plugin/bootstrap/LuckPermsBootstrap;getConfigDirectory()Ljava/nio/file/Path;"))
    public final Path getTempDir(Object luckPermsBootstrap) {
        this.configPath = Files.createTempDirectory("luckperms_");
        log.info("Moving LuckPerms config to {}/config.yml", this.configPath.toAbsolutePath());
        return this.configPath;
    }

    @Redirect(method = "testUsernameValidity", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean redirectLenientTest(Predicate<String> predicate, Object username) {
        return username instanceof String s && !s.isEmpty();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Inject(method = "resolveConfig", at = @At(value = "TAIL", shift = At.Shift.BEFORE))
    public void overwireConfig() {
        File file = this.configPath.resolve("config.yml").toFile();
        Map<String, Object> root = this.mapper.readValue(file, new TypeReference<>() {
        });
        Map<String, Object> data = (Map<String, Object>) root.get("data");
        InputStream inputStream = Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("application.properties"));
        Properties props = new Properties();
        props.load(inputStream);
        inputStream.close();
        data.put("database", "permissions");
        if (this.hasMongo()) {
            log.info("Using MongoDB configuration from application.properties");
            data.put("mongodb-collection-prefix", "permission_");
            data.put("database", props.getProperty("spring.data.mongodb.database", "permissions"));
            root.put("storage-method", "MongoDB");
            data.put("mongodb-connection-uri", props.getProperty("spring.data.mongodb.uri", ""));
        } else if (this.hasMariaDB(props)) {
            log.info("Using MariaDB configuration from application.properties");
            root.put("storage-method", "MariaDB");
        } else if (this.hasPostgreSQL(props)) {
            log.info("Using PostgreSQL configuration from application.properties");
            root.put("storage-method", "PostgreSQL");
        } else if (this.hasMySQL(props)) {
            log.info("Using MySQL configuration from application.properties");
            root.put("storage-method", "MYSQL");
        } else {
            log.info("Using H2 configuration from application.properties");
            root.put("storage-method", "H2");
        }
        String url = props.getProperty("spring.datasource.url", "");
        String[] args = url.split("/");
        if (args.length >= 3) {
            data.put("address", args[2]);
            data.put("database", args.length >= 4 ? args[3].split("\\?")[0] : "permissions");
        }
        Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>) data.get("pool-settings"))
                .get("properties");
        boolean respectSSL = url.contains("SSL=true") ||
                props.getProperty("spring.data.mongodb.ssl.enabled", "true")
                        .equalsIgnoreCase("true");
        properties.put("useSSL", respectSSL);
        properties.put("verifyServerCertificate", respectSSL);
        if ("PostgreSQL".equals(root.get("storage-method"))) {
            properties.remove("useUnicode");
            properties.remove("characterEncoding");
        }
        data.put("username", props.getProperty("spring.datasource.username", ""));
        data.put("password", props.getProperty("spring.datasource.password", ""));
        root.put("auto-install-translations", false);
        root.put("allow-invalid-usernames", true);
        this.mapper.writeValue(file, root);
    }

    private boolean hasClass(String clazz) {
        try {
            Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    private boolean hasMongo() {
        return hasClass("org.springframework.data.mongodb.SpringDataMongoDB");
    }

    private boolean hasMariaDB(Properties props) {
        return this.hasDriver("org.mariadb.jdbc.Driver", props);
    }

    private boolean hasPostgreSQL(Properties props) {
        return this.hasDriver("org.postgresql.Driver", props);
    }

    private boolean hasMySQL(Properties props) {
        return this.hasDriver("com.mysql.jdbc.Driver", props) || this.hasDriver("com.mysql.cj.jdbc.Driver", props);
    }

    private boolean hasDriver(String driver, Properties props) {
        return hasClass(driver) && driver.equals(props.getProperty("spring.datasource.driver-class-name"));
    }
}
