package com.horqian.api.util;


import cn.hutool.core.codec.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;


/**
 * @author bz
 * @date 2022/08/31
 * @description
 */
@Component
@RequiredArgsConstructor
public class RSAUtils3 {

    private final static String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAocrZPyv7qYi49R/1bPtPSy+SdPwMqn0qkL99LVoS1+z9AQTzjpfrGgWBe9iEObbmW2yYWRqtFGuPPg3GkXvlyofCqu8jFDRsw/NEBvrdxME6FpQyfgcsz/CpVv4BlTeZLauWeWUfjl92guvo9Mw8Bz0NcRbjD8Kl+OSwjC09YB5t5n83f86Yb0o5yLUNkFTfJhGKZkQja9JgaNVo3V/PwQefvg9SGSdgjwrgWO9ANuEYbPsEHIsqMeUyUqBxt7MQoQs3NsMnN3nnU+/9rgQmeNy2JQs3n/dPJEpcA29zEfnqc1KBVonmBY9M/j9zQ82MeQBwQarGm7OLDxNSw5b/kQIDAQAB";

    private final static String privateKeyString = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQChytk/K/upiLj1H/Vs+09LL5J0/AyqfSqQv30tWhLX7P0BBPOOl+saBYF72IQ5tuZbbJhZGq0Ua48+DcaRe+XKh8Kq7yMUNGzD80QG+t3EwToWlDJ+ByzP8KlW/gGVN5ktq5Z5ZR+OX3aC6+j0zDwHPQ1xFuMPwqX45LCMLT1gHm3mfzd/zphvSjnItQ2QVN8mEYpmRCNr0mBo1WjdX8/BB5++D1IZJ2CPCuBY70A24Rhs+wQciyox5TJSoHG3sxChCzc2wyc3eedT7/2uBCZ43LYlCzef908kSlwDb3MR+epzUoFWieYFj0z+P3NDzYx5AHBBqsabs4sPE1LDlv+RAgMBAAECggEAEwJWTAnK47xgnsxGdauBQYAxYPVMN6vFGISnHHD8zcnWYAxI4XQP91q2P+rMfkty/ULhE3cC5udMSSotLmK0OPuCb7rxECqorM0kLP3EmJ1rScPXut708OmBu+rUHMgPg0Ipx9GNx5iHwBIfqMNmOiN+NM9PlkZ0Ya3c4FtgXq6EXanj3uXHfPBV7N2yCVka4FxxI8kcl/jlEplXIFyRsD2SS1U52pc4Cab9hU3rSCVM1FJPfquOWfnBO0tm+YdgceciiZ/ecn5GrmABlch2hsr4LKaIbC1o1jhp4BhChzfijCQueM4099fiJ2V0cXJRBG9/qbIR1BHveE7jPd+SwQKBgQDnCwqVFH+P6T4bLb2aUsFgvOkUB8d3Epv8WgL/n3Mc0r1vpu74tak2FdfMxCQjiJ19r2ICXAusbYzd//iXUitvpw/ckOJKPIvoBY0erS7ZU1feUv5scjPlRrrBFzvFHXRCeKIic4RJhGcPLTq1IYnXY5GoFa6v6nN0aUOnj+ILTQKBgQCzRNbcYBU81IP6hqHXz33PN9ewehLNjyfcS4RKVRMQZM5K7HQXbuTgov1gkib8j+x3Ld4IgG4JOCxWSGUEp8/oeyBPUZ4Z8VSfzMR8ImksMMXodN56f59XIGlvSTjHX38UwwJ4QSd2zRCA3apRHvslw+UBDPDu/bnJcM3SaGi7VQKBgQC7h4C9ZHftjTm1wbilrAn9xRfCBFZoVZh6iWzporhCauML3wkUog6IMc6JC6LxnI+IVnRMyf1HpVuzTXKvECjlZUpAkvC5pM9CMk9K2PKvvzAQ6nhL+Z4ZuQEYfQ0sfy3ATj37jJuROICWFvYR0vkY9F5jecXiT4AKuu4I8YZd+QKBgQCUYNIGLXDmnG9wmKa0XYUgUnQwGZoekfLIH6krbScOVqF42iVsj9jZJY7as52ZRfbO3iP8nXB2KQxpjhoutEcdgWvIuyFFD76fVXj71e9/KA9my/SpJ6DXGHisJ8rbGmuw34fXyPXFGw4h21bxNddh9rZBHFj/NvrjhH6XPNwS0QKBgQCVgRGcVV6eqlxxDGorAGj6qav15zedldGjAtQiS9MRybHuwo9UWE4aCHh6ytO2AkRlMETYgc/oFJB6pUnoTZuC0gGr48fKF5xnEajP7wZVBsWUCvFkpUc7udfJY7ZcRmjOILdMHfICUfaQw4eO4N4ChJZngDjlTZWTbc9vTLqYbQ==";

