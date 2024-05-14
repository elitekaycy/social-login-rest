package com.oauth.socialecommerce.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oauth.socialecommerce.model.OAuthCallBackRequest;
import com.oauth.socialecommerce.model.OAuthUserResponse;
import com.oauth.socialecommerce.model.SecurityUser;
import com.oauth.socialecommerce.model.User;
import com.oauth.socialecommerce.model.UserResponse;
import com.oauth.socialecommerce.repository.UserRepository;

@Service
public class UserService {

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final RestTemplate restTemplate;
  private final TokenService tokenService;
  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  // private final PasswordEncoder passwordEncoder;

  public UserService(ObjectMapper objectMapper, TokenService tokenService,
      ClientRegistrationRepository clientRegistrationRepository,
      RestTemplate restTemplate, /** PasswordEncoder passwordEncoder **/
      UserRepository userRepository,
      AuthenticationManager authenticationManager) {
    this.clientRegistrationRepository = clientRegistrationRepository;
    this.restTemplate = restTemplate;
    this.tokenService = tokenService;
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    // this.passwordEncoder = passwordEncoder;
  }

  public UserResponse getUserFromOauthAccessToken(OAuthCallBackRequest request)
      throws URISyntaxException, JsonMappingException, JsonProcessingException {

    String userInfoEndpointUri = clientRegistrationRepository
        .findByRegistrationId(request.registrationId())
        .getProviderDetails()
        .getUserInfoEndpoint()
        .getUri();

    URI userInfoUri = new URI(userInfoEndpointUri + "?access_token=" + request.code());

    OAuthUserResponse oauthUser = restTemplate.getForObject(userInfoUri, OAuthUserResponse.class);

    User user = findOrCreateUser(oauthUser);

    Authentication authenticated = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
    SecurityContextHolder.getContext().setAuthentication(authenticated);

    String token = tokenService.generateToken(new SecurityUser(user));
    return new UserResponse(token, user);
  }

  public User findOrCreateUser(OAuthUserResponse oAuthUserResponse) {
    Optional<User> getUser = userRepository.findByEmail(oAuthUserResponse.email());

    if (getUser.isPresent()) {
      return getUser.get();
    }

    User newUser = new User();

    newUser.setEmail(oAuthUserResponse.email());
    newUser.setName(oAuthUserResponse.name());
    newUser.setPassword(null);
    newUser.setRoles(List.of("USER"));

    return userRepository.save(newUser);
  }

}
