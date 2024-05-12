package com.unknown.subinjection;


import android.util.Base64;

import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String KEY = "82ed48f7f5a16f02b20b6b38553ec7cd2f4074be82c5f859ae70765c29846ea0";
    private static final String IV = "a0192f6af73f95063f8a0915b1eb96e2";

    public String toB64(String data){
        return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
    }
    public String enc(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        Key key = new SecretKeySpec(hexStringToByteArray(KEY), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(IV));
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.encodeToString(encryptedData, Base64.DEFAULT);
    }

    public String dec(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(KEY), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(IV));
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decodedData = Base64.decode(data, Base64.DEFAULT);
        byte[] decrypted = cipher.doFinal(decodedData);

        return new String(decrypted);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}