    public HashMap<String, String> getKey() throws Exception {
        return generateKeyToRedis();
    }
    /**
     * 加密
     */
    public static String encode(String idcard){
        String rsa = "";
        try {
            PublicKey pub = loadPublicKeyFromString("RSA", publicKeyString);
            rsa = encrypt("RSA", idcard, pub, idcard.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsa;
    }

    /**
     * 解密
     */
    public static String decode(String rsaString){
        String idcard = "";
        try {
            PrivateKey pri = loadPrivateKeyFromString("RSA", privateKeyString);
            idcard = decrypt("RSA", rsaString, pri, rsaString.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idcard;
    }

    /**
     * 生成密钥对并保存在本地文件中
     *
     * @throws Exception
     */
    private static HashMap<String, String> generateKeyToRedis() throws Exception {
        HashMap<String, String> map = new HashMap<>();
        // 获取密钥对生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 获取密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥
        PublicKey publicKey = keyPair.getPublic();
        // 获取私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 获取byte数组
        byte[] publicKeyEncoded = publicKey.getEncoded();
        byte[] privateKeyEncoded = privateKey.getEncoded();
        // 进行Base64编码
        String publicKeyString = Base64.encode(publicKeyEncoded);
        String privateKeyString = Base64.encode(privateKeyEncoded);
        map.put("publicKeyString", publicKeyString);
        map.put("privateKeyString", privateKeyString);
        return map;
//        // 保存文件
//        FileUtils.writeStringToFile(new File(pubPath), publicKeyString, Charset.forName("UTF-8"));
//        FileUtils.writeStringToFile(new File(priPath), privateKeyString, Charset.forName("UTF-8"));

    }

//    /**
//     * 从文件中加载公钥
//     *
//     * @param algorithm : 算法
//     * @param filePath  : 文件路径
//     * @return : 公钥
//     * @throws Exception
//     */
//    private PublicKey loadPublicKeyFromFile(String algorithm, String filePath) throws Exception {
//        // 将文件内容转为字符串
//        String keyString = FileUtils.readFileToString(new File(filePath), Charset.forName("UTF-8"));
//
//        return loadPublicKeyFromString(algorithm, keyString);
//
//    }

    /**
     * 从字符串中加载公钥
     *
     * @param algorithm : 算法
     * @param keyString : 公钥字符串
     * @return : 公钥
     * @throws Exception
     */
    private static PublicKey loadPublicKeyFromString(String algorithm, String keyString) throws Exception {
        // 进行Base64解码
        byte[] decode = Base64.decode(keyString);
        // 获取密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        // 构建密钥规范
        X509EncodedKeySpec keyspec = new X509EncodedKeySpec(decode);
        // 获取公钥
        return keyFactory.generatePublic(keyspec);

    }

//    /**
//     * 从文件中加载私钥
//     *
//     * @param algorithm : 算法
//     * @param filePath  : 文件路径
//     * @return : 私钥
//     * @throws Exception
//     */
//    private static PrivateKey loadPrivateKeyFromFile(String algorithm, String filePath) throws Exception {
//        // 将文件内容转为字符串
//        String keyString = FileUtils.readFileToString(new File(filePath), Charset.forName("UTF-8"));
//        return loadPrivateKeyFromString(algorithm, keyString);
//
//    }

    /**
     * 从字符串中加载私钥
     *
     * @param algorithm : 算法
     * @param keyString : 私钥字符串
     * @return : 私钥
     * @throws Exception
     */
    private static PrivateKey loadPrivateKeyFromString(String algorithm, String keyString) throws Exception {
        // 进行Base64解码
        byte[] decode = Base64.decode(keyString);
        // 获取密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        // 构建密钥规范
        PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(decode);
        // 生成私钥
        return keyFactory.generatePrivate(keyspec);

    }

    /**
     * 使用密钥加密数据
     *
     * @param algorithm      : 算法
     * @param input          : 原文
     * @param key            : 密钥
     * @param maxEncryptSize : 最大加密长度(需要根据实际情况进行调整)
     * @return : 密文
     * @throws Exception
     */
    private static String encrypt(String algorithm, String input, Key key, int maxEncryptSize) throws Exception {
        // 获取Cipher对象
        Cipher cipher = Cipher.getInstance(algorithm);
        // 初始化模式(加密)和密钥
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 将原文转为byte数组
        byte[] data = input.getBytes(StandardCharsets.UTF_8);
        // 总数据长度
        int total = data.length;
        // 输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        decodeByte(maxEncryptSize, cipher, data, total, baos);
        // 对密文进行Base64编码
        return Base64.encode(baos.toByteArray());

    }

    /**
     * 解密数据
     *
     * @param algorithm      : 算法
     * @param encrypted      : 密文
     * @param key            : 密钥
     * @param maxDecryptSize : 最大解密长度(需要根据实际情况进行调整)
     * @return : 原文
     * @throws Exception
     */
    private static String decrypt(String algorithm, String encrypted, Key key, int maxDecryptSize) throws Exception {
        // 获取Cipher对象
        Cipher cipher = Cipher.getInstance(algorithm);
        // 初始化模式(解密)和密钥
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 由于密文进行了Base64编码, 在这里需要进行解码
        byte[] data = Base64.decode(encrypted);
        // 总数据长度
        int total = data.length;
        // 输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        decodeByte(maxDecryptSize, cipher, data, total, baos);
        // 输出原文
        return baos.toString();

    }

    /**
     * 分段处理数据
     *
     * @param maxSize : 最大处理能力
     * @param cipher  : Cipher对象
     * @param data    : 要处理的byte数组
     * @param total   : 总数据长度
     * @param baos    : 输出流
     * @throws Exception
     */
    private static void decodeByte(int maxSize, Cipher cipher, byte[] data, int total, ByteArrayOutputStream baos) throws Exception {
        // 偏移量
        int offset = 0;
        // 缓冲区
        byte[] buffer;
        // 如果数据没有处理完, 就一直继续
        while (total - offset > 0) {
            // 如果剩余的数据 >= 最大处理能力, 就按照最大处理能力来加密数据
            if (total - offset >= maxSize) {
                // 加密数据
                buffer = cipher.doFinal(data, offset, maxSize);
                // 偏移量向右侧偏移最大数据能力个
                offset += maxSize;
            } else {
                // 如果剩余的数据 < 最大处理能力, 就按照剩余的个数来加密数据
                buffer = cipher.doFinal(data, offset, total - offset);
                // 偏移量设置为总数据长度, 这样可以跳出循环
                offset = total;
            }
            // 向输出流写入数据
            baos.write(buffer);
        }
    }

}
