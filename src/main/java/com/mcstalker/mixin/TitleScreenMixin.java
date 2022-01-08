package com.mcstalker.mixin;

import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.MCStalkerServerList;
import com.mcstalker.screen.ServerDiscoveryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgetsNormal")
    private void addCustomButton(int y, int spacingY, CallbackInfo ci) {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 72 + 12 + spacingY, 200, 20, Text.of("MC Stalker"), (buttonWidget) -> {
            try {
                this.client.setScreen(new ServerDiscoveryScreen(this, Requests.getServers().getServerList()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }
}