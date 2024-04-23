package com.datfusrental.jwt;

import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.datfusrental.entities.UserDetails;
import com.datfusrental.helper.UserHelper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
 
@Component
public class JwtTokenUtil {
	
	@Autowired
	private UserHelper userHelper;
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
    private static final long EXPIRE_DURATION = 12 * 60 * 60 * 1000; // 24 hour
     
	public String generateAccessToken(UserDetails user)
			throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {

		return Jwts.builder()
				.setSubject(user.getLoginId())
				.setIssuer("ADMIN")
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRE_DURATION))
				.signWith(SignatureAlgorithm.HS512, user.getSecretKey()).compact();

	}

	
	public String generateSecretKey() {

		byte[] secretKeyBytes = new byte[32]; // 256 bits
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(secretKeyBytes);

		// Encode the secret key as a Base64-encoded string
		String secretKey = Base64.getEncoder().encodeToString(secretKeyBytes);
		return secretKey;
	}
	
	public boolean validateJwtToken(String loginId, String token) {

		try {
			
			UserDetails user = userHelper.getUserDetailsByLoginId(loginId);
			if(user != null) {
				
			

			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(user.getSecretKey()))
					.parseClaimsJws(token).getBody();

			// Check expiration time
			Date expiration = claims.getExpiration();
			Date now = new Date();
			if (expiration.before(now)) {
				logger.info("Token has expired");
				return false;
			} else {
				logger.info("Token is valid");
				return true;
			}
			}else {
				logger.info("User Not Found");
				return false;
			}
		} catch (Exception e) {
			logger.info("Token verification failed");
			return false;
		}
	}
    
    
  
}