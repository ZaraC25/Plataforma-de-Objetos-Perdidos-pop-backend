package edu.universidad.pop.modules.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@SuppressWarnings("null")
public class JwtService {

    @Value("${pop.jwt.secret}")
    private String secretKey;

    @Value("${pop.jwt.expiration-ms:3600000}")
    private long accessTokenExpirationMs;

    @Value("${pop.jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public String generarAccessToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails, accessTokenExpirationMs);
    }

    public String generarRefreshToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails, refreshTokenExpirationMs);
    }

    private String generarToken(Map<String, Object> extraClaims,
                                UserDetails userDetails,
                                long expirationMs) {
        Date ahora = new Date(System.currentTimeMillis());
        Date expiracion = new Date(System.currentTimeMillis() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            final String correo = extraerCorreo(token);
            return correo.equals(userDetails.getUsername()) && !esTokenExpirado(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}