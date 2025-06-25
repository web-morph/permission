package com.github.webmorph.mixin;

import com.github.webmorph.permission.api.LuckPermsApiProvider;
import me.lucko.luckperms.standalone.app.LuckPermsApplication;
import me.lucko.luckperms.standalone.loader.StandaloneLoader;
import net.luckperms.api.LuckPerms;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * A Mixin that modifies the behavior of the {@link me.lucko.luckperms.standalone.loader.StandaloneLoader}
 * to disable the interactive terminal and expose the internal {@link LuckPermsApplication} instance
 * for external access to the LuckPerms API.
 *
 * <p>In the standalone version of LuckPerms, the {@code start()} method launches an interactive
 * terminal UI, which blocks the main thread and prevents further application logic (such as a
 * Spring Boot application) from continuing to run.</p>
 *
 * <p>This mixin redirects the call to {@code LuckPermsApplication.start(String[] args)} to a no-op,
 * effectively disabling the terminal. This allows the Spring-based application to retain full control
 * over the application lifecycle without being blocked by LuckPerms' terminal input loop.</p>
 *
 * <p>Additionally, it implements {@link LuckPermsApiProvider}
 * and exposes the internal {@code LuckPermsApplication} instance via {@code getLuckPerms()},
 * making the LuckPerms API accessible to the rest of the application context.</p>
 *
 * <p>This is particularly useful when LuckPerms is embedded as a permission engine within a
 * non-Minecraft Java application and needs to be fully integrated with Spring or other frameworks.</p>
 */
@Mixin(StandaloneLoader.class)
public class MixinStandaloneLoader implements LuckPermsApiProvider {
    @Shadow
    private LuckPermsApplication app;

    @Redirect(method = {"start"}, at = @At(value = "INVOKE", target = "Lme/lucko/luckperms/standalone/app/LuckPermsApplication;start([Ljava/lang/String;)V"))
    public void start(LuckPermsApplication luckPermsApplication, String[] args) {
    }

    @Override
    public LuckPerms getLuckPerms() {
        return this.app.getApi();
    }
}