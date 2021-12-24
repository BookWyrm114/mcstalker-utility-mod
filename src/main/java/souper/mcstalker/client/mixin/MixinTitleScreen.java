package souper.mcstalker.client.mixin;

import souper.mcstalker.client.GuiMcstalkerServerBrowser;
import souper.mcstalker.client.api.MCStalkerAPIWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen
{
    public MixinTitleScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = false)
    private void init(CallbackInfo ci)
    {
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 78 + 72 + 12, 200, 20, new LiteralText("MCStalker"), (button) ->
        {
            client.setScreen(new GuiMcstalkerServerBrowser(null, MCStalkerAPIWrapper.refresh()));
        }));
    }
}
