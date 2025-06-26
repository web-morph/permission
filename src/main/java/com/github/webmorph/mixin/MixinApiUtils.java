package com.github.webmorph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "me.lucko.luckperms.common.api.ApiUtils")
public class MixinApiUtils {
    @Overwrite
    public static String checkUsername(String s, Object plugin) {
        return s;

    }
}
