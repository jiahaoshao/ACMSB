package net.fangyi.acmsb.Util;

import java.util.Base64;

public class Base64Util {
    public static byte[] encode(byte[] signData) {
        return Base64.getEncoder().encode(signData);
    }

    public static byte[] decode(String signData) {
        return Base64.getDecoder().decode(signData);
    }

    public static String encodeToStr(byte[] signData) {
        return new String(Base64.getEncoder().encode(signData));
    }

    public static String decodeToStr(String signData) {
        return new String(Base64.getDecoder().decode(signData));
    }
}

