package com.mcstalker.screen;

import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.Filters;
import com.mcstalker.utils.BooleanButton;
import com.mcstalker.utils.EnumButton;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.mcstalker.networking.objects.FilterProperties.*;

public class FilterOptionsScreen extends Screen {

	private final Screen parent;
	private final ServerDiscoveryScreen browser;

	private EnumButton<Filters.SortMode> buttonSortMode;
	private EnumButton<Filters.AscDesc> buttonAscdesc;
	private TextFieldWidget buttonVersionName;
	private ButtonWidget buttonMustHavePlayersOnline;
	private ButtonWidget buttonMustBeVanilla;
	private TextFieldWidget buttonMotdSearch;
	private EnumButton<Filters.WhiteListStatus> buttonWhiteListStatus;
	private EnumButton<Filters.AuthStatus> buttonAuthStatus;

	public FilterOptionsScreen(Text title, Screen parent, ServerDiscoveryScreen browser) {
		super(title);
		this.parent = parent;
		this.browser = browser;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		assert this.client != null;

		this.children().forEach(this::remove);
		this.client.keyboard.setRepeatEvents(true);
		this.textFields.clear();

		int height = 6 * 20 + 5 * 10;
		int baseY = (this.height - height) / 2;
		int center = this.width / 2;

		// apply button
		ButtonWidget btnApply = addDrawableChild(new ButtonWidget(center - 154, baseY + 150, 308, 20, Text.of("Apply Changes"), (button) -> {
			page = 1;
			try {
				client.setScreen(new ServerDiscoveryScreen(this.parent, Requests.getServers()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		// back button
		addDrawableChild(new ButtonWidget(5, 5, 20, 20, Text.of("<"), (button) -> {
			this.client.setScreen(this.browser);
		}));

		// ------filter options------

		this.buttonSortMode = addDrawableChild(new EnumButton<>(center - 154, baseY, 150, 20, Text.of("Sort Mode: %value%"), button -> {
			saveSettings();
		}, Filters.SortMode.UPDATED, Filters.SortMode.class));

		this.buttonAscdesc = addDrawableChild(new EnumButton<>(center + 4, baseY, 150, 20, Text.of("Asc/Desc: %value%"), button -> {
			saveSettings();
		}, Filters.AscDesc.DESC, Filters.AscDesc.class));

		// TODO: Version
		this.buttonVersionName = addDrawableChild(new TextFieldWidget(this.client.textRenderer, center - 152, baseY + 30, 146, 20, Text.of("Version")));
		this.buttonVersionName.setSuggestion(this.buttonVersionName.getText() == null || this.buttonVersionName.getText().isEmpty() ? "Version Search (e: 1.12.2)" : "");
		this.buttonVersionName.active = false;
		this.buttonVersionName.setTextPredicate((text) -> false);

		// TODO: country

		this.buttonMustHavePlayersOnline = addDrawableChild(new BooleanButton(center - 154, baseY + 60, 150, 20, Text.of("Players online: %value%"), button -> {
			mustHavePeople = ((BooleanButton) button).getValue();
			saveSettings();
		}, mustHavePeople));

		this.buttonMustBeVanilla = addDrawableChild(new BooleanButton(center + 4, baseY + 60, 150, 20, Text.of("Vanilla only: %value%"), button -> {
			vanillaOnly = ((BooleanButton) button).getValue();
			saveSettings();
		}, vanillaOnly));

		this.buttonMotdSearch = addDrawableChild(new TextFieldWidget(this.client.textRenderer, center - 152, baseY + 90, 305, 20, Text.of("MOTD Search")));
		this.buttonMotdSearch.setText(searchText);
		this.buttonMotdSearch.setChangedListener(text -> {
			searchText = text;
			this.buttonMotdSearch.setSuggestion(text == null || text.isEmpty() ? "MOTD Search" : "");
			saveSettings();
		});
		this.buttonMotdSearch.setSuggestion(this.buttonMotdSearch.getText() == null || this.buttonMotdSearch.getText().isEmpty() ? "MOTD Search" : "");

		this.buttonWhiteListStatus = addDrawableChild(new EnumButton<>(center - 154, baseY + 120, 150, 20, Text.of("Whitelist: %value%"), button -> {
			whiteListStatus = ((EnumButton<Filters.WhiteListStatus>) button).getValue();
			saveSettings();
		}, Filters.WhiteListStatus.ANY, Filters.WhiteListStatus.class));

		this.buttonAuthStatus = addDrawableChild(new EnumButton<>(center + 4, baseY + 120, 150, 20, Text.of("Authentication: %value%"), button -> {
			authStatus = ((EnumButton<Filters.AuthStatus>) button).getValue();
			saveSettings();
		}, Filters.AuthStatus.ANY, Filters.AuthStatus.class));

		super.init();
	}

	private static void saveSettings() {
		// TODO: save settings
	}

	@Override
	public void tick() {
		super.tick();
		this.textFields.forEach(TextFieldWidget::tick);
	}

	private final List<TextFieldWidget> textFields = new ArrayList<>();

	@Override
	protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		if (drawableElement instanceof TextFieldWidget t)
			textFields.add(t);

		return super.addDrawableChild(drawableElement);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		textFields.forEach(t -> t.render(matrices, mouseX, mouseY, delta));
	}
}