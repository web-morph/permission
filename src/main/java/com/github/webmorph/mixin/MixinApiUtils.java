package com.github.webmorph.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(targets = "me.lucko.luckperms.common.api.ApiUtils")
public class MixinApiUtils {
    @Redirect(method = "checkUsername", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
    private static boolean redirectLenientTest(Predicate<String> predicate, String username) {
        return !username.isEmpty();
    }
}
