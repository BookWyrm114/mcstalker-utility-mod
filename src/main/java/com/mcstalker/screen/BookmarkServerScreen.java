package com.mcstalker.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;

@SuppressWarnings("ConstantConditions")
public class BookmarkServerScreen extends Screen {

	private final Screen parent;
	private final String ip;

	public BookmarkServerScreen(Screen parent, String ip) {
		super(Text.of("Bookmark Server"));
		this.parent = parent;
		this.ip = ip;
	}

	private TextFieldWidget tf;

	@Override
	protected void init() {
		tf = this.addDrawableChild(new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 50, 200, 20, Text.of("MCStalker Bookmarked Server")));
		tf.setText("MCStalker Bookmarked Server");

		this.addDrawableChild(new ButtonWidget(this.width / 2 - 75, this.height / 4 + 96, 150, 20, Text.of("Bookmark"), (buttonWidget) -> {
			final ServerList list = new ServerList(this.client);
			list.add(new ServerInfo(tf.getText(), ip, false));
			list.saveFile();
			this.client.setScreen(this.parent);
		}));
	}

	@Override
	public void tick() {
		tf.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (tf.keyPressed(keyCode, scanCode, modifiers)) return true;
		if (keyCode == 256 && shouldCloseOnEsc()) {
			this.client.setScreen(this.parent);
			return true;
		}
		return false;
	}
}