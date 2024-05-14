package com.oauth.socialecommerce.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.oauth.socialecommerce.model.SecurityUser;

@Service
public class TokenService {

  private final JwtEncoder jwtEncoder;

  public TokenService(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public String generateToken(SecurityUser user) {
    Instant now = Instant.now();

    String scope = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

    /**
     * Map<String, String> extraClaims = Collections.emptyMap();
     * 
     * extraClaims.put("scope", scope);
     * extraClaims.put("email", user.getUsername());
     * extraClaims.put("username", user.getName());
     * 
     **/

    JwtClaimsSet claims = JwtClaimsSet
        .builder()
        .issuedAt(now)
        .expiresAt(now.plus(1, ChronoUnit.HOURS))
        .subject(user.getName())
        .claim("scope", scope)
        .claim("email", user.getUsername())
        .claim("username", user.getName())
        .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

}
