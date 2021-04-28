package com.visoft.file.service.service.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncoderService {
    private final static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static String getEncode(String value){
        return bCryptPasswordEncoder.encode(value);
    }

    public static boolean isPasswordsMatch(String password, String encodedPassword) {
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }
}