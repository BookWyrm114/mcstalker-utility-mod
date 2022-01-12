package com.mcstalker.mixin;

import com.mcstalker.MCStalker;
import com.mcstalker.screen.InvalidHWIDScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	public void inject(MinecraftClient instance, Screen screen) {
		if (!MCStalker.VALID_HWID) {
			instance.setScreen(new InvalidHWIDScreen());
		} else {
			instance.setScreen(screen);
		}
	}
}