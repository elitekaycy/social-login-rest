package com.oauth.socialecommerce.ecommerce;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.socialecommerce.model.*;

@RestController
public class AuthController {
  private final ObjectMapper objectMapper;

  public AuthController(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @GetMapping("/login/oauth/{provider}")
  public ResponseEntity<LoginRedirectResponse> loginWithOauth(@PathVariable String provider,
      HttpServletRequest request) {

    String fullUrl = request.getScheme() + "://" + request.getServerName()
        + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");

    return ResponseEntity.ok(new LoginRedirectResponse(fullUrl + "/login/oauth2/authorization/" + provider, fullUrl));
  }

  @GetMapping("/user")
  public ResponseEntity<String> getAuthUser(Authentication authentication) throws JsonProcessingException {
    String response = objectMapper.writeValueAsString(authentication);
    return ResponseEntity.ok(response);
  }

}
