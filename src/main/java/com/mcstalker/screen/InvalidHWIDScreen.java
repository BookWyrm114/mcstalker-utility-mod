package com.mcstalker.screen;

import com.mcstalker.MCStalker;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class InvalidHWIDScreen extends Screen {
	public InvalidHWIDScreen() {
		super(Text.of("Invalid HWID!"));
	}

	@Override
	protected void init() {
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 60, this.height / 2 + 20, 120, 20, Text.of("Copy HWID"), button -> {
			button.setMessage(Text.of("Copied!"));
			// System.setProperty("java.awt.headless", "false");
			StringSelection selection = new StringSelection(MCStalker.HWID);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
		}));
	}

	private static final Text INVALID_HWID_TEXT = Text.of("Your HWID is unauthorised, please make a ticket in our help channel using t!new HWID Authorisation.");

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		client.textRenderer.draw(matrices, INVALID_HWID_TEXT, this.width / 2F - client.textRenderer.getWidth(INVALID_HWID_TEXT) / 2F, this.height / 2F - textRenderer.fontHeight, Color.RED.getRGB());
		super.render(matrices, mouseX, mouseY, delta);
	}
}