package com.mcstalker.screen;

import baritone.utils.GuiClick;
import com.mcstalker.MCStalker;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Clipboard;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InvalidHWIDScreen extends Screen {

	public InvalidHWIDScreen(int statusCode) {
		super(Text.of("Invalid HWID!"));
		if (statusCode == 429)
			this.ROWS.add(Text.of("Ratelimited. Please try again in a minute!"));
		else {
			this.ROWS.add(Text.of("Your HWID is unauthorised, please make a ticket."));
			this.ROWS.add(Text.of("in our help channel using \"t!new HWID Authorisation\""));
			this.ROWS.add(Text.of(""));
			this.ROWS.add(Text.of("Status Code: " + statusCode));
		}
	}

	@Override
	protected void init() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 60, this.height / 2 + getHeight() / 2 - 20, 120, 20, Text.of("Copy HWID"), button -> {
			button.setMessage(Text.of("Copied!"));
			new Clipboard().setClipboard(this.client.getWindow().getHandle(), MCStalker.HWID);
		}));
	}

	private final List<Text> ROWS = new ArrayList<>();

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		int height = getHeight();
		for (int i = 0; i < this.ROWS.size(); i++) {
			Text text = this.ROWS.get(i);
			this.client.textRenderer.draw(matrices, text, this.width / 2F - this.client.textRenderer.getWidth(text) / 2F, this.height / 2F - height / 2F + i * (this.client.textRenderer.fontHeight + 2), Color.WHITE.getRGB());
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	private int getHeight() {
		return 30 + this.ROWS.size() * this.client.textRenderer.fontHeight + 2 * (this.ROWS.size() - 1);
	}

	@Override
	public void onClose() {}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {return false;}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {return false;}
}