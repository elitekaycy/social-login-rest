package com.oauth.socialecommerce.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  public CustomAuthenticationSuccessHandler(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
    this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication)
      throws IOException {

    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
    OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService
        .loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), authentication.getName());

    String authorizationCode = oAuth2AuthorizedClient.getAccessToken().getTokenValue();

    String redirectUrl = "/auth/callback?code="
        + URLEncoder.encode(authorizationCode, "UTF-8")
        + "&registrationId="
        + URLEncoder.encode(oauthToken.getAuthorizedClientRegistrationId(), "UTF-8");

    response.sendRedirect(request.getContextPath() + redirectUrl);
  }

}
