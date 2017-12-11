
package com.sc.utils.encrypt;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

import javax.crypto.Cipher;


public class RSATools {

    private static final String ENCODING = "UTF-8";

    //私钥签名
    public static String sign(String data, String pfx_path, String key_pass) {

        try {

            RSAPrivateKey pbk = getPrivateKey(pfx_path, key_pass);

            // 用私钥对信息生成数字签名
            Signature signet = Signature.getInstance("MD5withRSA");
            signet.initSign(pbk);
            signet.update(data.getBytes(ENCODING));
            byte[] signed = signet.sign(); // 对信息的数字签名
            return Base64.encodeBytes(signed);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //私钥签名2048
    public static String signSHA(String data, String pfx_path, String key_pass) {

        try {

            RSAPrivateKey pbk = getPrivateKey(pfx_path, key_pass);

            // 用私钥对信息生成数字签名
            Signature signet = Signature.getInstance("SHA256withRSA");
            signet.initSign(pbk);
            signet.update(data.getBytes(ENCODING));
            byte[] signed = signet.sign(); // 对信息的数字签名

            return Base64.encodeBytes(signed);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //公钥加密
    public static String encrypt(String data, String pub_key) {

        try {

            KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(pub_key));
            RSAPublicKey pbk = (RSAPublicKey) rsaKeyFac.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pbk);

            byte[] encDate = cipher.doFinal(data.getBytes(ENCODING));

            return Base64.encodeBytes(encDate);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //公钥加密
    public static byte[] encrypt64(String data, String pub_key) {

        try {

            KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(pub_key));
            RSAPublicKey pbk = (RSAPublicKey) rsaKeyFac.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pbk);

            byte[] encDate = cipher.doFinal(data.getBytes(ENCODING));

            return encDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //私钥解密
    public static String decrypt(String sign_msg, String pfx_path, String pfx_pass) {

        try {

            RSAPrivateKey pbk = getPrivateKey(pfx_path, pfx_pass);

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.DECRYPT_MODE, pbk);

            byte[] btSrc = cipher.doFinal(Base64.decode(sign_msg));

            return new String(btSrc, ENCODING);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //公钥验签
    public static boolean verify(String data, String pub_key, String value) {

        try {
            byte[] bts_data = Base64.decode(data);
            byte[] bts_key = Base64.decode(pub_key);

            KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bts_key);
            RSAPublicKey pbk = (RSAPublicKey) rsaKeyFac.generatePublic(keySpec);

            Signature signetcheck = Signature.getInstance("MD5withRSA");
            signetcheck.initVerify(pbk);
            signetcheck.update(value.getBytes(ENCODING));

            return signetcheck.verify(bts_data);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //公钥验签
    public static boolean verifySHA(String data, String pub_key, String value) {

        try {
            byte[] bts_data = Base64.decode(data);
            byte[] bts_key = Base64.decode(pub_key);

            KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bts_key);
            RSAPublicKey pbk = (RSAPublicKey) rsaKeyFac.generatePublic(keySpec);

            Signature signetcheck = Signature.getInstance("SHA256withRSA");
            signetcheck.initVerify(pbk);
            signetcheck.update(value.getBytes(ENCODING));

            return signetcheck.verify(bts_data);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取私钥
     * @param keyPath
     * @param passwd
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String keyPath, String passwd) throws Exception {

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(keyPath);

            char[] nPassword = null;
            if ((passwd == null) || passwd.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passwd.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();

            Enumeration enumq = ks.aliases();
            String keyAlias = null;
            if (enumq.hasMoreElements()) {
                keyAlias = (String) enumq.nextElement();
            }

            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);

            return (RSAPrivateKey) prikey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取公钥
     * @param keyPath
     * @param passwd
     * @return
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String keyPath, String passwd) throws Exception {

        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");

            FileInputStream fis = new FileInputStream(keyPath);

            char[] nPassword = null;
            if ((passwd == null) || passwd.trim().equals("")) {
                nPassword = null;
            } else {
                nPassword = passwd.toCharArray();
            }
            ks.load(fis, nPassword);
            fis.close();

            Enumeration enumq = ks.aliases();
            String keyAlias = null;
            if (enumq.hasMoreElements()) {
                keyAlias = (String) enumq.nextElement();
            }

            Certificate cert = ks.getCertificate(keyAlias);

            PublicKey pubkey = cert.getPublicKey();

            return (RSAPublicKey) pubkey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
