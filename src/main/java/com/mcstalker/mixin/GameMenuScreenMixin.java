package com.mcstalker.mixin;

import com.mcstalker.MCStalker;
import com.mcstalker.networking.Requests;
import com.mcstalker.screen.ServerDiscoveryScreen;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.util.Clipboard;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    private ButtonWidget bookmarkServerButton;

    @Inject(at = @At("TAIL"), method = "initWidgets")
    public void initWidgets(CallbackInfo info) {
        //TODO: Rewrite this with streams?
        Element mojangButton = null;
        for (Element elem : this.children()) {
            if(!(elem instanceof ButtonWidget)) continue;

            final ButtonWidget btn = (ButtonWidget)elem;
           
            if(!(btn.getMessage() instanceof TranslatableText)) continue;
            
            final TranslatableText tt = (TranslatableText)btn.getMessage();
        	if(tt.getKey().equals("menu.disconnect") || tt.getKey().equals("menu.returnToMenu")) {
        		mojangButton = elem;
        	}
        }

        if(mojangButton != null) this.remove(mojangButton);

        Text text = this.client.isInSingleplayer() ? new TranslatableText("menu.returnToMenu") : new TranslatableText("menu.disconnect");
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, text, (button) -> {
            boolean bl = this.client.isInSingleplayer();
            boolean bl2 = this.client.isConnectedToRealms();
            button.active = false;
            this.client.world.disconnect();
            if (bl) {
                this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
            } else {
                this.client.disconnect();
            }

            TitleScreen titleScreen = new TitleScreen();
            if (bl) {
                this.client.setScreen(titleScreen);
            } else if (bl2) {
                this.client.setScreen(new RealmsMainScreen(titleScreen));
            } else {
                if (ServerDiscoveryScreen.hasBeenAccessed()) {
                    this.client.setScreen(new ServerDiscoveryScreen(titleScreen, ServerDiscoveryScreen.getFilterServerResponse()));
                    ServerDiscoveryScreen.setBeenAccessed(false);
                } else {
                    this.client.setScreen(new MultiplayerScreen(titleScreen));
                }

            }

        }));

        if(client.isInSingleplayer()) {
            return;
        }

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16 + 24, 204, 20, new LiteralText("Copy IP"), (button) -> {
            new Clipboard().setClipboard(this.client.getWindow().getHandle(), this.client.getNetworkHandler().getConnection().getAddress().toString().split("/")[1]);
        }));

        if (!ServerDiscoveryScreen.hasBeenAccessed()) {
            return;
        }

        bookmarkServerButton = new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16 + (24 * 2), 204, 20, new LiteralText("Bookmark Server"), (button) -> {
            final String ip = this.client.getNetworkHandler().getConnection().getAddress().toString().split("/")[1];

            final ServerList list = new ServerList(this.client);
            list.add(new ServerInfo("MCStalker Bookmarked Server", ip, false));
            list.saveFile();

            bookmarkServerButton.setMessage(new LiteralText("Saved!"));
            bookmarkServerButton.active = false;
        });
        this.addDrawableChild(bookmarkServerButton);
    }
}
