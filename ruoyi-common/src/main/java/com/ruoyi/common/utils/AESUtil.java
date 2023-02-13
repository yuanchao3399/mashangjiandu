package com.ruoyi.common.utils;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    private final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    private static final String key = "d32fb8b2df7746c9bbabd39b52e3344d";

    /**
     * 字符串加密
     *
     * @param str 待加密字符串
     * @return 加密结果
     */
    public static String encrypt(String str) {
        return encryptByECB(str, key);
    }

    /**
     * 字符串解密
     *
     * @param str 待解密内容
     * @return 解密结果
     */
    public static String decrypt(String str) {
        return decryptByECB(str, key);
    }


    public static String encryptByECB(String data, String key) throws ServiceException {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;
            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec);
            byte[] encrypted = cipher.doFinal(plaintext);
            return Base64Encoder.encode(encrypted);

        } catch (Exception e) {
            throw new ServiceException( "加密失败",102);
        }
    }

    public static String decryptByECB(String data, String key) throws ServiceException {
        try {
            byte[] encrypted1 = Base64Decoder.decode(data);
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keyspec);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();
        } catch (Exception e) {
            throw new ServiceException( "解密失败",101);
        }
    }


    public static void main(String[] args) {
//        System.out.println(UUID.randomUUID().toString().replaceAll("-",""));

        String decrypt = decrypt("Gh6xcIE0UjUzJM98JZgLaMbXwuSfmZKZSqc1wEjVNxtxVq7AUHvdg6B0xhiy22x8ynCh/FbWHrbQHqAFs44yLvyFqbtyZj02jFxzqU8AE7hZivfmDCoGwLiRqyt3S7+vcgdXlDLLtjOIKpFT/wIjbcO7ICZfu6wcA0Ij6pPsuxy9/QpIcJ5u2b0lYQIQqP+PwSBLAMkX9OYKiwDvDDfDl+Zz19blLO9w8jdia9yQ7yS9QoYpsVJV2a+imlEKWE/5");
        System.out.println(decrypt);

        String encrypt1 = encrypt(decrypt);
        System.out.println(encrypt1);

        String encrypt = encrypt("{\"id\":\"IB9E3DKIDU94Z8Q7BJ3CZ9Q3G6HJ1Z05\",\"type\":\"1\",\"serviceType\":\"1\",\"content\":\"\",\"windowNum\":\"\",\"detailInfo\":\"1\",\"isReal\":\"N\",\"realName\":\"\",\"phone\":\"\",\"idCard\":\"\",\"politicsFace\":\"\"}");
        System.out.println(encrypt);

    }

}
