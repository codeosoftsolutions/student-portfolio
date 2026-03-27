package com.studenttap.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        System.out.println(">>> JwtFilter: " + method
            + " " + requestURI);

        // Step 1: Get token from header
        String token = getTokenFromRequest(request);

        if (token == null) {
            System.out.println(">>> No token found in request");
        } else {
            System.out.println(">>> Token found, validating...");
        }

        // Step 2: Validate token and set authentication
        if (StringUtils.hasText(token)
                && jwtUtil.validateToken(token)) {

            String email = jwtUtil.getEmailFromToken(token);
            System.out.println(">>> Token valid for: " + email);

            UserDetails userDetails =
                userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

            authentication.setDetails(
                new WebAuthenticationDetailsSource()
                    .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                .setAuthentication(authentication);

            System.out.println(">>> Authentication set for: "
                + email);

        } else if (StringUtils.hasText(token)) {
            System.out.println(">>> Token is INVALID or EXPIRED!");
        }

        filterChain.doFilter(request, response);
    }

    // Extract token from "Authorization: Bearer <token>"
    private String getTokenFromRequest(
            HttpServletRequest request) {
        String bearerToken =
            request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}