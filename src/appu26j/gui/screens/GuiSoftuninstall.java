package appu26j.gui.screens;

import appu26j.Registry;
import appu26j.assets.Assets;
import appu26j.gui.Gui;
import appu26j.gui.textures.Texture;
import appu26j.utils.AppUtil;
import appu26j.utils.ScissorUtil;
import appu26j.utils.apps.AppInfo;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class GuiSoftuninstall extends GuiScreen
{
    public static final ArrayList<AppInfo> apps = new ArrayList<>();
    private String previousUninstalledApp = "";
    private float scroll = 0, maxScroll = 0;
    private boolean dragging = false;
    public static int loading = 2;
    private Texture delete;

    @Override
    public void drawScreen(float mouseX, float mouseY)
    {
        if (!apps.isEmpty())
        {
            if (this.dragging)
            {
                float progress = (mouseY - 15) / (this.height - 30);
                this.scroll = this.maxScroll * progress;

                if (this.scroll < 0)
                {
                    this.scroll = 0;
                }

                if (this.scroll > this.maxScroll)
                {
                    this.scroll = this.maxScroll;
                }
            }

            this.maxScroll = 0;
            float y = 15 - this.scroll;
            ArrayList<AppInfo> finalApps = new ArrayList<>(apps);

            for (AppInfo appInfo : finalApps)
            {
                Gui.drawRect(15, y, this.width - 40, y + 60, this.isInsideBox(mouseX, mouseY, 15, y, this.width - 40, y + 60) ? new Color(235, 235, 235) : new Color(245, 245, 245));
                this.fontRenderer.drawString(appInfo.getName(), 25, y, new Color(50, 50, 50));
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                ScissorUtil.scissor(0, 0, this.width - 100, this.height);
                this.fontRenderer.drawString(appInfo.getUninstallCmd(), this.fontRenderer.getStringWidth(appInfo.getPublisher()) + 30, y + 27, new Color(175, 175, 175));
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                this.fontRenderer.drawString(appInfo.getPublisher(), 25, y + 27, new Color(125, 125, 125));
                Gui.drawImage(this.delete, this.width - 95, y + 4, 0, 0, 48, 48, 48, 48);
                y += 75;
                this.maxScroll += 75;
            }

            this.maxScroll -= (loading == 0 ? 685 : 625);

            if (this.maxScroll < 0)
            {
                this.maxScroll = 0;
            }

            if (this.scroll > this.maxScroll)
            {
                this.scroll = this.maxScroll;
            }

            if (loading != 0)
            {
                this.fontRendererMid.drawString("Loading...", (this.width / 2) - (this.fontRendererMid.getStringWidth("Loading...") / 2), y - 10, new Color(100, 100, 100));
            }

            Gui.drawRect(this.width - 27, 15, this.width - 13, this.height - 15, new Color(200, 200, 200));
            float scrollProgress = (this.scroll / this.maxScroll);
            float yPos = ((this.height - 180) * scrollProgress) + 90;
            Gui.drawRect(this.width - 27, yPos - 75, this.width - 13, yPos + 75, new Color(245, 245, 245));
        }
    }

    @Override
    public void mouseClicked(int mouseButton, float mouseX, float mouseY)
    {
        super.mouseClicked(mouseButton, mouseX, mouseY);
        float y = 15 - this.scroll;
        ArrayList<AppInfo> finalApps = new ArrayList<>(apps);

        for (AppInfo appInfo : finalApps)
        {
            if (this.isInsideBox(mouseX, mouseY, 15, y, this.width - 40, y + 60) && !this.previousUninstalledApp.equals(appInfo.getName()))
            {
                new Thread(() ->
                {
                    try
                    {
                        Process process = Runtime.getRuntime().exec(appInfo.getUninstallCmd());
                        process.waitFor();
                        String output = Registry.execute("QUERY " + appInfo.getRegistryPath() + " /v DisplayName");

                        if (output.contains("ERROR: The system was unable to find the specified registry key or value."))
                        {
                            GuiSoftuninstall.apps.remove(appInfo);
                        }

                        this.previousUninstalledApp = "";
                    }

                    catch (Exception e)
                    {
                        ;
                    }
                }).start();

                this.previousUninstalledApp = appInfo.getName();
                break;
            }

            y += 75;
        }

        if (this.isInsideBox(mouseX, mouseY, this.width - 37, 5, this.width - 3, this.height - 5) && mouseButton == 0)
        {
            this.dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseButton, float mouseX, float mouseY)
    {
        super.mouseReleased(mouseButton, mouseX, mouseY);
        this.dragging = false;
    }

    @Override
    public void initGUI(float width, float height)
    {
        super.initGUI(width, height);
        AppUtil.loadApps();
        this.delete = Gui.getTexture(Assets.getAsset("delete.png"));
    }

    @Override
    public void scroll(float scrollY)
    {
        super.scroll(scrollY);
        this.scroll -= scrollY * 37.5F;

        if (this.scroll < 0)
        {
            this.scroll = 0;
        }

        if (this.scroll > this.maxScroll)
        {
            this.scroll = this.maxScroll;
        }
    }
}
