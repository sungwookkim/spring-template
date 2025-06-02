package com.member.spring.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
public class WebConfigController implements WebMvcConfigurer {

    @GetMapping("favicon.ico")
    public void favicon() {

    }
}
