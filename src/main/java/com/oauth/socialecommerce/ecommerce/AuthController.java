package com.oauth.socialecommerce.ecommerce;

import java.net.URISyntaxException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oauth.socialecommerce.model.*;
import com.oauth.socialecommerce.model.OAuthCallBackRequest;
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

    String authProviderUrl = request.getScheme()
        + "://"
        + request.getServerName()
        + (request.getServerPort() != 80 ? ":" + request.getServerPort() : "");

    return ResponseEntity.ok(new LoginRedirectResponse(authProviderUrl + "/login/oauth2/authorization/" + provider));
  }

  @GetMapping("/auth/callback")
  public ResponseEntity<UserResponse> verifyOauthUser(
      @RequestParam(name = "registrationId", required = true) String registrationId,
      @RequestParam(name = "code", required = true) String code) throws URISyntaxException, JsonProcessingException {

    OAuthCallBackRequest oAuthCallBackRequest = new OAuthCallBackRequest(code, registrationId);
    return ResponseEntity.ok(userService.getUserFromOauthAccessToken(oAuthCallBackRequest));
  }
}
