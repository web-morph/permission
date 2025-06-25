package com.github.webmorph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A Mixin that overrides the default data directory used by standalone LuckPerms,
 * redirecting it from the {@code data} folder to a custom {@code permissions} folder.
 *
 * <p>By default, {@code LPStandaloneBootstrap#getDataDirectory()} returns a path to
 * the {@code data} directory where LuckPerms stores all configuration and data files
 * (users, groups, tracks, etc.).</p>
 *
 * <p>This mixin overwrites that behavior, forcing LuckPerms to use the {@code permissions}
 * directory instead. This is useful for projects that want to logically separate
 * permission-related data from other application data, or to match a specific folder
 * structure or deployment strategy.</p>
 *
 * <p>Note: this applies only to the standalone version of LuckPerms and requires
 * proper Mixin setup to take effect.</p>
 *
 * @see <a href="https://github.com/LuckPerms/LuckPerms/blob/b8d1f52d7de9cb5a28404468b5facfb9ae7b6968/standalone/src/main/java/me/lucko/luckperms/standalone/LPStandaloneBootstrap.java#L50">LPStandaloneBootstrap source</a>
 */

@Mixin(targets = "me.lucko.luckperms.standalone.LPStandaloneBootstrap")
public class MixinLPStandaloneBootstrap {
    @Overwrite
    public Path getDataDirectory() {
        return Paths.get("permissions").toAbsolutePath();
    }
}
