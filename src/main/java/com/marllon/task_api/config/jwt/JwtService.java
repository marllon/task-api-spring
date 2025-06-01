package com.marllon.task_api.config.jwt;

import com.google.cloud.secretmanager.v1.*;
import com.google.protobuf.ByteString;
import com.marllon.task_api.domain.user.User;
import com.marllon.task_api.security.CustomUserDetailsService;
import com.marllon.task_api.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${secret.jwt.name}")
    private String jwtSecretName;

    @Value("${secret.jwt.version}")
    private String jwtSecretVersion;

    @Value("${gcp.project-id}")
    private String gcpProjectId;

    private Key signingKey;

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public JwtService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        String secret = loadSecret(jwtSecretName, jwtSecretVersion);
        signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String loadSecret(String secretName, String version) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(gcpProjectId, secretName, version);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            ByteString payload = response.getPayload().getData();
            return payload.toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JWT secret from GCP Secret Manager", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        final String username = extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}

