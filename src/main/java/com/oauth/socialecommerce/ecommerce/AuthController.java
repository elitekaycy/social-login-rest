package com.oauth.socialecommerce.ecommerce;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oauth.socialecommerce.model.*;
import com.oauth.socialecommerce.service.OAuthCallBackRequest;
import com.oauth.socialecommerce.service.UserService;

@RestController
public class AuthController {
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/login/oauth/{provider}")
  public ResponseEntity<LoginRedirectResponse> loginWithOauth(@PathVariable String provider,
      HttpServletRequest request) {

    String fullUrl = request.getScheme()
        + "://"
        + request.getServerName()
        + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");

    return ResponseEntity.ok(
        new LoginRedirectResponse(fullUrl
            + "/login/oauth2/authorization/"
            + provider,
            fullUrl));
  }

  @GetMapping("/auth/callback")
  public ResponseEntity<UserResponse> verifyOauthUser(
      @RequestParam(name = "registrationId", required = true) String registrationId,
      @RequestParam(name = "code", required = true) String code) throws URISyntaxException, JsonProcessingException {

    OAuthCallBackRequest oAuthCallBackRequest = new OAuthCallBackRequest(code, registrationId);
    return ResponseEntity.ok(userService.getUserFromOauthAccessToken(oAuthCallBackRequest));
  }
}
