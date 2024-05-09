package com.oauth.socialecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  AuthenticationEntryPoint authenticationEntryPoint() {
    return new RestUnauthorizedEntryPoint();
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    return http
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers("/login/**").permitAll();
          auth.anyRequest().authenticated();
        })
        .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint()))
        .oauth2Login(oauth -> {
          oauth.loginPage("/login/oauth");
          oauth.authorizationEndpoint(authorization -> {
            authorization.baseUri("/login/oauth2/authorization");
          });
          oauth.successHandler(new SimpleUrlAuthenticationSuccessHandler("/user"));
        })
        .build();
  }
}
