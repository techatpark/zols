package org.zols.starter.security.jwt;

import java.util.Date;
import java.util.HashMap;

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;

@Component
public class TokenProvider {
  private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

  @Value("${app.auth.tokenSecret}")
  private String jwtSecret;

  @Value("${app.auth.tokenExpirationMsec}")
  private int jwtExpirationMs;

  public String generateJwtToken(Authentication authentication) {

    final Date now = new Date();
    final Date expiryDate = new Date(now.getTime()
            + jwtExpirationMs);
    final SecretKey tkey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    return Jwts.builder()
            .setClaims(new HashMap<>())
            .setSubject(authentication.getName())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(expiryDate)
            .signWith(tkey).compact();
  }

  public String getUserNameFromJwtToken(String token) {
    final SecretKey tkey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    final Claims claims = Jwts.parserBuilder().setSigningKey(tkey).build().parseClaimsJws(token)
            .getBody();

    return claims.getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      final SecretKey tkey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
      Jwts.parserBuilder().setSigningKey(tkey).build().parseClaimsJws(authToken);
      return true;
    }  catch (MalformedJwtException e) {
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}