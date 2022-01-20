package com.mcstalker.screen;

import com.google.common.collect.Lists;
import com.mcstalker.MCStalker;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.FilterProperties;
import com.mcstalker.networking.objects.FilterServerResponse;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

import static com.mcstalker.networking.objects.FilterProperties.getInstance;

@Environment(EnvType.CLIENT)
public class ServerDiscoveryScreen extends MultiplayerScreen {
	private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
	private final Screen parent;
	protected MultiplayerServerListWidget serverListWidget;
	private static MultiplayerServerListWidget lastWidget;
	private final ServerList serverList;

	private static FilterServerResponse filterServerResponse;
	private static boolean wasAccessed;

	private ButtonWidget buttonJoin;
	private ButtonWidget buttonPrevious;
	private ButtonWidget buttonNext;

	private List<Text> tooltipText;
	private boolean hasInited;

	private final List<ButtonWidget> drawablesBypass = Lists.newArrayList();

	public ServerDiscoveryScreen(Screen parent, FilterServerResponse response) {
		super(parent);
		this.parent = parent;
		this.serverList = response.getServerList();
		filterServerResponse = response;
		this.hasInited = false;
	}

	@Override
	protected void init() {
		assert this.client != null;

		this.client.keyboard.setRepeatEvents(true);
		if (this.hasInited) {
			this.serverListWidget.updateSize(width, height, 32, this.height - 64);
		} else {
			hasInited = true;
			lastWidget = serverListWidget = new MultiplayerServerListWidget(this, client, width, height, 32, height - 64, 36);

			serverListWidget.setServers(this.serverList);
		}

		drawablesBypass.clear();

		addSelectableChild(serverListWidget);
		buttonJoin = (new ButtonWidget(width / 2 - 154, height - 28, 100, 20, new TranslatableText("selectServer.select"), (button) -> this.connect()));

		drawablesBypass.add(buttonJoin);

		drawablesBypass.add(this.buttonNext = new ButtonWidget(width / 2 + 14, height - 28, 60, 20, new LiteralText("Next"), (button) -> {
			if (filterServerResponse.remainingPages > 0) {
				getInstance().page++;
			}

			Requests.getServers(res -> MCStalker.toExecute.offer(() -> {
				if (client.currentScreen instanceof ServerDiscoveryScreen)
					client.setScreen(new ServerDiscoveryScreen(this.parent, res));
			}));
		}));

		drawablesBypass.add(this.buttonPrevious = new ButtonWidget(width / 2 + 4 - 55, height - 28, 60, 20, new LiteralText("Previous"), (button) -> {
			getInstance().page = Math.max(getInstance().page - 1, 1);

			Requests.getServers(res -> MCStalker.toExecute.offer(() -> {
				if (client.currentScreen instanceof ServerDiscoveryScreen)
					client.setScreen(new ServerDiscoveryScreen(this.parent, res));
			}));
		}));

		drawablesBypass.add(new ButtonWidget(width / 2 + 4 + 76, height - 28, 75, 20, ScreenTexts.CANCEL, (button) -> client.setScreen(parent)));

		int width = 400;

		drawablesBypass.add(new ButtonWidget(this.width / 2 - width / 4, height - 52, width / 2, 20, new LiteralText("Filter Servers"), (button) -> this.client.setScreen(new FilterOptionsScreen(new LiteralText("Filter Options"), this.parent, this))));

		for (ButtonWidget widget : drawablesBypass) {
			addDrawableChild(widget);
		}

		updateButtonActivationStates();
	}

	@Override
	public void tick() {
		this.serverListPinger.tick();
	}

	@Override
	public void removed() {
		client.keyboard.setRepeatEvents(false);
		this.serverListPinger.cancel();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.serverListWidget == null) {
			this.serverListWidget = lastWidget;
		}

		if (keyCode == 256 && this.shouldCloseOnEsc()) {
			this.onClose();
			return true;
		} else if (keyCode == 294) {
			this.refresh();
			return true;
		} else if (this.serverListWidget != null && this.serverListWidget.getSelectedOrNull() != null) {
			if (keyCode != 257 && keyCode != 335) {
				return this.serverListWidget.keyPressed(keyCode, scanCode, modifiers);
			} else {
				this.connect();
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.tooltipText = null;
		this.serverListWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, "MCStalker Server Browser", this.width / 2, 20, 16777215);
		for (ButtonWidget widget : drawablesBypass) {
			widget.render(matrices, mouseX, mouseY, delta);
		}
		if (this.tooltipText != null) {
			this.renderTooltip(matrices, this.tooltipText, mouseX, mouseY);
		}
		client.textRenderer.draw(matrices, "MCStalker mod by " + MCStalker.AUTHORS, 2, 2, 0xFFFFFF);
		client.textRenderer.draw(matrices, "MCStalker by " + MCStalker.MCSTALKER_AUTHORS, 2, 5 + client.textRenderer.fontHeight, 0xFFFFFF);
	}

	@Override
	public void connect() {
		MultiplayerServerListWidget.Entry entry = serverListWidget.getSelectedOrNull();
		if (entry instanceof MultiplayerServerListWidget.ServerEntry e) {
			this.connectToServer(e.getServer());
		}
	}

	private void connectToServer(ServerInfo entry) {
		ServerDiscoveryScreen.wasAccessed = true;
		ConnectScreen.connect(this, client, ServerAddress.parse(entry.address), entry);
	}

	@Override
	public void select(MultiplayerServerListWidget.Entry entry) {
		this.serverListWidget.setSelected(entry);
		this.updateButtonActivationStates();
	}

	@Override
	protected void updateButtonActivationStates() {
		this.buttonJoin.active = false;

		MultiplayerServerListWidget.Entry entry = this.serverListWidget.getSelectedOrNull();
		if (entry != null && !(entry instanceof MultiplayerServerListWidget.ScanningEntry)) {
			this.buttonJoin.active = true;
		}

		final FilterProperties i = getInstance();
		if (i.page <= 1)
			this.buttonPrevious.active = false;

		if (i.page >= filterServerResponse.remainingPages)
			this.buttonNext.active = false;

		if (i.page > 1)
			this.buttonPrevious.active = true;

		if (i.page < filterServerResponse.remainingPages)
			this.buttonNext.active = true;
	}

	public static FilterServerResponse getFilterServerResponse() {
		return filterServerResponse;
	}

	public static boolean hasBeenAccessed() {
		return wasAccessed;
	}

	public static void setBeenAccessed(boolean wasAccessed) {
		ServerDiscoveryScreen.wasAccessed = wasAccessed;
	}

	@Override
	public MultiplayerServerListPinger getServerListPinger() {
		return this.serverListPinger;
	}

	@Override
	public void setTooltip(List<Text> tooltipText) {
		this.tooltipText = tooltipText;
	}

	@Override
	public ServerList getServerList() {
		return this.serverList;
	}

	private void refresh() {
		this.client.setScreen(new ServerDiscoveryScreen(this.parent, filterServerResponse));
	}
}