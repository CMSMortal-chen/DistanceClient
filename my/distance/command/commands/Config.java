package my.distance.command.commands;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.channels.ReadableByteChannel;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;

import my.distance.ui.ClientNotification;
import my.distance.ui.notifications.user.Notifications;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.command.Command;
import my.distance.manager.ModuleManager;
import my.distance.module.Module;
import my.distance.util.misc.Helper;

public class Config extends Command {
	private JsonParser parser;
	private JsonObject jsonData;
	private static File dir;

	static {
		Config.dir = new File(String.valueOf(System.getenv("SystemDrive")) + "//config");
	}

	public Config() {
		super("config", new String[] { "cfg", "loadconfig", "preset" }, "config", "加载网络配置(官配)");
		this.parser = new JsonParser();
	}

	@SuppressWarnings("resource")
	private void hypixelCn(final String[] args) {
		Notifications.getManager().post("加载失败","国服已经关服,没必要更新了", Notifications.Type.WARNING);
	}
	@SuppressWarnings("resource")
	private void hypixelUs(final String[] args) {
		try {
			Helper.sendClientMessage("Config Version:V1", ClientNotification.Type.info);
			final URL settings = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/hypixel.txt");
			final URL enabled = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/hypixel_Enabled");
			final String filepath = String.valueOf(System.getenv("SystemDrive")) + "//config//HypixelUS.txt";
			final String filepathenabled = String.valueOf(System.getenv("SystemDrive"))
					+ "//config//HypixelUSEnabled.txt";
			final HttpURLConnection httpcon = (HttpURLConnection)settings.openConnection();
            httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
            final ReadableByteChannel channel = Channels.newChannel(httpcon.getInputStream());
            final HttpURLConnection httpcon2 = (HttpURLConnection)enabled.openConnection();
            httpcon2.addRequestProperty("User-Agent", "Mozilla/4.0");
            final ReadableByteChannel channelenabled = Channels.newChannel(httpcon2.getInputStream());
            final FileOutputStream stream = new FileOutputStream(filepath);
            final FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
            stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
            streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
			Helper.sendClientMessage("Loaded official config.", ClientNotification.Type.success);
		} catch (Exception e) {
			Helper.sendClientMessage("Download failed.", ClientNotification.Type.error);
		}
		final List<String> enabled2 = read("HypixelUSEnabled.txt");
		for (Module m : ModuleManager.modules){
			if (m.isEnabled()) {
				m.setEnabled(false);
			}
		}
		for (final String v : enabled2) {
			final Module m = ModuleManager.getModuleByName(v);
			if (m == null) {
				continue;
			}
			m.setEnabled(true);
		}
		final List<String> vals = read("HypixelUS.txt");
		for (final String v2 : vals) {
			final String name = v2.split(":")[0];
			final String values = v2.split(":")[1];
			final Module i = ModuleManager.getModuleByName(name);
			if (i == null) {
				continue;
			}
			for (final Value value : i.getValues()) {
				if (value.getName().equalsIgnoreCase(values)) {
					if (value instanceof Option) {
						value.setValue(Boolean.parseBoolean(v2.split(":")[2]));
					} else if (value instanceof Numbers) {
						value.setValue(Double.parseDouble(v2.split(":")[2]));
					} else {
						((Mode) value).setMode(v2.split(":")[2]);
					}
				}
			}
		}
	}
	private void custom(String names) {
		try {
			Notifications.getManager().post("注意","尝试加载自定义配置,可能会失败");
			final URL settings = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/"+names);
			final URL enabled = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/"+names+"_Enabled");
			final String filepath = String.valueOf(System.getenv("SystemDrive")) + "//config//tmp.txt";
			final String filepathenabled = String.valueOf(System.getenv("SystemDrive"))
					+ "//config//tmpEnabled.txt";
			final HttpURLConnection httpcon = (HttpURLConnection)settings.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channel = Channels.newChannel(httpcon.getInputStream());
			final HttpURLConnection httpcon2 = (HttpURLConnection)enabled.openConnection();
			httpcon2.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channelenabled = Channels.newChannel(httpcon2.getInputStream());
			final FileOutputStream stream = new FileOutputStream(filepath);
			final FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
			stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
			streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
			Helper.sendClientMessage("Loaded official config.", ClientNotification.Type.success);
		} catch (Exception e) {
			Helper.sendClientMessage("Download failed.", ClientNotification.Type.error);
		}
		final List<String> enabled2 = read("tmpEnabled.txt");
		for (Module m : ModuleManager.modules){
			if (m.isEnabled()) {
				m.setEnabled(false);
			}
		}
		for (final String v : enabled2) {
			final Module m = ModuleManager.getModuleByName(v);
			if (m == null) {
				continue;
			}
			m.setEnabled(true);
		}
		final List<String> vals = read("tmp.txt");
		for (final String v2 : vals) {
			final String name = v2.split(":")[0];
			final String values = v2.split(":")[1];
			final Module i = ModuleManager.getModuleByName(name);
			if (i == null) {
				continue;
			}
			for (final Value value : i.getValues()) {
				if (value.getName().equalsIgnoreCase(values)) {
					if (value instanceof Option) {
						value.setValue(Boolean.parseBoolean(v2.split(":")[2]));
					} else if (value instanceof Numbers) {
						value.setValue(Double.parseDouble(v2.split(":")[2]));
					} else {
						((Mode) value).setMode(v2.split(":")[2]);
					}
				}
			}
		}
	}
	private void redesky(final String[] args) {
		try {
			final URL settings = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/hypixel.txt");
			final URL enabled = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/hypixel_Enabled");
			final String filepath = String.valueOf(System.getenv("SystemDrive")) + "//config//HypixelUS.txt";
			final String filepathenabled = String.valueOf(System.getenv("SystemDrive"))
					+ "//config//HypixelUSEnabled.txt";
			final HttpURLConnection httpcon = (HttpURLConnection)settings.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channel = Channels.newChannel(httpcon.getInputStream());
			final HttpURLConnection httpcon2 = (HttpURLConnection)enabled.openConnection();
			httpcon2.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channelenabled = Channels.newChannel(httpcon2.getInputStream());
			final FileOutputStream stream = new FileOutputStream(filepath);
			final FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
			stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
			streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
			Helper.sendClientMessage("Loaded official config.", ClientNotification.Type.success);
		} catch (Exception e) {
			Helper.sendClientMessage("Download failed.", ClientNotification.Type.error);
		}
		final List<String> enabled2 = read("HypixelUSEnabled.txt");
		for (Module m : ModuleManager.modules){
			if (m.isEnabled()) {
				m.setEnabled(false);
			}
		}
		for (final String v : enabled2) {
			final Module m = ModuleManager.getModuleByName(v);
			if (m == null) {
				continue;
			}
			m.setEnabled(true);
		}
		final List<String> vals = read("HypixelUS.txt");
		for (final String v2 : vals) {
			final String name = v2.split(":")[0];
			final String values = v2.split(":")[1];
			final Module i = ModuleManager.getModuleByName(name);
			if (i == null) {
				continue;
			}
			for (final Value value : i.getValues()) {
				if (value.getName().equalsIgnoreCase(values)) {
					if (value instanceof Option) {
						value.setValue(Boolean.parseBoolean(v2.split(":")[2]));
					} else if (value instanceof Numbers) {
						value.setValue(Double.parseDouble(v2.split(":")[2]));
					} else {
						((Mode) value).setMode(v2.split(":")[2]);
					}
				}
			}
		}
	}
	public static List<String> read(final String file) {
		final List<String> out = new ArrayList<>();
		try {
			if (!Config.dir.exists()) {
				Config.dir.mkdir();
			}
			final File f = new File(Config.dir, file);
			if (!f.exists()) {
				f.createNewFile();
			}
			Throwable t = null;
			try {
				final FileInputStream fis = new FileInputStream(f);
				try {
					final InputStreamReader isr = new InputStreamReader(fis);
					try {
						final BufferedReader br = new BufferedReader(isr);
						try {
							String line = "";
							while ((line = br.readLine()) != null) {
								out.add(line);
							}
						} finally {
							if (br != null) {
								br.close();
							}
						}
						if (isr != null) {
							isr.close();
						}
					} finally {
						if (t == null) {
							final Throwable t2 = null;
							t = t2;
						} else {
							final Throwable t2 = null;
							if (t != t2) {
								t.addSuppressed(t2);
							}
						}
						if (isr != null) {
							isr.close();
						}
					}
					if (fis != null) {
						fis.close();
						return out;
					}
				} finally {
					if (t == null) {
						final Throwable t3 = null;
						t = t3;
					} else {
						final Throwable t3 = null;
						if (t != t3) {
							t.addSuppressed(t3);
						}
					}
					if (fis != null) {
						fis.close();
					}
				}
			} finally {
				if (t == null) {
					final Throwable t4 = null;
					t = t4;
				} else {
					final Throwable t4 = null;
					if (t != t4) {
						t.addSuppressed(t4);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
	private void Maxmc(final String[] args) {
		try {
			final URL settings = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/maxmc");
			final URL enabled = new URL("https://gitee.com/Mymylesaws_Air/Air/raw/master/config/maxmc_Enabled");
			final String filepath = String.valueOf(System.getenv("SystemDrive")) + "//config//MaxMc.txt";
			final String filepathenabled = String.valueOf(System.getenv("SystemDrive"))
					+ "//config//MaxMcEnabled.txt";
			final HttpURLConnection httpcon = (HttpURLConnection)settings.openConnection();
			httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channel = Channels.newChannel(httpcon.getInputStream());
			final HttpURLConnection httpcon2 = (HttpURLConnection)enabled.openConnection();
			httpcon2.addRequestProperty("User-Agent", "Mozilla/4.0");
			final ReadableByteChannel channelenabled = Channels.newChannel(httpcon2.getInputStream());
			final FileOutputStream stream = new FileOutputStream(filepath);
			final FileOutputStream streamenabled = new FileOutputStream(filepathenabled);
			stream.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
			streamenabled.getChannel().transferFrom(channelenabled, 0L, Long.MAX_VALUE);
			Helper.sendClientMessage("Loaded official config.", ClientNotification.Type.success);
		} catch (Exception e) {
			Helper.sendClientMessage("Download failed.", ClientNotification.Type.error);
		}
		final List<String> enabled2 = read("MaxMcEnabled.txt");
		for (Module m : ModuleManager.modules){
			m.setEnabled(false);
		}
		for (final String v : enabled2) {
			final Module m = ModuleManager.getModuleByName(v);
			if (m == null) {
				continue;
			}
			m.setEnabled(true);
		}
		final List<String> vals = read("MaxMc.txt");
		for (final String v2 : vals) {
			final String name = v2.split(":")[0];
			final String values = v2.split(":")[1];
			final Module i = ModuleManager.getModuleByName(name);
			if (i == null) {
				continue;
			}
			for (final Value value : i.getValues()) {
				if (value.getName().equalsIgnoreCase(values)) {
					if (value instanceof Option) {
						value.setValue(Boolean.parseBoolean(v2.split(":")[2]));
					} else if (value instanceof Numbers) {
						value.setValue(Double.parseDouble(v2.split(":")[2]));
					} else {
						((Mode) value).setMode(v2.split(":")[2]);
					}
				}
			}
		}
	}
	@Override
	public String execute(final String[] args) {
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("hypixelus")) {
				this.hypixelUs(args);
			} else if (args[0].equalsIgnoreCase("list")) {
				Helper.sendMessageWithoutPrefix("=============== DistanceConfigs ===================");
				Helper.sendMessageWithoutPrefix("HypixelUS");
				Helper.sendMessageWithoutPrefix("===================================================");
			}else {
				return null;
				//this.custom(args[0]);
			}
		} else {
			Helper.sendMessage("Correct usage .config <config>. Use '.config list' to list config");
		}
		return null;
	}
}
