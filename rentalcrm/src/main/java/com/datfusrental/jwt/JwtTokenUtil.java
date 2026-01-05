package com.datfusrental.jwt;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

    private static final Logger logger = Logger.getLogger(JwtTokenUtil.class);

    // 🔐 ONE SECRET KEY (keep it safe)
    private static final String SECRET_KEY = "MyVerySecretKeyForJwt123456789";

    // ⏰ 12 hours
    private static final long EXPIRE_DURATION = 12 * 60 * 60 * 1000;

    // ✅ GENERATE TOKEN
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT token", e);
            return false;
        }
    }

    // ✅ EXTRACT USERNAME
    public String getUsername(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
