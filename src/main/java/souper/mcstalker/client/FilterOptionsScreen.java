package souper.mcstalker.client;

import souper.mcstalker.client.api.MCStalkerAPIWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

// all sorting options are static and """global"""
public class FilterOptionsScreen extends Screen
{
    private static MCStalkerAPIWrapper.SortingOrder sortingOrder = MCStalkerAPIWrapper.SortingOrder.UPDATED;
    private static MCStalkerAPIWrapper.ASCDESC ascdesc = MCStalkerAPIWrapper.ASCDESC.DESC;
    private static String country = "all";
    private boolean hasInited;
    private Screen parent;

    private ButtonWidget btnApply = null;


    public FilterOptionsScreen(Text title, Screen parent)
    {
        super(title);
        this.parent = parent;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(btnApply != null)
        {
            btnApply.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void init()
    {
        this.client.keyboard.setRepeatEvents(true);
        hasInited = false;

        btnApply = new ButtonWidget(width / 2 + 4, height - 28, 70, 20, new LiteralText("Apply"), (button) -> {
            FilterOptionsScreen.setCountry("JP"); // temp testing code
            client.setScreen(new GuiMcstalkerServerBrowser(this.parent, MCStalkerAPIWrapper.refresh()));
        });

        btnApply.active = true;

        addDrawable(btnApply);

        super.init();
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    public static MCStalkerAPIWrapper.SortingOrder getSortingOrder()
    {
        return sortingOrder;
    }

    public static void setSortingOrder(MCStalkerAPIWrapper.SortingOrder sortingOrder)
    {
        FilterOptionsScreen.sortingOrder = sortingOrder;
    }

    public static MCStalkerAPIWrapper.ASCDESC getAscdesc()
    {
        return ascdesc;
    }

    public static void setAscdesc(MCStalkerAPIWrapper.ASCDESC ascdesc)
    {
        FilterOptionsScreen.ascdesc = ascdesc;
    }

    public static String getCountry()
    {
        return country;
    }

    public static void setCountry(String country)
    {
        FilterOptionsScreen.country = country;
    }
}
