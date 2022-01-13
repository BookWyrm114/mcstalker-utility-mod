package com.mcstalker.mixin;

import com.mcstalker.MCStalker;
import com.mcstalker.networking.Requests;
import com.mcstalker.screen.ServerDiscoveryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("RETURN"), method = "initWidgetsNormal")
	private void addCustomButton(int y, int spacingY, CallbackInfo ci) {
		ButtonWidget mcStalkerButton = new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 72 + 12 + spacingY, 200, 20, Text.of("MCStalker"), (buttonWidget) -> {
			buttonWidget.active = false;
			buttonWidget.setMessage(Text.of("MCStalker | Loading..."));
			Requests.getServers(res -> {
				if (res != null) {
					if (!res.isRatelimited()) {
						MCStalker.toExecute.offer(() -> {
							this.client.setScreen(new ServerDiscoveryScreen(this, res));
						});
					} else {
						buttonWidget.active = false;
						buttonWidget.setMessage(Text.of("Ratelimited!"));
					}
				}
			});
		});

		this.addDrawableChild(mcStalkerButton);
	}
}