package com.example.nikonbe.common.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.nikonbe.common.constants.AuthConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

  @Value("${jwt.token.secret}")
  private String jwtSecretKey;

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractUsername(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  private Date extractExpiration(String token) {
    return extractClaims(token, Claims::getExpiration);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(
            new Date(
                System.currentTimeMillis()
                    + 1000L * 60 * AuthConstants.ACCESS_TOKEN_VALIDITY_MINUTES))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private String generateStaffToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 20))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateToken(UserDetails userDetails, Integer userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userDetails.getUsername());
    claims.put("id", userId);
    return generateToken(claims, userDetails);
  }

  public String generateStaffToken(UserDetails userDetails, Integer staffId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userDetails.getUsername());
    claims.put("id", staffId);
    claims.put("type", "staff");
    return generateStaffToken(claims, userDetails);
  }

  private String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    long expirationMillis;
    if (AuthConstants.MODE_TEST_REFRESH) {
      expirationMillis = 1000L * 60 * AuthConstants.REFRESH_TOKEN_VALIDITY_MINUTES_TEST;
    } else {
      expirationMillis = 1000L * 60 * 60 * 24 * AuthConstants.REFRESH_TOKEN_VALIDITY_DAYS;
    }
    return Jwts.builder()
        .setClaims(extraClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(UserDetails userDetails, Integer userId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userDetails.getUsername());
    claims.put("id", userId);
    return generateRefreshToken(claims, userDetails);
  }

  public String generateStaffRefreshToken(UserDetails userDetails, Integer staffId) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("sub", userDetails.getUsername());
    claims.put("id", staffId);
    claims.put("type", "staff");
    return generateRefreshToken(claims, userDetails);
  }
}
