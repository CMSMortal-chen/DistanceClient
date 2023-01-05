/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.compat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.liuli.elixir.account.MicrosoftAccount;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public final class OAuthServer {
    private final MicrosoftAccount.OAuthHandler handler;
    private final HttpServer httpServer;
    private final ThreadPoolExecutor threadPoolExecutor;

    public OAuthServer(MicrosoftAccount.OAuthHandler handler) {
        HttpServer httpServer1;
        this.handler = handler;
        try {
            httpServer1 = HttpServer.create(new InetSocketAddress("localhost", 1919), 0);
        }catch (Exception e){
            httpServer1 = null;
            e.printStackTrace();
        }
        this.httpServer = httpServer1;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        this.threadPoolExecutor = (ThreadPoolExecutor)executorService;
    }

    public MicrosoftAccount.OAuthHandler getHandler() {
        return this.handler;
    }

    public void start() {
        this.httpServer.createContext("/login", new OAuthHttpHandler(this));
        this.httpServer.setExecutor(this.threadPoolExecutor);
        this.httpServer.start();
        this.handler.openUrl(MicrosoftAccount.Companion.replaceKeys(MicrosoftAccount.AuthMethod.AZURE_APP, "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>"));
    }

    public void stop(boolean isInterrupt) {
        this.httpServer.stop(0);
        this.threadPoolExecutor.shutdown();
        if (isInterrupt) {
            this.handler.authError("Has been interrupted");
        }
    }

    public static /* synthetic */ void stop$default(OAuthServer oAuthServer, boolean bl, int n, Object object) {
        if ((n & 1) != 0) {
            bl = true;
        }
        oAuthServer.stop(bl);
    }

    public static final class OAuthHttpHandler
    implements HttpHandler {
        private final OAuthServer server;

        public OAuthHttpHandler(OAuthServer server) {
            this.server = server;
        }

        /*
         * WARNING - void declaration
         */
        @Override
        public void handle(HttpExchange exchange) {
            HashMap<String,String> query = new HashMap<>();

            String[] splited = exchange.getRequestURI().getQuery().split("&");

            for (String value : splited){
                String[] values = value.split("=");
                query.put(values[0],values[1]);
            }

            if(query.containsKey("code")) {
                try {
                    server.handler.authResult(MicrosoftAccount.Companion.buildFromAuthCode(query.get("code"), MicrosoftAccount.AuthMethod.AZURE_APP));
                    response(exchange, "Login Success", 200);
                } catch (Exception e) {
                    server.handler.authError(e.toString());
                    response(exchange, "Error: "+ e, 500);
                }
            } else {
                server.handler.authError("No code in the query");
                response(exchange, "No code in the query", 500);
            }
            server.stop(false);
        }

        private void response(HttpExchange exchange, String message, int code) {
            try {
                byte[] byArray = message.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(code, byArray.length);
                exchange.getResponseBody().write(byArray);
                exchange.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

