package cms.mortalchen.distance.irc;

import cms.mortalchen.encryption.RSA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class MyBufferedReader extends BufferedReader {
    public String privateKey = "";

    public MyBufferedReader(Reader in, int sz) {
        super(in, sz);
    }

    public MyBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        String msg = super.readLine();
        msg = this.cleanStr(msg);
        if (!privateKey.isEmpty())
            msg = RSA.decryptWithPrivateKey(msg, privateKey);
        msg = this.cleanStr(msg);
        return msg;

    }

    public String cleanStr(String str) {
        try {
            str = str.replaceAll("\n", "");
            str = str.replaceAll("\r", "");
            str = str.replaceAll("\t", "");
        } catch (Exception ignored) {
        }
        return str;
    }
}
