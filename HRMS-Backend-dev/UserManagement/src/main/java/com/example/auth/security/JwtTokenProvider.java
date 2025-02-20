package com.example.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    private Key getSigningKey() {
        try {
            log.debug("Generating signing key from secret");
            if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
                log.error("JWT secret is null or empty");
                throw new IllegalArgumentException("JWT secret cannot be null or empty");
            }
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT secret key configuration: {}", e.getMessage());
            throw new SecurityException("Invalid JWT configuration: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateToken(Authentication authentication) {
        try {
            log.info("Starting token generation for user: {}", authentication.getName());
            
            // Get the principal directly as it might be a String or UserDetails
            String username;
            if (authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getPrincipal().toString();
            }
            
            Map<String, Object> claims = new HashMap<>();
            
            // Add roles to claims
            List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            log.info("Adding roles to token: {}", roles);
            claims.put("roles", roles);

            // Generate token with detailed logging
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
            
            log.info("Token generated successfully");
            return token;
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new SecurityException("Could not generate token: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}