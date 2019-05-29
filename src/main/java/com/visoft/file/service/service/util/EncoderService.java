package com.visoft.file.service.service.util;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncoderService {

    public static String getEncode(String value){
        byte[] bytesOfValue = value.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodeValue = md.digest(bytesOfValue);
        return new String(Hex.encodeHex(encodeValue));
    }
}