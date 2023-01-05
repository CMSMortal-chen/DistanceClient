/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.account;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import cms.mortalchen.ihatekotlin.KtReaderHelper;
import me.liuli.elixir.compat.OAuthServer;
import me.liuli.elixir.compat.Session;
import me.liuli.elixir.exception.LoginException;
import me.liuli.elixir.utils.GsonExtensionKt;
import me.liuli.elixir.utils.HttpUtils;

public final class MicrosoftAccount
extends MinecraftAccount {
    public static final Companion Companion = new Companion();
    private String name = "UNKNOWN";
    private String uuid = "";
    private String accessToken = "";
    private String refreshToken = "";
    private AuthMethod authMethod = AuthMethod.MICROSOFT;
    public static final String XBOX_PRE_AUTH_URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>";
    public static final String XBOX_AUTH_URL = "https://login.live.com/oauth20_token.srf";
    public static final String XBOX_XBL_URL = "https://user.auth.xboxlive.com/user/authenticate";
    public static final String XBOX_XSTS_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    public static final String MC_AUTH_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    public static final String MC_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    public static final String XBOX_AUTH_DATA = "client_id=<client_id>&client_secret=<client_secret>&redirect_uri=<redirect_uri>&grant_type=authorization_code&code=";
    public static final String XBOX_REFRESH_DATA = "client_id=<client_id>&client_secret=<client_secret>&scope=<scope>&grant_type=refresh_token&redirect_uri=<redirect_uri>&refresh_token=";
    public static final String XBOX_XBL_DATA = "{\"Properties\":{\"AuthMethod\":\"RPS\",\"SiteName\":\"user.auth.xboxlive.com\",\"RpsTicket\":\"<rps_ticket>\"},\"RelyingParty\":\"http://auth.xboxlive.com\",\"TokenType\":\"JWT\"}";
    public static final String XBOX_XSTS_DATA = "{\"Properties\":{\"SandboxId\":\"RETAIL\",\"UserTokens\":[\"<xbl_token>\"]},\"RelyingParty\":\"rp://api.minecraftservices.com/\",\"TokenType\":\"JWT\"}";
    public static final String MC_AUTH_DATA = "{\"identityToken\":\"XBL3.0 x=<userhash>;<xsts_token>\"}";

    public MicrosoftAccount() {
        super("Microsoft");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    @Override
    public Session getSession(){
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
    public void update() throws LoginException, IOException {
        String string;
        LinkedHashMap<String,String> jsonPostHeader = new LinkedHashMap<>();

        jsonPostHeader.put("Content-Type","application/json");
        jsonPostHeader.put("Accept", "application/json");

        JsonParser jsonParser = new JsonParser();
        InputStream inputStream = HttpUtils.make$default(HttpUtils.INSTANCE, XBOX_AUTH_URL, "POST", Companion.replaceKeys(this.authMethod, XBOX_REFRESH_DATA) + this.refreshToken, Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded"), null, 16, null).getInputStream();
        JsonObject msRefreshJson = jsonParser.parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).getAsJsonObject();
        String string2 = GsonExtensionKt.string(msRefreshJson, "access_token");
        if (string2 == null) {
            throw new LoginException("Microsoft access token is null");
        }
        String string3 = GsonExtensionKt.string(msRefreshJson, "refresh_token");
        if (string3 == null) {
            throw new LoginException("Microsoft new refresh token is null");
        }
        this.refreshToken = string3;
        JsonParser jsonParser2 = new JsonParser();
        InputStream inputStream2 = HttpUtils.make$default(HttpUtils.INSTANCE, XBOX_XBL_URL, "POST", XBOX_XBL_DATA.replace("<rps_ticket>",this.authMethod.getRpsTicketRule().replace("<access_token>", string2)), jsonPostHeader, null, 16, null).getInputStream();
        JsonObject xblJson = jsonParser2.parse(new InputStreamReader(inputStream2, StandardCharsets.UTF_8)).getAsJsonObject();
        String string4 = GsonExtensionKt.string(xblJson, "Token");
        if (string4 == null) {
            throw new LoginException("Microsoft XBL token is null");
        }
        JsonObject jsonObject = GsonExtensionKt.obj(xblJson, "DisplayClaims");
        if (jsonObject == null) {
            string = null;
        } else {
            JsonArray jsonArray = GsonExtensionKt.array(jsonObject, "xui");
            if (jsonArray == null) {
                string = null;
            } else {
                JsonElement jsonElement = jsonArray.get(0);
                if (jsonElement == null) {
                    string = null;
                } else {
                    JsonObject jsonObject2 = jsonElement.getAsJsonObject();
                    string = jsonObject2 == null ? null : GsonExtensionKt.string(jsonObject2, "uhs");
                }
            }
        }
        if (string == null) {
            throw new LoginException("Microsoft XBL userhash is null");
        }
        String userhash = string;
        JsonParser jsonParser3 = new JsonParser();
        InputStream inputStream3 = HttpUtils.make$default(HttpUtils.INSTANCE, XBOX_XSTS_URL, "POST", XBOX_XSTS_DATA.replace("<xbl_token>", string4), jsonPostHeader, null, 16, null).getInputStream();
        JsonObject xstsJson = jsonParser3.parse(new InputStreamReader(inputStream3, StandardCharsets.UTF_8)).getAsJsonObject();
        String string5 = GsonExtensionKt.string(xstsJson, "Token");
        if (string5 == null) {
            throw new LoginException("Microsoft XSTS token is null");
        }
        JsonParser jsonParser4 = new JsonParser();
        InputStream inputStream4 = HttpUtils.make$default(HttpUtils.INSTANCE, MC_AUTH_URL, "POST", MC_AUTH_DATA.replace("<userhash>", userhash).replace("<xsts_token>", string5), jsonPostHeader, null, 16, null).getInputStream();
        JsonObject mcJson = jsonParser4.parse(new InputStreamReader(inputStream4, StandardCharsets.UTF_8)).getAsJsonObject();
        String string6 = GsonExtensionKt.string(mcJson, "access_token");
        if (string6 == null) {
            throw new LoginException("Minecraft access token is null");
        }
        this.accessToken = string6;
        JsonParser jsonParser5 = new JsonParser();
        InputStream inputStream5 = HttpUtils.make$default(HttpUtils.INSTANCE, MC_PROFILE_URL, "GET", "", Collections.singletonMap("Authorization", "Bearer " + this.accessToken), null, 16, null).getInputStream();
        JsonObject mcProfileJson = jsonParser5.parse(new InputStreamReader(inputStream5, StandardCharsets.UTF_8)).getAsJsonObject();
        String string7 = GsonExtensionKt.string(mcProfileJson, "name");
        if (string7 == null) {
            throw new LoginException("Minecraft account name is null");
        }
        this.setName(string7);
        String string8 = GsonExtensionKt.string(mcProfileJson, "id");
        if (string8 == null) {
            throw new LoginException("Minecraft account uuid is null");
        }
        this.uuid = string8;
    }

    @Override
    public void toRawJson(JsonObject json) {
        GsonExtensionKt.set(json, "name", this.getName());
        GsonExtensionKt.set(json, "refreshToken", this.refreshToken);
        GsonExtensionKt.set(json, "authMethod", this.authMethod.name());
    }

    @Override
    public void fromRawJson(JsonObject json) {
        AuthMethod authMethod = AuthMethod.MICROSOFT;
        MicrosoftAccount microsoftAccount = this;
        String string = GsonExtensionKt.string(json, "name");
        this.setName(string);
        this.refreshToken = GsonExtensionKt.string(json, "refreshToken");
        AuthMethod[] authMethodArray = AuthMethod.values();
        for (AuthMethod authMethod3 : authMethodArray) {
            if (!authMethod3.name().equalsIgnoreCase(GsonExtensionKt.string(json, "authMethod"))) {
                authMethod = authMethod3;
            }
        }
        microsoftAccount.authMethod = authMethod;
    }

    public static final class Companion {
        private Companion() {
        }

        public MicrosoftAccount buildFromAuthCode(String code, AuthMethod method) {
            try {
                JsonParser jsonParser = new JsonParser();
                InputStream object = HttpUtils.make$default(HttpUtils.INSTANCE, MicrosoftAccount.XBOX_AUTH_URL, "POST", this.replaceKeys(method, MicrosoftAccount.XBOX_AUTH_DATA) + code, Collections.singletonMap("Content-Type", "application/x-www-form-urlencoded"), null, 16, null).getInputStream();
                JsonObject data = jsonParser.parse(new InputStreamReader(object, StandardCharsets.UTF_8)).getAsJsonObject();
                if (!data.has("refresh_token")) {
                    throw new LoginException("Failed to get refresh token");
                }
                MicrosoftAccount it = new MicrosoftAccount();
                it.refreshToken = GsonExtensionKt.string(data, "refresh_token");
                it.authMethod = method;
                it.update();
                return it;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public MicrosoftAccount buildFromPassword(String username, String password) {
            try {
                AuthMethod method = AuthMethod.MICROSOFT;
                HttpURLConnection preAuthConnection = HttpUtils.make$default(HttpUtils.INSTANCE, this.replaceKeys(method, MicrosoftAccount.XBOX_PRE_AUTH_URL), "GET", null, null, null, 28, null);
                InputStream inputStream = preAuthConnection.getInputStream();
                Charset charset = StandardCharsets.UTF_8;
                String html = KtReaderHelper.readText(new InputStreamReader(inputStream, charset));
                List<String> list = preAuthConnection.getHeaderFields().get("Set-Cookie");

                StringBuilder stringBuilder = new StringBuilder();

                for (String s : list) {
                    stringBuilder.append(s).append(";");
                }

                String cookies = stringBuilder.toString();
                String urlPost = buildFromPassword$findArgs(html, "urlPost");
                String it = buildFromPassword$findArgs(html, "sFTTag");
                String ppft = it.substring(it.indexOf("value=\"") + 7, it.length() - 3);
                preAuthConnection.disconnect();

                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

                linkedHashMap.put("Cookie", cookies);
                linkedHashMap.put("Content-Type", "application/x-www-form-urlencoded");

                HttpURLConnection authConnection = HttpUtils.make(urlPost, "POST",
                        "login=" + username + "&loginfmt=" + username + "&passwd=" + password + "&PPFT=" + ppft,
                        linkedHashMap, HttpUtils.DEFAULT_AGENT);

                KtReaderHelper.readText(new InputStreamReader(authConnection.getInputStream(),StandardCharsets.UTF_8));

                String code1 = authConnection.getURL().toString();

                if (!code1.contains("code=")) {
                    throw new LoginException("Failed to get auth code from response");
                }
                String code = code1.substring(code1.indexOf("code=") + 5);
                code = code.substring(0, code.indexOf("&"));
                authConnection.disconnect();
                return this.buildFromAuthCode(code, method);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public OAuthServer buildFromOpenBrowser(OAuthHandler handler) {
            OAuthServer oAuthServer = new OAuthServer(handler);
            oAuthServer.start();
            return oAuthServer;
        }

        public String replaceKeys(AuthMethod method, String string) {
            return string.replace("<client_id>", method.getClientId()).replace("<client_secret>", method.getClientSecret()).replace("<redirect_uri>", method.getRedirectUri()).replace("<scope>", method.getScope());
        }

        private static String buildFromPassword$findArgs(String resp, String arg) throws LoginException {
            if (!resp.contains(arg)) {
                throw new LoginException("Failed to find argument in response " + arg);
            }
            String string3 = resp.substring(resp.indexOf("$arg:'") + arg.length() + 2);
            return string3.substring(0, string3.indexOf("',"));
        }
    }

    public enum AuthMethod {
        MICROSOFT("00000000441cc96b", "", "https://login.live.com/oauth20_desktop.srf", "service::user.auth.xboxlive.com::MBI_SSL", "<access_token>"),
        AZURE_APP("c6cd7b0f-077d-4fcf-ab5c-9659576e38cb", "vI87Q~GkhVHJSLN5WKBbEKbK0TJc9YRDyOYc5", "http://localhost:1919/login", "XboxLive.signin%20offline_access", "d=<access_token>");

        private final String clientId;
        private final String clientSecret;
        private final String redirectUri;
        private final String scope;
        private final String rpsTicketRule;

        AuthMethod(String clientId, String clientSecret, String redirectUri, String scope, String rpsTicketRule) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.redirectUri = redirectUri;
            this.scope = scope;
            this.rpsTicketRule = rpsTicketRule;
        }

        public final String getClientId() {
            return this.clientId;
        }

        public final String getClientSecret() {
            return this.clientSecret;
        }

        public final String getRedirectUri() {
            return this.redirectUri;
        }

        public final String getScope() {
            return this.scope;
        }

        public final String getRpsTicketRule() {
            return this.rpsTicketRule;
        }
    }

    public interface OAuthHandler {
        void openUrl(String var1);

        void authResult(MicrosoftAccount var1);

        void authError(String var1);
    }
}

