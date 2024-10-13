package paddlee

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys

import java.security.Key
import java.util.Date

class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256)

    String generateToken(String alias) {
        return Jwts.builder()
            .setSubject(alias)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hora
            .signWith(key)
            .compact()
    }

    boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (Exception e) {
            return false
        }
    }

    String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject()
    }
}
