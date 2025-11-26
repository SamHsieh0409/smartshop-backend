package com.smartshop.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AppConfig {
    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
              .setMatchingStrategy(MatchingStrategies.STRICT)
              .setSkipNullEnabled(true);
        return mapper;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())          // 關閉 CSRF（前後端分離必須）
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()  // 放行登入註冊
                .anyRequest().permitAll()                 // 其他全部放行
            )
            .formLogin(login -> login.disable())          // 不用 Spring Security 的登入
            .httpBasic(Customizer.withDefaults())         // 使用 basic 或不管它
            .logout(logout -> logout.disable())           // 不用 Spring Security 的 logout
        ;

        return http.build();
    }
}
