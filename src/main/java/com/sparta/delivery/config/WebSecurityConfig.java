package com.sparta.delivery.config;


import com.sparta.delivery.jwt.JwtUtil;
import com.sparta.delivery.security.JwtAuthenticationFilter;
import com.sparta.delivery.security.JwtAuthorizationFilter;
import com.sparta.delivery.security.UserDetailsServiceImpl;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
@EnableMethodSecurity(securedEnabled = true) // @Secured 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // CORS 설정
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("*")); // 모든 출처 허용
            config.setAllowedMethods(List.of("*")); // 모든 메서드 허용
            config.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
            config.setAllowCredentials(true); // 자격 증명 허용
            return config;
        }));

        // 요청 권한 설정
        http.authorizeHttpRequests(authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll() // static 리소스 접근 허용
                .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                .requestMatchers("/api/users/**").permitAll() // '/api/users/**'로 시작하는 요청 모두 접근 허가
                .requestMatchers("/api/shops/**").permitAll() // '/api/shops/**'로 시작하는 요청 모두 접근 허가
                .anyRequest().authenticated() // 그 외 모든 요청 인증 필요
        );

        // formLogin 비활성화
        http.formLogin(formLogin -> formLogin.disable());

        // 필터 설정
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
