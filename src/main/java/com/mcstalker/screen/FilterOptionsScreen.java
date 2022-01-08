package com.mcstalker.screen;

import com.ibm.icu.impl.duration.impl.Utils;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.Filters;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.io.IOException;

import static com.mcstalker.networking.objects.FilterProperties.*;

public class FilterOptionsScreen extends Screen {

	private boolean hasInited;
	private Screen parent;
	private ServerDiscoveryScreen browser;

	private ButtonWidget buttonNextPage;
	private ButtonWidget buttonPreviousPage;
	private ButtonWidget buttonJoin;
	private ButtonWidget buttonDelete;
	private ButtonWidget buttonMustHavePlayersOnline;
	private ButtonWidget buttonMustBeVanilla;

	private TextFieldWidget versionName;
	private TextFieldWidget motdSearch;

	private ButtonWidget btnApply = null;

	public FilterOptionsScreen(Text title, Screen parent, ServerDiscoveryScreen browser) {
		super(title);
		this.parent = parent;
		this.browser = browser;
	}

	// stolen gui code
	@Override
	protected void init() {
		this.client.keyboard.setRepeatEvents(true);
		hasInited = false;

		btnApply = new ButtonWidget(this.width / 2 - 154, this.height - 52 - 30, 100, 20, Text.of("Apply"), (button) -> {
			page = 1;
			try {
				client.setScreen(new ServerDiscoveryScreen(this.parent, Requests.getServers().getServerList()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		addDrawableChild(btnApply);

		addDrawableChild(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, Text.of("Cancel"), (button) -> {
			this.client.setScreen(this.browser);
		}));

		this.motdSearch = new TextFieldWidget(this.textRenderer, this.width / 2 - 151 - 2, 22 + 16, 151, 20, this.motdSearch, Text.of("MOTD"));
		this.motdSearch.setText(searchText);

		this.motdSearch.setChangedListener((search) -> searchText = search);

		this.addSelectableChild(motdSearch);

		this.buttonMustHavePlayersOnline = addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 28, 152, 20, Text.of("Must have players: " + (mustHavePeople ? "§2Yes" : "§4No")), (button) -> {
			mustHavePeople = !mustHavePeople;
			button.setMessage(Text.of("Must have players: " + (mustHavePeople ? "§2Yes" : "§4No")));
		}));

		this.buttonMustBeVanilla = addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 152, 20, Text.of("Must be Vanilla: " + (vanillaOnly ? "§2Yes" : "§4No")), (button) -> {
			vanillaOnly = !vanillaOnly;
			button.setMessage(Text.of("Must be Vanilla: " + (vanillaOnly ? "§2Yes" : "§4No")));
		}));

		this.versionName = new TextFieldWidget(this.textRenderer, this.width / 2 - 151 - 2, 22 + 16 + 22, 151, 20, this.versionName, Text.of("Version"));
		this.versionName.setText(version.protocolId() != -1 ? version.name() + " (" + version.protocolId() + ")" : "All");

		super.init();
	}

	@Override
	public void tick() {
		super.tick();
		this.motdSearch.tick();
		this.versionName.tick();
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		this.motdSearch.render(matrices, mouseX, mouseY, delta);
		this.versionName.render(matrices, mouseX, mouseY, delta);
	}
}