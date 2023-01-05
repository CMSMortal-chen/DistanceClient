/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.manage;

import com.google.gson.JsonObject;
import me.liuli.elixir.account.CrackedAccount;
import me.liuli.elixir.account.MicrosoftAccount;
import me.liuli.elixir.account.MinecraftAccount;
import me.liuli.elixir.account.MojangAccount;
import me.liuli.elixir.utils.GsonExtensionKt;

public final class AccountSerializer {
    public static final AccountSerializer INSTANCE = new AccountSerializer();

    private AccountSerializer() {
    }

    public JsonObject toJson(MinecraftAccount account) {
        JsonObject json = new JsonObject();
        account.toRawJson(json);
        String string = account.getClass().getCanonicalName();
        GsonExtensionKt.set(json, "type", string);
        return json;
    }

    public MinecraftAccount fromJson(JsonObject json) {
        try {
            String string = GsonExtensionKt.string(json, "type");
            Object obj = Class.forName(string).newInstance();
            MinecraftAccount account = (MinecraftAccount)obj;
            account.fromRawJson(json);
            return account;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public MinecraftAccount accountInstance(String name, String password) {
        MinecraftAccount minecraftAccount;
        if (name.startsWith("ms@")) {
            String realName = name.substring(3);
            minecraftAccount = ((CharSequence)password).length() == 0 ? MicrosoftAccount.Companion.buildFromAuthCode(realName, MicrosoftAccount.AuthMethod.MICROSOFT) : MicrosoftAccount.Companion.buildFromPassword(realName, password);
        } else if (((CharSequence)password).length() == 0) {
            CrackedAccount crackedAccount;
            CrackedAccount it = crackedAccount = new CrackedAccount();
            it.setName(name);
            minecraftAccount = crackedAccount;
        } else {
            MojangAccount mojangAccount;
            MojangAccount it = mojangAccount = new MojangAccount();
            it.setName(name);
            it.setPassword(password);
            minecraftAccount = mojangAccount;
        }
        return minecraftAccount;
    }
}

