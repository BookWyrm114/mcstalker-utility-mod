package com.mcstalker.mixin;

import com.mcstalker.api.API;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends Screen {
	protected DisconnectedScreenMixin(Text title) { super(title); }

	@Inject(method = "init", at = @At("RETURN"))
	public void addBanEvadeButton(CallbackInfo ci) {
		if (API.getBanEvadeButton().shouldRenderDefaultButton())
			this.addDrawableChild(new ButtonWidget(5, 5, 100, 20, Text.of("Ban Evade"), API.getBanEvadeButton()::onClick));
		else
			// ez outplayed java
			API.getBanEvadeButton().addCustomButton((DisconnectedScreen) (Object) this);
	}
}