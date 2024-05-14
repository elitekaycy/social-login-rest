package com.oauth.socialecommerce.ecommerce;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/")
public class EcommerceController {

  @GetMapping("/v1/ecommerce")
  public String home() {
    return "home";
  }

}
