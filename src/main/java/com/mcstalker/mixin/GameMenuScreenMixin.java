package com.mcstalker.mixin;

import com.mcstalker.screen.BookmarkServerScreen;
import com.mcstalker.screen.ServerDiscoveryScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.IntStream;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
	protected GameMenuScreenMixin(Text title) {
		super(title);
	}

	private ButtonWidget bookmarkServerButton;

	@Inject(at = @At("TAIL"), method = "initWidgets")
	public void initWidgets(CallbackInfo info) {
		new ArrayList<>(this.children()).stream()
				.filter(Objects::nonNull)
				.filter(widget -> widget instanceof ButtonWidget)
				.filter(wid -> ((ButtonWidget) wid).getMessage() instanceof TranslatableText)
				.filter(wid -> {
					final String key = ((TranslatableText) ((ButtonWidget) wid).getMessage()).getKey();
					return key.equals("menu.disconnect") || key.equals("menu.returnToMenu");
				}).forEach(this::remove);

		final Text text = new TranslatableText(this.client.isInSingleplayer() ? "menu.returnToMenu" : "menu.disconnect");
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16, 204, 20, text, (button) -> {
			final boolean inSingleplayer = this.client.isInSingleplayer();
			button.active = false;

			this.client.world.disconnect();
			if (inSingleplayer) {
				this.client.disconnect(new SaveLevelScreen(new TranslatableText("menu.savingLevel")));
			} else {
				this.client.disconnect();
			}

			TitleScreen titleScreen = new TitleScreen();
			if (inSingleplayer) {
				this.client.setScreen(titleScreen);
			} else if (this.client.isConnectedToRealms()) {
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

		if (client.isInSingleplayer()) {
			return;
		}

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16 + 24, 204, 20, new LiteralText("Copy IP"), (button) -> {
			new Clipboard().setClipboard(this.client.getWindow().getHandle(), this.client.getNetworkHandler().getConnection().getAddress().toString().split("/")[1]);
			button.setMessage(Text.of("Copied!"));
			button.active = false;
		}));

		if (!ServerDiscoveryScreen.hasBeenAccessed()) {
			return;
		}

		bookmarkServerButton = new ButtonWidget(this.width / 2 - 102, this.height / 4 + 120 + -16 + (24 * 2), 204, 20, new LiteralText("Bookmark Server"), (button) -> {
			final String ip = this.client.getNetworkHandler().getConnection().getAddress().toString().split("/")[1];
			this.client.setScreen(new BookmarkServerScreen(this, ip));
		});
		ServerList list = new ServerList(this.client);
		list.loadFile();
		if (IntStream
			.range(0, list.size())
			.anyMatch(i ->
				list.get(i).address.equals(
					this.client.getNetworkHandler().getConnection().getAddress().toString().split("/")[1]
				)
			)
		) {
			bookmarkServerButton.active = false;
			bookmarkServerButton.setMessage(Text.of("Bookmark Server (already Bookmarked)"));
		}
		this.addDrawableChild(bookmarkServerButton);
	}
}