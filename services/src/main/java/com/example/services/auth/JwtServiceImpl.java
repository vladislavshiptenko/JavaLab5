package com.example.services.auth;

import com.example.dto.user.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {
    private static final String secretKey = "33a6d819ce32059c88d4c90d565cd5e9747418c99208610b09210a53fb79a597";
    private static final SecretKey jwt_secret = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);

        return (String) claims.getOrDefault("username", "");
    }

    public long extractId(String token) {
        Claims claims = extractAllClaims(token);

        return (int) claims.getOrDefault("id", 0);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserDetailsImpl customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("username", customUserDetails.getUsername());
            claims.put("role", customUserDetails.getRole());
        }

        return Jwts.builder().claims(claims).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .claims(claims)
                .signWith(jwt_secret).compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);

        return claims.getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwt_secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
