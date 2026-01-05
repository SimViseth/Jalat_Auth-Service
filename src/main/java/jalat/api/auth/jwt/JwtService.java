package jalat.api.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private static final String SECRET = "HDAJyAuvGzPGNWD4imkXDiOvrH4pk9D90yYWi3i7XSql+9jV3yRf8fopsRc+bn1g";
    private static final long JWT_TOKEN_VALIDITY = 7 * 24 * 60 * 60;
    private static final long JWT_REFRESH_TOKEN_VALIDITY = 168 * 60 * 60;
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    //Create token for user
    private String createToken(Map<String, Object> claims, String username, long expiration){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }
    //Generate token for user
    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, JWT_TOKEN_VALIDITY);
    }
    // Generate refresh token for user
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, JWT_REFRESH_TOKEN_VALIDITY);
    }

    // Validate refresh token
    public Boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        final String username = extractUsername(refreshToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(refreshToken));
    }

    //    Extract All Claims with SignKey and token
    private Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    //    Extract Claim with token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //    Extract Claim with username
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    //    Extract Claim with expiration
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    //    Check token is expired
    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    //    Check token is valid or not
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
