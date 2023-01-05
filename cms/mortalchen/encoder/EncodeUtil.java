package cms.mortalchen.encoder;

import LemonObfAnnotation.ObfuscationClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@ObfuscationClass
public class EncodeUtil {

    /**
     * MD5加密
     * @param source 原文
     * @return 编码后的字符串
     * @throws Exception 找不到加密方式抛出异常
     */
    public static String MD5encode(String source) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] bytes = source.getBytes();
        byte[] targetBytes = digest.digest(bytes);
        char[] characters = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder builder = new StringBuilder();
        for (byte b : targetBytes) {
            int high = (b >> 4) & 15;
            int low = b & 15;
            char highChar = characters[high];
            char lowChar = characters[low];

            builder.append(highChar).append(lowChar);
        }

        return builder.toString();
    }
    /**
     * Sha384加密
     * @param source 原文
     * @return 编码后的字符串
     * @throws Exception 找不到加密方式抛出异常
     */
    public static String SHA256encode(String source) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-384");
        StringBuilder builder = new StringBuilder();
        digest.update(source.getBytes(StandardCharsets.UTF_8));
        return byte2Hex(digest.digest());
    }

    /**
     * Byte转Hex
     * @param bytes 输入byte
     * @return 输出Hex
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuilder stringBuffer = new StringBuilder();
        String temp = null;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
}
