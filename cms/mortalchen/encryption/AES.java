package cms.mortalchen.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES加密字符串
     *
     * @param content 需要被加密的字符串
     * @param key     加密需要的密码
     * @return 密文
     */
    public static String AESencrypt(String content, String key) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

            return Base64.getEncoder().encodeToString(result);//通过Base64转码返回

        } catch (Exception e) {
            System.err.println("ENCODE FAILED!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return 密钥
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg;

        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);

            //AES 要求密钥长度为 128
            kg.init(256, new SecureRandom(key.getBytes(StandardCharsets.UTF_8)));

            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException e) {
            System.err.println("GEN KEY FAILED!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密AES加密过的字符串
     *
     * @param content AES加密过过的内容
     * @param key     加密时的密码
     * @return 明文
     */
    public static String AESdecrypt(String content, String key) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));

            //执行操作
            byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));


            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("DECODE FAILED!");
            e.printStackTrace();
        }
        return null;
    }
}
