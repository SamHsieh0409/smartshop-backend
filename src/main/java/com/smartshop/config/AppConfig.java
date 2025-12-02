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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
    
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); 
        
        
        // 1. 允許本機前端
        config.addAllowedOriginPattern("http://localhost:5173"); 
        config.addAllowedOriginPattern("http://127.0.0.1:5173"); 

        // 2. 允許 Ngrok 產生的網址 (使用萬用字元 * 匹配所有 ngrok-free.app 子網域)
        config.addAllowedOriginPattern("https://*.ngrok-free.app");

        // 3. 允許綠界回傳的網域
        // config.addAllowedOriginPattern("https://*.ecpay.com.tw");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
