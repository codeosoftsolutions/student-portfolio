
/*

package com.studenttap.security;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    // Generate secret key from string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // CREATE token from student email
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    // GET email from token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // CHECK if token is valid
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

	public String extractEmail(String token) {
		// TODO Auto-generated method stub
		return null;
	}
}*/



package com.studenttap.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret:StudentTapNFCPortfolioSecretKey2024VeryLongAndSecure123456}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpiration;

    // ✅ Generate token with email as subject
    public String generateToken(String email) {
        SecretKey key = Keys.hmacShaKeyFor(
            jwtSecret.getBytes());

        return Jwts.builder()
            .subject(email)          // ← email stored here
            .issuedAt(new Date())
            .expiration(new Date(
                System.currentTimeMillis()
                + jwtExpiration))
            .signWith(key)
            .compact();
    }

    // ✅ Extract email safely - never returns null
    public String extractEmail(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes());

            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // Try subject first
            String subject = claims.getSubject();
            if (subject != null && !subject.isEmpty()) {
                return subject;
            }

            // Try email claim
            String emailClaim = claims.get(
                "email", String.class);
            if (emailClaim != null) {
                return emailClaim;
            }

            System.out.println(
                ">>> WARNING: No email in token!");
            System.out.println(
                ">>> Claims: " + claims.toString());
            return null;

        } catch (Exception e) {
            System.out.println(
                ">>> extractEmail error: "
                + e.getMessage());
            return null;
        }
    }

    // ✅ Validate token
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes());

            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

            return true;
        } catch (Exception e) {
            System.out.println(
                ">>> Token invalid: "
                + e.getMessage());
            return false;
        }
    }
}




