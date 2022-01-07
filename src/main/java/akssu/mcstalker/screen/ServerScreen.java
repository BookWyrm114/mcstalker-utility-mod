package akssu.mcstalker.screen;


import akssu.mcstalker.FileManager;
import akssu.mcstalker.MCStalker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.LanServerQueryManager.LanServerDetector;
import net.minecraft.client.network.LanServerQueryManager.LanServerEntryList;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ServerScreen extends Screen {
    private final MultiplayerServerListPinger serverListPinger = new MultiplayerServerListPinger();
    private final Screen parent;
    //protected MultiplayerServerListWidget serverListWidget;
    //private ServerList serverList;
    private ButtonWidget buttonEdit;
    private ButtonWidget buttonNextPage;
    private ButtonWidget buttonPreviousPage;
    private ButtonWidget buttonJoin;
    private ButtonWidget buttonDelete;
    private ButtonWidget buttonMustHavePlayersOnline;
    private ButtonWidget buttonMustBeVanilla;
    protected TextFieldWidget searchBox1;
    protected TextFieldWidget searchBox2;
    @Nullable
    private List<Text> tooltipText;
    private ServerInfo selectedEntry;
    private LanServerEntryList lanServers;
    @Nullable
    private LanServerDetector lanServerDetector;
    private boolean initialized;



    private int top = 64;
    private double scrollAmount;
    protected final int itemHeight = 36;
    protected int headerHeight;
    private boolean renderHeader;
    protected int left = 0;
    protected int right = this.width;


    public ServerScreen(Screen parent) {
        super(Text.of("MC Stalker"));
        this.parent = parent;
    }

    protected void init() {
        super.init();
        this.client.keyboard.setRepeatEvents(true);


        //server list

        /*
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 64, this.height - 64);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            this.serverList.loadFile();
            //this.lanServers = new LanServerEntryList();

            this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
            this.serverListWidget.setServers(this.serverList);
        }

        this.addSelectableChild(this.serverListWidget);
         */

        this.buttonJoin = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 52, 100, 20,Text.of("Join Server"), (button) -> {

        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 50, this.height - 52, 100, 20,Text.of("Remove Server"), (button) -> {

        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 4 + 50, this.height - 52, 100, 20, Text.of("Cancel"), (button) -> {
            this.client.setScreen(this.parent);
        }));

        this.searchBox1 = new TextFieldWidget(this.textRenderer, this.width / 2 - 151 -2, 22 + 16, 151, 20, this.searchBox1, Text.of("search"));
        this.searchBox1.setChangedListener((search) -> {

        });

        this.searchBox2 = new TextFieldWidget(this.textRenderer, this.width / 2 + 2, 22 + 16, 151, 20, this.searchBox2, Text.of("search"));
        this.searchBox2.setChangedListener((search) -> {

        });

        boolean musthaveplayersonline = ((boolean)MCStalker.settings.getSettingByName("MustHavePlayersOnline").get());
        this.buttonMustHavePlayersOnline = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 28, 152, 20, Text.of("Must have players: " + musthaveplayersonline), (button) -> {
            if(MCStalker.settings.getSettingByName("MustHavePlayersOnline").get() == true){
                MCStalker.settings.getSettingByName("MustHavePlayersOnline").set(false);
            } else {
                MCStalker.settings.getSettingByName("MustHavePlayersOnline").set(true);
            }
            FileManager.INSTANCE.saveSettingsList();
            this.client.setScreen(new ServerScreen(this.parent));
        }));

        boolean mustbevanilla = ((boolean)MCStalker.settings.getSettingByName("MustBeVanilla").get());
        this.buttonMustBeVanilla = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, this.height - 28, 152, 20, Text.of("Must be Vanilla: " + mustbevanilla), (button) -> {
            if(MCStalker.settings.getSettingByName("MustBeVanilla").get() == true){
                MCStalker.settings.getSettingByName("MustBeVanilla").set(false);
            } else {
                MCStalker.settings.getSettingByName("MustBeVanilla").set(true);
            }
            FileManager.INSTANCE.saveSettingsList();
            this.client.setScreen(new ServerScreen(this.parent));
        }));

        this.buttonPreviousPage = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 - 154, 28 - 20, 80, 20, Text.of("Prev Page"), (button) -> {

        }));

        this.buttonNextPage = (ButtonWidget)this.addDrawableChild(new ButtonWidget(this.width / 2 + 154 - 80, 28 - 20, 80, 20, Text.of("Next Page"), (button) -> {

        }));

        this.addSelectableChild(searchBox1);
        this.addSelectableChild(searchBox2);

    }

    public void tick() {

        this.searchBox1.tick();
        this.searchBox2.tick();

        this.serverListPinger.tick();
    }

    public void removed() {
        this.client.keyboard.setRepeatEvents(false);
        if (this.lanServerDetector != null) {
            this.lanServerDetector.interrupt();
            this.lanServerDetector = null;
        }

        this.serverListPinger.cancel();
    }

    private void refresh() {
        this.client.setScreen(new net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen(this.parent));
    }

    private void directConnect(boolean confirmedAction) {
        if (confirmedAction) {
            this.connect(this.selectedEntry);
        } else {
            this.client.setScreen(this);
        }

    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.tooltipText = null;
        this.renderBackground(matrices);
        //this.serverListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, Text.of("Search servers"), this.width / 2, 20, 16777215);

        this.searchBox1.render(matrices, mouseX, mouseY, delta);
        this.searchBox2.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
        if (this.tooltipText != null) {
            this.renderTooltip(matrices, this.tooltipText, mouseX, mouseY);
        }

    }

    private void connect(ServerInfo entry) {
        ConnectScreen.connect(this, this.client, ServerAddress.parse(entry.address), entry);
    }

    public MultiplayerServerListPinger getServerListPinger() {
        return this.serverListPinger;
    }



    /*
    public ServerList getServerList() {
        return this.serverList;
    }

     */

}
