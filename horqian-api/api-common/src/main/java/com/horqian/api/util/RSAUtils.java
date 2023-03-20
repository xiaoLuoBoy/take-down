package com.horqian.api.util;


import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;



/**
 * @author bz
 * @date 2022/08/31
 * @description
 */
@Component
@RequiredArgsConstructor
public class RSAUtils {

    private final static String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAocrZPyv7qYi49R/1bPtPSy+SdPwMqn0qkL99LVoS1+z9AQTzjpfrGgWBe9iEObbmW2yYWRqtFGuPPg3GkXvlyofCqu8jFDRsw/NEBvrdxME6FpQyfgcsz/CpVv4BlTeZLauWeWUfjl92guvo9Mw8Bz0NcRbjD8Kl+OSwjC09YB5t5n83f86Yb0o5yLUNkFTfJhGKZkQja9JgaNVo3V/PwQefvg9SGSdgjwrgWO9ANuEYbPsEHIsqMeUyUqBxt7MQoQs3NsMnN3nnU+/9rgQmeNy2JQs3n/dPJEpcA29zEfnqc1KBVonmBY9M/j9zQ82MeQBwQarGm7OLDxNSw5b/kQIDAQAB";

    private final static String privateKeyString = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQChytk/K/upiLj1H/Vs+09LL5J0/AyqfSqQv30tWhLX7P0BBPOOl+saBYF72IQ5tuZbbJhZGq0Ua48+DcaRe+XKh8Kq7yMUNGzD80QG+t3EwToWlDJ+ByzP8KlW/gGVN5ktq5Z5ZR+OX3aC6+j0zDwHPQ1xFuMPwqX45LCMLT1gHm3mfzd/zphvSjnItQ2QVN8mEYpmRCNr0mBo1WjdX8/BB5++D1IZJ2CPCuBY70A24Rhs+wQciyox5TJSoHG3sxChCzc2wyc3eedT7/2uBCZ43LYlCzef908kSlwDb3MR+epzUoFWieYFj0z+P3NDzYx5AHBBqsabs4sPE1LDlv+RAgMBAAECggEAEwJWTAnK47xgnsxGdauBQYAxYPVMN6vFGISnHHD8zcnWYAxI4XQP91q2P+rMfkty/ULhE3cC5udMSSotLmK0OPuCb7rxECqorM0kLP3EmJ1rScPXut708OmBu+rUHMgPg0Ipx9GNx5iHwBIfqMNmOiN+NM9PlkZ0Ya3c4FtgXq6EXanj3uXHfPBV7N2yCVka4FxxI8kcl/jlEplXIFyRsD2SS1U52pc4Cab9hU3rSCVM1FJPfquOWfnBO0tm+YdgceciiZ/ecn5GrmABlch2hsr4LKaIbC1o1jhp4BhChzfijCQueM4099fiJ2V0cXJRBG9/qbIR1BHveE7jPd+SwQKBgQDnCwqVFH+P6T4bLb2aUsFgvOkUB8d3Epv8WgL/n3Mc0r1vpu74tak2FdfMxCQjiJ19r2ICXAusbYzd//iXUitvpw/ckOJKPIvoBY0erS7ZU1feUv5scjPlRrrBFzvFHXRCeKIic4RJhGcPLTq1IYnXY5GoFa6v6nN0aUOnj+ILTQKBgQCzRNbcYBU81IP6hqHXz33PN9ewehLNjyfcS4RKVRMQZM5K7HQXbuTgov1gkib8j+x3Ld4IgG4JOCxWSGUEp8/oeyBPUZ4Z8VSfzMR8ImksMMXodN56f59XIGlvSTjHX38UwwJ4QSd2zRCA3apRHvslw+UBDPDu/bnJcM3SaGi7VQKBgQC7h4C9ZHftjTm1wbilrAn9xRfCBFZoVZh6iWzporhCauML3wkUog6IMc6JC6LxnI+IVnRMyf1HpVuzTXKvECjlZUpAkvC5pM9CMk9K2PKvvzAQ6nhL+Z4ZuQEYfQ0sfy3ATj37jJuROICWFvYR0vkY9F5jecXiT4AKuu4I8YZd+QKBgQCUYNIGLXDmnG9wmKa0XYUgUnQwGZoekfLIH6krbScOVqF42iVsj9jZJY7as52ZRfbO3iP8nXB2KQxpjhoutEcdgWvIuyFFD76fVXj71e9/KA9my/SpJ6DXGHisJ8rbGmuw34fXyPXFGw4h21bxNddh9rZBHFj/NvrjhH6XPNwS0QKBgQCVgRGcVV6eqlxxDGorAGj6qav15zedldGjAtQiS9MRybHuwo9UWE4aCHh6ytO2AkRlMETYgc/oFJB6pUnoTZuC0gGr48fKF5xnEajP7wZVBsWUCvFkpUc7udfJY7ZcRmjOILdMHfICUfaQw4eO4N4ChJZngDjlTZWTbc9vTLqYbQ==";

    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
//    public static void main(String[] args) throws Exception {
//        //生成公钥和私钥
//        genKeyPair();
//        //加密字符串
//
//        System.out.println("随机生成的公钥为：" + keyMap.get(0));
//        System.out.println("随机生成的私钥为：" + keyMap.get(1));
//
//        String message = "Hello，这是明文！！！";
//        System.out.println("原文为：" + message);
//
//        //System.out.println(java.net.URLDecode(message));
//
//        String messageEn = encrypt(message);
//        System.out.println("加密后的字符串为：" + messageEn);
//
//        String messageDe = decrypt(messageEn);
//        System.out.println("解密后的字符串为：" + messageDe);
//    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt(String str) {
        String outStr = "";
        try {
            //base64编码的公钥
            byte[] decoded = Base64.decodeBase64(publicKeyString.getBytes(StandardCharsets.UTF_8));
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8"))).toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @return 明文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str){
        String outStr = "";
        if (StringUtils.hasText(str))
            try {
                //64位解码加密后的字符串
                byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
                //base64编码的私钥
                byte[] decoded = Base64.decodeBase64(privateKeyString.getBytes(StandardCharsets.UTF_8));
                RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
                //RSA解密
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, priKey);
                outStr = new String(cipher.doFinal(inputByte), "UTF-8");
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(str);
            }

        return outStr;
    }


}
