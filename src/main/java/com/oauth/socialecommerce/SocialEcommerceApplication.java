package com.oauth.socialecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.oauth.socialecommerce.config.RsaKeyProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class SocialEcommerceApplication {

  public static void main(String[] args) {
    SpringApplication.run(SocialEcommerceApplication.class, args);
  }

}
