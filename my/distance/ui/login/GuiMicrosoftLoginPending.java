package my.distance.ui.login;

import my.distance.util.misc.Helper;
import my.distance.fastuni.FontLoader;
import me.liuli.elixir.account.MicrosoftAccount;
import me.liuli.elixir.compat.OAuthServer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiMicrosoftLoginPending extends GuiScreen {
    private String stage = "Initializing...";
    private OAuthServer server;
    private GuiScreen prevGui;

    public GuiMicrosoftLoginPending(GuiScreen prevGui){
        this.prevGui = prevGui;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            server.stop(true);
            mc.displayGuiScreen(prevGui);
        }
    }

    @Override
    public void drawScreen(int mouseX,int mouseY,float partialTicks) {
        this.drawDefaultBackground();

        FontLoader.msFont16.drawCenteredStringWithShadow(stage, width / 2f, height / 2f - 50, 0xffffff);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        server = MicrosoftAccount.Companion.buildFromOpenBrowser(new MicrosoftAccount.OAuthHandler() {
            @Override
            public void openUrl(String url) {
                stage = "请在浏览器里面继续操作...";
                System.out.println(("Opening URL: ") + url);
                Helper.showURL(url);
            }

            @Override
            public void authError(String error) {
                stage = "错误: " + error;
            }

            @Override
            public void authResult(MicrosoftAccount account) {
                AltManager.getAlts().add(new Alt(account));
                mc.displayGuiScreen(prevGui);
            }
        });

    }
}
