/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.MapsKt
 *  kotlin.io.TextStreamsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Charsets
 *  org.jetbrains.annotations.NotNull
 */
package me.liuli.elixir.utils;

import cms.mortalchen.ihatekotlin.KtReaderHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class HttpUtils {
    public static final HttpUtils INSTANCE = new HttpUtils();
    public static final String DEFAULT_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    private HttpUtils() {
    }

    public static HttpURLConnection make(String url, String method, String data, Map<String, String> header, String agent) {
        try {

            URLConnection uRLConnection = new URL(url).openConnection();
            if (uRLConnection == null) {
                throw new NullPointerException("null cannot be cast to non-null type java.net.HttpURLConnection");
            }
            HttpURLConnection httpConnection = (HttpURLConnection) uRLConnection;
            httpConnection.setRequestMethod(method);
            httpConnection.setConnectTimeout(2000);
            httpConnection.setReadTimeout(10000);
            httpConnection.setRequestProperty("User-Agent", agent);
            for (Map.Entry<String, String> stringStringEntry : header.entrySet()) {
                Map.Entry<String, String> element$iv;
                Map.Entry<String, String> $dstr$key$value = element$iv = stringStringEntry;
                boolean bl = false;
                String key = $dstr$key$value.getKey();
                String value = $dstr$key$value.getValue();
                httpConnection.setRequestProperty(key, value);
            }
            httpConnection.setInstanceFollowRedirects(true);
            httpConnection.setDoOutput(true);
            if (((CharSequence) data).length() > 0) {
                DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                dataOutputStream.writeBytes(data);
                dataOutputStream.flush();
            }
            httpConnection.connect();
            return httpConnection;
        }catch (Exception e){
            return null;
        }
    }

    public static HttpURLConnection make$default(HttpUtils httpUtils, String string, String string2, String string3, Map map, String string4, int n, Object object) {
        if ((n & 4) != 0) {
            string3 = "";
        }
        if ((n & 8) != 0) {
            map = new LinkedHashMap<>();
        }
        if ((n & 0x10) != 0) {
            string4 = DEFAULT_AGENT;
        }
        return make(string, string2, string3, map, string4);
    }

    public String request(String url, String method, String data, Map<String, String> header, String agent){
        try {
            HttpURLConnection connection = make(url, method, data, header, agent);
            InputStream inputStream = connection.getInputStream();
            Charset charset = StandardCharsets.UTF_8;
            return KtReaderHelper.readText(new InputStreamReader(inputStream, charset));
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public String get(String url, Map<String, String> header) {
        return this.request(url, "GET", null, header, DEFAULT_AGENT);
    }


    public String post(String url, String data, Map<String, String> header) {
        return this.request(url, "POST", data, header, DEFAULT_AGENT);
    }

}

