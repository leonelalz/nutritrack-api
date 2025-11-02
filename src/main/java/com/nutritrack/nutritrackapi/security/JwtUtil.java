package com.nutritrack.nutritrackapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Lista temporal para tokens invalidados (logout)
    private static final List<String> invalidatedTokens = new ArrayList<>();

    // ==================== GENERACIÓN ====================

    /** Generar token con email, nombre y perfilId */
    public String generateToken(String email, String name, UUID perfilId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email) // correo principal
                .claim("name", name)
                .claim("perfilId", perfilId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Generar un nuevo token desde uno existente (refresh) */
    public String generateTokenFromExistingToken(String oldToken) {
        Claims claims = getAllClaimsFromToken(oldToken);
        String email = claims.getSubject();
        String name = claims.get("name", String.class);
        UUID perfilId = claims.get("perfilId", UUID.class);
        return generateToken(email, name, perfilId);
    }

    // ==================== EXTRACCIÓN ====================

    /** Obtener token del header Authorization */
    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /** Extraer email del token */
    public String getEmailFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /** Extraer nombre */
    public String getNameFromToken(String token) {
        return getAllClaimsFromToken(token).get("name", String.class);
    }

    /** Extraer perfilId */
    public UUID getPerfilIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("perfilId", UUID.class);
    }

    /** Obtener todos los claims */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ==================== VALIDACIÓN ====================

    /** Validar token (estructura, firma, expiración y logout) */
    public boolean validateToken(String token) {
        try {
            if (isTokenInvalidated(token)) {
                return false; // token fue desactivado manualmente
            }
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ==================== LOGOUT ====================

    /** Invalida un token actual (logout) */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    /** Verifica si el token fue invalidado */
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    // ==================== UTILIDADES ====================

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
