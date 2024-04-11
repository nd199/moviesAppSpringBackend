package com.naren.movieticketbookingapplication.jwt;

import com.naren.movieticketbookingapplication.Entity.Role;
import com.naren.movieticketbookingapplication.Exception.AlgorithmNotSupportedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class JwtUtil {

    private static final SecretKey SECRET_KEY;

    static {
        try {
            SECRET_KEY = generateSecretKey();
        } catch (NoSuchAlgorithmException e) {
            throw new AlgorithmNotSupportedException("Algorithm not supported");
        }
    }

    private static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        return keyGenerator.generateKey();
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        log.debug("Issuing JWT token for subject: {}", subject);
        return Jwts.builder().claims(claims)
                .subject(subject)
                .issuer("codeNaren.com")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String issueToken(String subject, Set<Role> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("subject", subject);
        claims.put("roles", roles);

        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(SECRET_KEY)
                .compact();
    }


    private Claims getClaims(String token) {
        log.debug("Parsing and verifying JWT token");
        return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }

    public String getSubject(String token) {
        log.debug("Getting subject from JWT token");
        return getClaims(token).getSubject();
    }

//    public Set<String> getRoles(String token) {
//        //noinspection unchecked
//        return (Set<String>) getClaims(token).get("roles");
//    }

    public boolean isTokenValid(String token, String userName) {
        try {
            Claims claims = getClaims(token);
            boolean isValid = claims.getSubject().equals(userName) && !isTokenExpired(claims);
            if (!isValid) {
                log.warn("JWT token validation failed for subject: {}", userName);
            }
            return isValid;
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
            return false; // Invalid token or parsing error
        }
    }

    private boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        boolean isExpired = expiration != null && expiration.before(Date.from(Instant.now()));
        if (isExpired) {
            log.warn("JWT token has expired");
        }
        return isExpired;
    }
}
