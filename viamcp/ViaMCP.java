package viamcp;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import sun.misc.URLClassPath;
import viamcp.loader.MCPBackwardsLoader;
import viamcp.loader.MCPViaLoader;
import viamcp.loader.MCPRewindLoader;
import viamcp.platform.MCPViaInjector;
import viamcp.platform.MCPViaPlatform;
import viamcp.utils.JLoggerToLog4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class ViaMCP
{
    public final static int PROTOCOL_VERSION = 47;
    private static final ViaMCP instance = new ViaMCP();

    public static ViaMCP getInstance()
    {
        return instance;
    }

    private final Logger jLogger = new JLoggerToLog4j(LogManager.getLogger("ViaMCP"));
    private final CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();

    private ExecutorService ASYNC_EXEC;
    private EventLoop EVENT_LOOP;

    private File file;
    private int version;
    private String lastServer;

    /**
     * Version Slider that works Asynchronously with the Version GUI
     * Please initialize this before usage with initAsyncSlider() or initAsyncSlider(x, y, width (min. 110), height)
     */

    public void start()
    {
        final File[] files = new File(Minecraft.getMinecraft().mcDataDir, "libraries").listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isFile() && f.getName().startsWith("Via") && f.getName().toLowerCase().endsWith(".jar")) {
                    f.delete();
                }
            }
        }
        copyjar("ViaBackwards-4.2.0-SNAPSHOT.jar",
                "ViaRewind-2.0.3-SNAPSHOT.jar",
                "ViaSnakeYaml-1.27.jar",
                "ViaVersion-4.2.0-SNAPSHOT.jar");
        try {
            loadVia();
        } catch (NoSuchFieldException | IllegalAccessException | MalformedURLException e) {
            e.printStackTrace();
        }
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaMCP-%d").build();
        ASYNC_EXEC = Executors.newFixedThreadPool(8, factory);

        EVENT_LOOP = new LocalEventLoopGroup(1, factory).next();
        EVENT_LOOP.submit(INIT_FUTURE::join);

        setVersion(PROTOCOL_VERSION);
        this.file = new File("ViaMCP");
        if (this.file.mkdir())
        {
            this.getjLogger().info("Creating ViaMCP Folder");
        }

        Via.init(ViaManagerImpl.builder().injector(new MCPViaInjector()).loader(new MCPViaLoader()).platform(new MCPViaPlatform(file)).build());

        MappingDataLoader.enableMappingsCache();
        ((ViaManagerImpl) Via.getManager()).init();

        new MCPBackwardsLoader(file);
        new MCPRewindLoader(file);

        INIT_FUTURE.complete(null);
    }
    public Logger getjLogger()
    {
        return jLogger;
    }

    public CompletableFuture<Void> getInitFuture()
    {
        return INIT_FUTURE;
    }

    public ExecutorService getAsyncExecutor()
    {
        return ASYNC_EXEC;
    }

    public EventLoop getEventLoop()
    {
        return EVENT_LOOP;
    }

    public File getFile()
    {
        return file;
    }

    public String getLastServer()
    {
        return lastServer;
    }

    public int getVersion()
    {
        return version;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public void setLastServer(String lastServer)
    {
        this.lastServer = lastServer;
    }

    public void loadVia() throws NoSuchFieldException, IllegalAccessException, MalformedURLException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Field addUrl = loader.getClass().getDeclaredField("ucp");
        addUrl.setAccessible(true);
        URLClassPath ucp = (URLClassPath) addUrl.get(loader);
        final File[] files = new File(Minecraft.getMinecraft().mcDataDir, "libraries").listFiles();
        if (files != null) {
            for (final File f : files) {
                if (f.isFile() && f.getName().startsWith("Via") && f.getName().toLowerCase().endsWith(".jar")) {
                    ucp.addURL(f.toURI().toURL());
                }
            }
        }
    }

    private final File library = new File(Minecraft.getMinecraft().mcDataDir, "libraries");
    private void copyjar(String... filenames){
        for (String filename : filenames) {
            File jar = new File(library, filename);
            InputStream is = ViaMCP.class.getResourceAsStream("/viamcp/lib/" + filename);
            if (is == null) {
                throw new IllegalStateException("ViaMCP cannot be found!");
            }
            try {
                Files.copy(is, jar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                jar.deleteOnExit();
            } catch (IOException e) {
                throw new IllegalStateException("ViaMCP cannot be loaded!");
            }
        }
    }
}
