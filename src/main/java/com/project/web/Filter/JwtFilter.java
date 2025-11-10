package com.project.web.Filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key SECRET_KEY;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    public void initKey() {
        SECRET_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String jwt = extractTokenFromCookies(request);

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // ensure SECRET_KEY is initialized (in case @PostConstruct didn't run or
                // jwtSecret changed)
                if (SECRET_KEY == null && jwtSecret != null) {
                    try {
                        SECRET_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        logger.error("Failed to init SECRET_KEY from jwt.secret property", e);
                        // don't break the chain; treat as no-auth
                        SecurityContextHolder.clearContext();
                        chain.doFilter(request, response);
                        return;
                    }
                }

                String username = Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody()
                        .getSubject();

                if (username != null) {
                    if (userDetailsService == null) {
                        logger.warn("UserDetailsService is not available - cannot load user details for jwt subject={}",
                                username);
                    } else {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        if (userDetails != null) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);

                            // Gắn username vào request để dùng ở controller nếu cần
                            request.setAttribute("username", username);
                        } else {
                            logger.warn("UserDetailsService returned null for username={}", username);
                        }
                    }
                }

            } catch (ExpiredJwtException e) {
                // Token hết hạn: xóa context nhưng tiếp tục filter chain
                logger.info("JWT expired: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            } catch (JwtException | IllegalArgumentException e) {
                // Token sai/giả mạo hoặc key null: xóa context và tiếp tục filter chain
                logger.warn("Invalid JWT token: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            } catch (Exception e) {
                // phòng trường hợp bất ngờ (ví dụ NPE từ userDetailsService)
                logger.error("Unexpected error in JwtFilter: {}", e.getMessage(), e);
                SecurityContextHolder.clearContext();
                chain.doFilter(request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
