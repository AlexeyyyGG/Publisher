package com.cloud.publishing.backend.security;

import static com.cloud.publishing.backend.security.SecurityConstants.ACCESS_TOKEN_TYPE;
import static com.cloud.publishing.backend.security.SecurityConstants.REFRESH_TOKEN_TYPE;
import static com.cloud.publishing.backend.security.SecurityConstants.TYPE;

import com.cloud.publishing.common.constants.Parameters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private static final String ROLES = "roles";
    private static final String INVALID_TOKEN_TYPE = "Invalid token type";

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.expiration.ms}") long accessTokenExpiration,
            @Value("${jwt.refresh.expiration.ms}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails, ACCESS_TOKEN_TYPE, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, REFRESH_TOKEN_TYPE, refreshTokenExpiration);
    }

    private String generateToken(UserDetails userDetails, String type, long expiration) {
        var builder = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(TYPE, type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration));
        if(userDetails instanceof EmployeeDetails employee){
            builder.claim(Parameters.ID, employee.getId());
        }
        if (ACCESS_TOKEN_TYPE.equals(type)) {
            builder.claim(ROLES, userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList());
        }
        return builder.signWith(secretKey, SignatureAlgorithm.HS256).compact();
    }

    public Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get(ROLES);
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public String extractUsernameFromRefreshToken(String token) {
        Claims claims = parseAllClaims(token);
        if (!REFRESH_TOKEN_TYPE.equals(claims.get(TYPE))) {
            throw new RuntimeException(INVALID_TOKEN_TYPE);
        }
        return claims.getSubject();
    }
}