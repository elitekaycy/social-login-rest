package com.oauth.socialecommerce.ecommerce;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class EcommerceController {

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/login")
  public String loginPage() {
    return "login page";
  }
}
