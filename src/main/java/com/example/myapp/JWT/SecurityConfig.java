package com.example.myapp.JWT;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpMethod;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.example.myapp.service.UserService;
import com.example.myapp.JWT.JWTAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JWTAuthenticationFilter jwtFilter) throws Exception {
        http
                // CORS 設定を有効化し、CORSConfigurationSource を適用する
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // CSRF を無効化
                .csrf(AbstractHttpConfigurer::disable)
                // セッションを stateless に
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // OPTIONSリクエストはすべて許可
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // /auth/register, /auth/login, /api/** は認証なしでアクセス可能
                                .requestMatchers("/auth/register", "/auth/login", "/api/**",
                                        "/error", "/ws")
                                .permitAll().anyRequest().authenticated())
                // フォームログインとBasic認証は無効化
                .formLogin(login -> login.disable()).httpBasic(basic -> basic.disable())
                // JWT認証フィルターを追加（認証前に実行）
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService; // UserService をそのまま返す
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // すべてのオリジンを許可。必要に応じて、"http://localhost:3000" 等に絞ることも可能
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // OPTIONS, GET, POST, PUT, DELETE などのメソッドを許可
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // すべてのヘッダーを許可
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        // クレデンシャルの送信を許可
        configuration.setAllowCredentials(true);
        // プリフライトリクエストのキャッシュ時間を設定（秒単位）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
