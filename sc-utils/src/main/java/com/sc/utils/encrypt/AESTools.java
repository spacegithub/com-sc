package com.sc.utils.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESTools {

    private String key="N30QBkY6eEb554S7";

    private String initVector="2048799189696608";
    private static final Logger logger = LoggerFactory.getLogger(AESTools.class);
    private static ApplicationContext applicationContext;
    public String encrypt(String value) {
        try {
            if(StringUtils.isEmpty(value)){
                return null;
            }
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            logger.error("调用SecretFactory加密失败,失败原因:"+ex.getMessage(),ex);
        }

        return null;
    }

    public String decrypt(String encrypted) {
        try {
            if(StringUtils.isEmpty(encrypted)){
                return null;
            }
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            logger.error("调用SecretFactory解密失败,失败原因:"+ex.getMessage(),ex);
            return encrypted;
        }
    }

    public static void main(String[] args) {
        AESTools factory=new AESTools();
        System.out.println("-->" +factory.decrypt("LBOQ8hgIjBMUf/tT41catO5eTcsMC7qpKc/e8wTMadA="));
    }
}
