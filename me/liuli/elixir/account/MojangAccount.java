/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.Agent
 *  com.mojang.authlib.UserAuthentication
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.exceptions.AuthenticationUnavailableException
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.account;

import com.google.gson.JsonObject;
import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import java.net.Proxy;
import me.liuli.elixir.compat.Session;
import me.liuli.elixir.exception.LoginException;
import me.liuli.elixir.utils.GsonExtensionKt;

public final class MojangAccount
extends MinecraftAccount {
    private String name = "";
    private String password = "";
    private String uuid = "";
    private String accessToken = "";

    public MojangAccount() {
        super("Mojang");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String string) {
        this.password = string;
    }

    @Override
    public Session getSession() {
        if (((CharSequence)this.uuid).length() == 0 || ((CharSequence)this.accessToken).length() == 0) {
            try {
                this.update();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return new Session(this.getName(), this.uuid, this.accessToken, "mojang");
    }

    @Override
    public void update() throws LoginException {
        UserAuthentication userAuthentication = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        if (userAuthentication == null) {
            throw new NullPointerException("null cannot be cast to non-null type com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication");
        }
        YggdrasilUserAuthentication userAuthentication2 = (YggdrasilUserAuthentication)userAuthentication;
        userAuthentication2.setUsername(this.getName());
        userAuthentication2.setPassword(this.password);
        try {
            userAuthentication2.logIn();
            String string = userAuthentication2.getSelectedProfile().getName();
            this.setName(string);
            string = userAuthentication2.getSelectedProfile().getId().toString();
            this.uuid = string;
            string = userAuthentication2.getAuthenticatedToken();
            this.accessToken = string;
        }
        catch (AuthenticationUnavailableException exception) {
            throw new LoginException("Mojang server is unavailable");
        }
        catch (AuthenticationException exception) {
            String string = exception.getMessage();
            if (string == null) {
                string = "Unknown error";
            }
            throw new LoginException(string);
        }
    }

    @Override
    public void toRawJson(JsonObject json) {
        GsonExtensionKt.set(json, "name", this.getName());
        GsonExtensionKt.set(json, "password", this.password);
    }

    @Override
    public void fromRawJson(JsonObject json) {
        String string = GsonExtensionKt.string(json, "name");
        this.setName(string);
        String string2 = GsonExtensionKt.string(json, "password");
        this.password = string2;
    }
}

