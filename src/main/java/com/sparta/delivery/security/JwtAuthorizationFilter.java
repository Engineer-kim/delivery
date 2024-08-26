package com.sparta.delivery.security;

import com.sparta.delivery.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        // 1. Authorization 헤더에서 토큰 가져오기
        String tokenValue = jwtUtil.getJwtFromHeader(req);

        if (StringUtils.hasText(tokenValue)) {

            // 2. 토큰 검증
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }

            // 3. 토큰에서 사용자 정보 가져오기
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);


            // 4. 가져온 사용자 정보로 인증 객체(Authentication) 생성
            // 5. 인증 객체 SecurityContextHolder 에 저장
            try {
                // info.getSubject() : 토큰 생성할 때 setSubject(username) 로 넣었던 username 추출
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    // SecurityContextHolder 안에 있는 SecurityContext 에  Authentication 넣기
    public void setAuthentication(String username) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(username);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성 (Authentication)
    // 원래는 Authentication Manager 가 알아서 해줌
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}