package akssu.mcstalker.mixin;

import akssu.mcstalker.screen.ServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
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

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 48 + 72 + 12 + spacingY, 200, 20, Text.of("MC Stalker"), (buttonWidget) -> {
            //MinecraftClient.getInstance().setScreen(new SelectWorldScreen(this));
            Screen screen = this.client.options.skipMultiplayerWarning ? new ServerScreen(this) : new MultiplayerWarningScreen(this);
            this.client.setScreen((Screen)screen);
            //Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            //this.client.setScreen((Screen)screen);


        }));

        //MultiplayerScreen
        //SelectWorldScreen
    }



}
