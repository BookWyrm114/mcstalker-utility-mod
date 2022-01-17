package com.mcstalker.screen;

import com.mcstalker.ConfigManager;
import com.mcstalker.networking.Requests;
import com.mcstalker.networking.objects.FilterProperties;
import com.mcstalker.networking.objects.Filters;
import com.mcstalker.utils.widgets.BooleanButton;
import com.mcstalker.utils.widgets.EnumButton;
import com.mcstalker.utils.widgets.ISuggestionList;
import com.mcstalker.utils.widgets.SuggestionTextField;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mcstalker.networking.objects.FilterProperties.getInstance;

public class FilterOptionsScreen extends Screen {

	private final Screen parent;
	private final ServerDiscoveryScreen browser;

	private EnumButton<Filters.SortMode> buttonSortMode;
	private EnumButton<Filters.AscDesc> buttonAscdesc;
	private SuggestionTextField versionField;
	private SuggestionTextField countryField;
	private ButtonWidget buttonMustHavePlayersOnline;
	private ButtonWidget buttonMustBeVanilla;
	private TextFieldWidget motdSearchField;
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

		FilterProperties i = getInstance().clone();

		// apply button
		addDrawableChild(new ButtonWidget(center - 154, baseY + 150, 308, 20, Text.of("Apply Changes"), (button) -> {
			i.page = 1;
			try {
				FilterProperties.setInstance(i);
				ConfigManager.getInstance().writeConfigToFile();
				client.setScreen(new ServerDiscoveryScreen(this.parent, Requests.getServers()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));

		// back button
		addDrawableChild(new ButtonWidget(5, 5, 20, 20, Text.of("<"), (button) -> this.client.setScreen(this.browser)));

		// ------filter options------

		this.buttonSortMode = addDrawableChild(new EnumButton<>(center - 154, baseY, 150, 20, Text.of("Sort Mode: %value%"), button -> i.sortMode = ((EnumButton<Filters.SortMode>) button).getValue(), gOD(i.sortMode, Filters.SortMode.UPDATED), Filters.SortMode.class));

		this.buttonAscdesc = addDrawableChild(new EnumButton<>(center + 4, baseY, 150, 20, Text.of("Asc/Desc: %value%"), button -> i.ascdesc = ((EnumButton<Filters.AscDesc>) button).getValue(), gOD(i.ascdesc, Filters.AscDesc.DESC), Filters.AscDesc.class));

		this.versionField = addDrawableChild(new SuggestionTextField(this.client.textRenderer, center - 152, baseY + 30, 146, 20, Text.of(i.version.name()), new ISuggestionList() {
			@Override
			public List<String> getValues() {
				return new ArrayList<>(Filters.availableMojangVersions.keySet());
			}

			@Override
			public void onValueSelected(String value) {
				i.version = Filters.availableMojangVersions.get(value);
			}

			@Override
			public String getDefaultSuggestion() {
				return "Version search (e: 1.12.2)";
			}
		}));

		this.countryField = addDrawableChild(new SuggestionTextField(this.client.textRenderer, center + 4, baseY + 30, 146, 20, Text.of(i.country.getFancyName()), new ISuggestionList() {
			@Override
			public List<String> getValues() {
				return new ArrayList<>(Filters.Country.getCountries().keySet());
			}

			@Override
			public void onValueSelected(String value) {
				i.country = Filters.Country.getCountry(value);
			}

			@Override
			public String getDefaultSuggestion() {
				return "Country Search (e: US)";
			}
		}));

		this.buttonMustHavePlayersOnline = addDrawableChild(new BooleanButton(center - 154, baseY + 60, 150, 20, Text.of("Players online: %value%"), button -> i.mustHavePeople = ((BooleanButton) button).getValue(), i.mustHavePeople));

		this.buttonMustBeVanilla = addDrawableChild(new BooleanButton(center + 4, baseY + 60, 150, 20, Text.of("Vanilla only: %value%"), button -> i.vanillaOnly = ((BooleanButton) button).getValue(), i.vanillaOnly));

		this.motdSearchField = addDrawableChild(new TextFieldWidget(this.client.textRenderer, center - 152, baseY + 90, 305, 20, Text.of("MOTD Search")));
		this.motdSearchField.setText(i.searchText);
		this.motdSearchField.setChangedListener(text -> {
			i.searchText = text;
			this.motdSearchField.setSuggestion(text == null || text.isEmpty() ? "MOTD Search" : "");
		});
		this.motdSearchField.setSuggestion(this.motdSearchField.getText() == null || this.motdSearchField.getText().isEmpty() ? "MOTD Search" : "");

		this.buttonWhiteListStatus = addDrawableChild(new EnumButton<>(center - 154, baseY + 120, 150, 20, Text.of("Whitelist: %value%"), button -> i.whiteListStatus = ((EnumButton<Filters.WhiteListStatus>) button).getValue(), gOD(i.whiteListStatus, Filters.WhiteListStatus.ANY), Filters.WhiteListStatus.class));

		this.buttonAuthStatus = addDrawableChild(new EnumButton<>(center + 4, baseY + 120, 150, 20, Text.of("Authentication: %value%"), button -> i.authStatus = ((EnumButton<Filters.AuthStatus>) button).getValue(), gOD(i.authStatus, Filters.AuthStatus.ANY), Filters.AuthStatus.class));

		super.init();
	}

	private static <T> T gOD(@Nullable T value, T defaultValue) {
		return value != null ? value : defaultValue;
	}

	@Override
	public void onClose() {
		this.client.setScreen(this.browser);
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

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 258) {
			if (textFields.stream()
					.filter(TextFieldWidget::isFocused)
					.filter(f -> f instanceof SuggestionTextField)
					.map(SuggestionTextField.class::cast)
					.anyMatch(SuggestionTextField::onTabPressed))
				return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);

		textFields.forEach(t -> t.render(matrices, mouseX, mouseY, delta));
	}
}