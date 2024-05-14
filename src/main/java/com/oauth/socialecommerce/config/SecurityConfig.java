package com.oauth.socialecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final RsaKeyProperties rsaKeys;

  public SecurityConfig(RsaKeyProperties rsaKeys) {
    this.rsaKeys = rsaKeys;
  }

  @Autowired
  private OAuth2AuthorizedClientService oAuthorizedClientService;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/login/**").permitAll();
          auth.anyRequest().authenticated();
        })
        .exceptionHandling(exception -> {
          exception.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
              .accessDeniedHandler(new BearerTokenAccessDeniedHandler());
        })
        .oauth2Client(Customizer.withDefaults())
        .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
        .oauth2Login(oauth -> {
          oauth.loginPage("/login/oauth");
          oauth.authorizationEndpoint(authorization -> {
            authorization.baseUri("/login/oauth2/authorization");
          });
          oauth.successHandler(new CustomAuthenticationSuccessHandler(oAuthorizedClientService));
        })
        .build();
  }

  /**
   * @Bean
   *       PasswordEncoder passwordEncoder() {
   *       return new BCryptPasswordEncoder();
   *       }
   **/

  @Bean
  @Primary
  public UserDetailsService userDetailsService() {
    return new SecurityUserDetailService();
  }

  @Bean
  public AuthenticationManager authManager(UserDetailsService userDetailsService) {
    var authProvider = new CustomAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    // authProvider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(authProvider);
  }

  @Bean
  JwtDecoder JwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));

    return new NimbusJwtEncoder(jwks);
  }

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   *
   * This bean was used as the previous authenticationEntrypoint for just the raw
   * oauth without JWT
   *
   * Incase we didnt introuduce the OAuthResourceServer, instantiate this bean as
   * the default unauthorized oauth access control
   * 
   * @Bean
   *       AuthenticationEntryPoint authenticationEntryPoint() {
   *       return new RestUnauthorizedEntryPoint();
   *       }
   * 
   * 
   **/
}
