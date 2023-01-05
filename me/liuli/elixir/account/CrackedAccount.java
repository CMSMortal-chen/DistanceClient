package me.liuli.elixir.account;

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import me.liuli.elixir.compat.Session;
import me.liuli.elixir.utils.GsonExtensionKt;

public final class CrackedAccount
extends MinecraftAccount {
    private String name = "Player";

    public CrackedAccount() {
        super("Cracked");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    @Override
    public Session getSession() {
        String string = this.getName();
        byte[] byArray = this.getName().getBytes(StandardCharsets.UTF_8);
        String string2 = UUID.nameUUIDFromBytes(byArray).toString();
        return new Session(string, string2, "-", "legacy");
    }

    @Override
    public void update() {
    }

    @Override
    public void toRawJson(JsonObject json) {
        GsonExtensionKt.set(json, "name", this.getName());
    }

    @Override
    public void fromRawJson(JsonObject json) {
        String string = GsonExtensionKt.string(json, "name");
        this.setName(string);
    }
}

