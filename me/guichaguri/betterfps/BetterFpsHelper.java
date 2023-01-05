package me.guichaguri.betterfps;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author Guilherme Chaguri
 */
public class BetterFpsHelper {

    public static final String MC_VERSION = "1.8.9";
    public static final String VERSION = "1.2.1";

    public static final String URL = "http://guichaguri.github.io/BetterFps/";

    public static final String UPDATE_URL = "https://raw.githubusercontent.com/Guichaguri/BetterFps/1.8/lastest-version.properties";

    // Config Name, Class Name
    public static final LinkedHashMap<String, String> helpers = new LinkedHashMap<>();

    // Config Name, Display Name
    public static final LinkedHashMap<String, String> displayHelpers = new LinkedHashMap<>();

    static {
        helpers.put("vanilla", "VanillaMath");
        helpers.put("rivens", "RivensMath");
        helpers.put("taylors", "TaylorMath");
        helpers.put("libgdx", "LibGDXMath");
        helpers.put("rivens-full", "RivensFullMath");
        helpers.put("rivens-half", "RivensHalfMath");
        helpers.put("java", "JavaMath");
        helpers.put("random", "RandomMath");
        helpers.put("distance", "DistanceMath");

        displayHelpers.put("vanilla", "Vanilla Algorithm");
        displayHelpers.put("rivens", "Riven's Algorithm");
        displayHelpers.put("taylors", "Taylor's Algorithm");
        displayHelpers.put("libgdx", "LibGDX's Algorithm");
        displayHelpers.put("rivens-full", "Riven's \"Full\" Algorithm");
        displayHelpers.put("rivens-half", "Riven's \"Half\" Algorithm");
        displayHelpers.put("java", "Java Math");
        displayHelpers.put("random", "Random Math");
        displayHelpers.put("distance","Distance Algorithm");
    }

    public static File LOC;
    public static File MCDIR = null;

    public static void init() {

    }

    public static BetterFpsConfig loadConfig() {

        try {
            BetterFpsConfig.instance = new BetterFpsConfig();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return BetterFpsConfig.instance;
    }

    public static void saveConfig() {
    }

}
