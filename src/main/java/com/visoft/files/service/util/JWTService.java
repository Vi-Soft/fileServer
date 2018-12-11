package com.visoft.files.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTService {

    private static String KEY = "visoftFileServer";

    public static String genearete(String tokenId){
        Claims claims = Jwts.claims()
                .setId(tokenId);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, KEY)
                .compact();
    }

    public static String validate(String token) {
        String tokenId;
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody();

            tokenId = body.getId();
        }
        catch (Exception e) {
           return null;
        }

        return tokenId;
    }
}
