package com.example.tradingjournal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("JwtAuthenticationFilter triggered 🚨");


        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Skip and continue
            return;
        }

        final String jwt = authHeader.substring(7); // Remove "Bearer "
        final String userEmail = jwtService.extractUsername(jwt);
        System.out.println("JWT: " + jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                    System.out.println("Username from token: " + userDetails.getUsername());
                System.out.println("User authenticated: " + (SecurityContextHolder.getContext().getAuthentication() != null));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authorities: " + userDetails.getAuthorities());

            }
        }

        filterChain.doFilter(request, response); // continue to the next filter
    }
}
