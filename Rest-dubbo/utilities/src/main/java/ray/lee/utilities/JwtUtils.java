package ray.lee.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtils {
	public static final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(1);
	
	public static String generateToken(String clientId, String clientSecret) {
        return generateToken(null, clientId, clientSecret);
    }
    
    public static String generateToken(Map<String, Object> extractClaims, String clientId, String clientSecret) {
    	if(extractClaims == null) {
    		extractClaims = new HashMap<String, Object>();
    	}
    	return Jwts.builder().subject(clientId)
    								 .claims(extractClaims)
    								 .issuedAt(new Date())
    								 .expiration(new Date(System.currentTimeMillis()+EXPIRATION_TIME))
    								 .signWith(getSignInKey(clientSecret), Jwts.SIG.HS256)
    								 .compact();
    }

    public static boolean isTokenValid(String token, String clientId, String clientSecret) {
        Optional<String> jwtClientId = extractClientId(token, clientSecret);
        if(jwtClientId.isPresent()) {
        	return (jwtClientId.get().equals(clientId));	
        } else {
        	return false;
        }
    }
    
    public static boolean isTokenExpired(String token, String clientSecret) {
		Date expirationDate = extractExpiration(token, clientSecret);
		if(expirationDate == null) {
			return true;
		} else {
			return expirationDate.before(new Date());
		}
    }    

    /**
     * 獲取令牌中所有的聲明
     * @return 令牌中所有的聲明
     */
    public static Claims extractAllClaims(String token, String secret) {
        try {
            return Jwts.parser()
            		.verifyWith(getSignInKey(secret))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
    
	private static Optional<String> extractClientId(String token, String clientSecret) {
        try {
            return Optional.of(extractClaim(token, Claims::getSubject, clientSecret));
        } catch(Throwable e) {
        	return Optional.empty();
        }
    }    
    
    private static Date extractExpiration(String token, String clientSecret) {
    	try {
			return extractClaim(token, Claims::getExpiration, clientSecret);
		} catch (Throwable e) {
			log.debug(e.getMessage(), e);
			return null;
		}
    }    
    
	private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String clientSecret) throws Throwable {
        final Claims claims = extractAllClaims(token, clientSecret);
        return claimsResolver.apply(claims);
    }    

    private static SecretKey getSignInKey(String secret) {
    	try {
    		MessageDigest md = MessageDigest.getInstance("SHA-256");
   		 	md.update(secret.getBytes());
   		 	return Keys.hmacShaKeyFor(md.digest());
    	} catch(NoSuchAlgorithmException e) {
			log.debug(e.getMessage(), e);
			return null;
    	}
    }
}
