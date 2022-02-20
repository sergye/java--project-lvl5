package hexlet.code.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
public class TokenServiceImpl implements TokenService, Clock {

    private final SecretKey secretKey;
    private final String issuer;
    private final Long expirationSec;
    private final Long clockSkewSec;

    public TokenServiceImpl(@Value("${jwt.issuer:taskMan}") final String issuer,
                           @Value("${jwt.expiration-sec:86400}") final Long expirationSec,
                           @Value("${jwt.clock-skew-sec:300}") final Long clockSkewSec) {
        this.secretKey = Keys.secretKeyFor(HS256);
        this.issuer = issuer;
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
    }

    @Override
    public String getToken(final Map<String, Object> attributes) {
        return Jwts.builder()
                .signWith(secretKey)
                .compressWith(new GzipCompressionCodec())
                .setClaims(createClaims(attributes, expirationSec))
                .compact();
    }

    @Override
    public Map<String, Object> parse(final String token) {
        return Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Date now() {
        return new Date();
    }

    private Claims createClaims(final Map<String, Object> attributes, final Long expiresInSec) {
        final Claims claims = Jwts.claims();
        claims.setIssuer(issuer);
        claims.setIssuedAt(now());
        claims.putAll(attributes);
        if (expiresInSec > 0) {
            claims.setExpiration(new Date(System.currentTimeMillis() + expiresInSec * 1000));
        }
        return claims;
    }
}
