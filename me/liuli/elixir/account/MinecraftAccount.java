/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.account;

import com.google.gson.JsonObject;
import me.liuli.elixir.compat.Session;
import me.liuli.elixir.exception.LoginException;

import java.io.IOException;

public abstract class MinecraftAccount {
    private final String type;

    public MinecraftAccount(String type) {
        this.type = type;
    }

    public final String getType() {
        return this.type;
    }

    public abstract String getName();

    public abstract Session getSession();

    public abstract void update() throws LoginException, IOException;

    public abstract void toRawJson(JsonObject var1);

    public abstract void fromRawJson(JsonObject var1);
}

