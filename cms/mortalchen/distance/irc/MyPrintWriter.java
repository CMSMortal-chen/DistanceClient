package cms.mortalchen.distance.irc;

import cms.mortalchen.encryption.RSA;

import java.io.*;

public class MyPrintWriter extends PrintWriter {
    public String publicKey = "";

    public MyPrintWriter(Writer out) {
        super(out);
    }

    public MyPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public MyPrintWriter(OutputStream out) {
        super(out);
    }

    public MyPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public MyPrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public MyPrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public MyPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public MyPrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void println(String x) {
        if (!publicKey.isEmpty())
            x = RSA.encryptWithPublicKey(x, publicKey);
        super.println(x);
    }
}

