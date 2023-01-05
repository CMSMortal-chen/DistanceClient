package my.distance.manager;

import my.distance.api.value.Mode;
import my.distance.api.value.Numbers;
import my.distance.api.value.Option;
import my.distance.api.value.Value;
import my.distance.Client;
import my.distance.module.Module;
import my.distance.module.modules.render.ClickGui;
import my.distance.ui.notifications.user.Notifications;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;

public class ConfigManager {
    public static void saveConfig(String dirs){
        Notifications.getManager().post("ConfigManager","尝试保存配置:"+dirs);
        File dir = new File(FileManager.dir,dirs);
        if (!dir.exists()){
            dir.mkdir();
        }
        StringBuilder values = new StringBuilder();
        for (Module m : ModuleManager.getModules()) {
            for (Value v : m.getValues()) {
                values.append(String.format("%s:%s:%s%s", m.getName(), v.getName(), v.getValue(), System.lineSeparator()));
            }
        }
        FileManager.save(dir,"Values.txt", values.toString(), false);
        String content = "";
        Module m;
        for (Iterator<Module> var4 = ModuleManager.getModules().iterator(); var4.hasNext(); content = content + String.format(
                "%s:%s%s", m.getName(), Keyboard.getKeyName(m.getKey()), System.lineSeparator())) {
            m = var4.next();
        }

        FileManager.save(dir,"Binds.txt", content, false);
        StringBuilder enabled = new StringBuilder();
        for (Module mod : ModuleManager.getModules()) {
            if (mod.isEnabled())
                enabled.append(String.format("%s%s", mod.getName(), System.lineSeparator()));
        }
        FileManager.save(dir,"Enabled.txt", enabled.toString(), false);

        String ConfigInfo = String.format("%s:%s%s", dirs , Client.userName,System.lineSeparator());
        FileManager.save(dir,"Config.DistanceConfigInfo", ConfigInfo, false);
        Notifications.getManager().post("ConfigManager","保存配置:"+dirs+" 完成");
    }
    public static void loadConfig(String dirs){
        File dir = new File(FileManager.dir,dirs);
        if (!dir.exists()){
            Notifications.getManager().post("ConfigManager","您加载的配置不存在!");
            return;
        }
        try {
            File info = new File(dir,"Config.DistanceConfigInfo");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(info));
            String s = bufferedReader.readLine();
            String[] s2 = s.split(":");
            Notifications.getManager().post("ConfigManager","配置:"+s2[0]+" 作者:"+s2[1]);
        } catch (Exception ignored) { }

        List<String> binds = FileManager.read(dir,"Binds.txt");
        for (String v : binds) {
            String name = v.split(":")[0];
            String bind = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null)
                continue;
            m.setKey(Keyboard.getKeyIndex(bind.toUpperCase()));
        }
        if(ModuleManager.getModuleByClass(ClickGui.class).getKey() == 0)
            ModuleManager.getModuleByClass(ClickGui.class).setKey(Keyboard.getKeyIndex("RSHIFT"));
        List<String> enabled = FileManager.read(dir,"Enabled.txt");
        for (Module m : ModuleManager.modules){
            if (m.isEnabled()) {
                m.setEnabled(false);
            }
        }
        for (String v : enabled) {
            Module m = ModuleManager.getModuleByName(v);
            if (m == null)
                continue;
            if (!m.isEnabled()) {
                m.setEnabled(true);
            }
        }
        List<String> vals = FileManager.read(dir,"Values.txt");
        for (String v : vals) {
            String name = v.split(":")[0];
            String values = v.split(":")[1];
            Module m = ModuleManager.getModuleByName(name);
            if (m == null)
                continue;

            for (Value value : m.getValues()) {
                if (!value.getName().equalsIgnoreCase(values))
                    continue;
                if (value instanceof Option) {
                    value.setValue(Boolean.parseBoolean(v.split(":")[2]));
                    continue;
                }
                if (value instanceof Numbers) {
                    value.setValue(Double.parseDouble(v.split(":")[2]));

                    continue;
                }
                ((Mode) value).setMode(v.split(":")[2]);
            }
        }
    }
    public static void removeConfig(String dirs){
        File dir = new File(FileManager.dir,dirs);
        if (!dir.exists()){
            Notifications.getManager().post("ConfigManager","您尝试删除的配置不存在!");
        }else {
            deleteFile(dir);
            Notifications.getManager().post("ConfigManager","配置"+dirs+"已删除!");
        }
    }
    public static void deleteFile(File file) {
        if(file.exists()) {
            if(file.isFile()){
                file.delete();
            }else{
                File[] listFiles = file.listFiles();
                for (File file2 : listFiles) {
                    deleteFile(file2);
                }
            }
            file.delete();
        }else {
            System.err.println("路径不存在?");
        }
    }

}
