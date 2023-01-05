/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.liuli.elixir.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public final class GsonExtensionKt {
    public static void set(JsonObject $this$set, String key, JsonElement value) {
        $this$set.add(key, value);
    }

    public static void set(JsonObject $this$set, String key, char value) {
        $this$set.addProperty(key, value);
    }

    public static void set(JsonObject $this$set, String key, Number value) {
        $this$set.addProperty(key, value);
    }

    public static void set(JsonObject $this$set, String key, String value) {
        $this$set.addProperty(key, value);
    }

    public static void set(JsonObject $this$set, String key, boolean value) {
        $this$set.addProperty(key, value);
    }

    public static String toJsonString(JsonElement $this$toJsonString, boolean prettyPrint) {
        Gson gson = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
        String string = gson.toJson($this$toJsonString);
        return string;
    }

    public static /* synthetic */ String toJsonString$default(JsonElement jsonElement, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = false;
        }
        return GsonExtensionKt.toJsonString(jsonElement, bl);
    }

    public static String string(JsonObject $this$string, String key) {
        return $this$string.has(key) ? $this$string.get(key).getAsString() : null;
    }

    public static Integer ints(JsonObject $this$int, String key) {
        return $this$int.has(key) ? $this$int.get(key).getAsInt() : null;
    }

    public static Long longs(JsonObject $this$long, String key) {
        return $this$long.has(key) ? $this$long.get(key).getAsLong() : null;
    }

    public static Double doubles(JsonObject $this$double, String key) {
        return $this$double.has(key) ? $this$double.get(key).getAsDouble() : null;
    }

    public static Boolean booleans(JsonObject $this$boolean, String key) {
        return $this$boolean.has(key) ? $this$boolean.get(key).getAsBoolean() : null;
    }

    public static JsonObject obj(JsonObject $this$obj, String key) {
        return $this$obj.has(key) ? $this$obj.get(key).getAsJsonObject() : null;
    }

    public static JsonArray array(JsonObject $this$array, String key) {
        return $this$array.has(key) ? $this$array.get(key).getAsJsonArray() : null;
    }

    public static String string(JsonArray $this$string, int index) {
        return $this$string.size() > index ? $this$string.get(index).getAsString() : null;
    }

    public static Integer ints(JsonArray $this$int, int index) {
        return $this$int.size() > index ? $this$int.get(index).getAsInt() : null;
    }

    public static Long longs(JsonArray $this$long, int index) {
        return $this$long.size() > index ? $this$long.get(index).getAsLong() : null;
    }

    public static Double doubles(JsonArray $this$double, int index) {
        return $this$double.size() > index ? $this$double.get(index).getAsDouble() : null;
    }

    public static Boolean booleans(JsonArray $this$boolean, int index) {
        return $this$boolean.size() > index ? $this$boolean.get(index).getAsBoolean() : null;
    }

    public static JsonObject obj(JsonArray $this$obj, int index) {
        return $this$obj.size() > index ? $this$obj.get(index).getAsJsonObject() : null;
    }

    public static JsonArray array(JsonArray $this$array, int index) {
        return $this$array.size() > index ? $this$array.get(index).getAsJsonArray() : null;
    }
}

