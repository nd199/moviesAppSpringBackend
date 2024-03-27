package com.naren.movieticketbookingapplication.jwt;

import com.naren.movieticketbookingapplication.Exception.AlgorithmNotSupportedException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JWTUtil {


    private static final SecretKey SECRET_KEY;

    static {
        try {
            SECRET_KEY = generateSigningKey();
        } catch (NoSuchAlgorithmException e) {
            throw new AlgorithmNotSupportedException("Algorithm HmacSHA256 is not supported");
        }
    }


    public  String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public  String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }


    public  String issueToken(String subject, Map<String, Object> claims) {

        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuer("https://www.codeNaren.com")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(SECRET_KEY)
                .compact();
    }


    private static SecretKey generateSigningKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        return keyGenerator.generateKey();
    }
}
