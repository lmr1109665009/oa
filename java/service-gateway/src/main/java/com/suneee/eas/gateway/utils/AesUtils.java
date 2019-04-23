package com.suneee.eas.gateway.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AesUtils
{
    private static final Logger logger = LoggerFactory
            .getLogger(AesUtils.class);

    /**
     * 加密
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     * @return: String
     */
    public static String Encrypt(String sSrc, String sKey) throws Exception
    {
        if (sKey == null)
        {
            logger.error("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16)
        {
            logger.error("Key长度不是16位");
            return null;
        }

        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// "算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new Base64().encode(encrypted).toString();// 此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

    /**
     * 解密
     * @param sSrc
     * @param sKey
     * @throws Exception
     * @return: String
     */
    public static String Decrypt(String sSrc, String sKey) throws Exception
    {
        try
        {
            // 判断Key是否正确
            if (sKey == null)
            {
                logger.error("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16)
            {
                logger.error("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc.getBytes());// 先用base64解密
            try
            {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e)
            {
                logger.error(e.getMessage());
                return null;
            }
        } catch (Exception ex)
        {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /*public static void main(String[] args) throws Exception {
        
         //此处使用AES-128-ECB加密模式，key需要为16位。
         
        String cKey = "1234567890123456";
        // 需要加密的字串
        String cSrc = "www.gowhere.so";
        System.out.println(cSrc);
        // 加密
        String enString = AesUtils.Encrypt(cSrc, cKey);
        System.out.println("加密后的字串是：" + enString);
 
        // 解密
        String DeString = AesUtils.Decrypt(enString, cKey);
        System.out.println("解密后的字串是：" + DeString);
    }
*/
    
}
