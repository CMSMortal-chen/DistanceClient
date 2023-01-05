package cms.mortalchen.encryption;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RSA {
    /**
     * RSA算法
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 服务端的公钥
     */
    public static String SERVER_PUBLIC_KEY = "RSAPublicKey";

    /**
     * 客户端的公钥
     */
    public static String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 客户端的私钥
     */
    public static String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 生成密钥对
     */
    public static void genKey() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            PRIVATE_KEY = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            PUBLIC_KEY = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (Exception e) {
            System.out.println("RSAError:");
            e.printStackTrace();
        }
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     */
    public static String encryptWithPublicKey(String str, String publicKey) {
        try {
            //base64编码的公钥
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));

            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] inputArray = str.getBytes(StandardCharsets.UTF_8);
            int inputLength = inputArray.length;
            // 标识
            int offSet = 0;
            byte[] resultBytes = {};
            byte[] cache;
            while (inputLength - offSet > 0) {
                if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
                    offSet += MAX_ENCRYPT_BLOCK;
                } else {
                    cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                    offSet = inputLength;
                }
                resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
                System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            }
            //RSA加密
            return new String(Base64.getEncoder().encode(resultBytes), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("RSA ENCODE ERROR");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     */
    public static String decryptWithPrivateKey(String str, String privateKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
            //base64编码的私钥
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            int inputLen = inputByte.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(inputByte, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(inputByte, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("RSA DECODE ERROR");
            e.printStackTrace();
            return null;
        }
    }


}
