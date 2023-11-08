package htwberlin.focustimer.service;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * JwtAuthenticationFilter is a filter that intercepts incoming requests 
 * to validate JWT tokens and establish user authentication if a valid token is provided.
 * 
 * Inspired by: https://www.youtube.com/watch?v=mn5UZYtPLjg
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Performs token validation and user authentication if a valid JWT token is provided in the request.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     * @param response The HttpServletResponse object for sending responses.
     * @param filterChain The FilterChain for handling the request.
     * @throws ServletException If a servlet-related exception occurs.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            String mail = jwtTokenProvider.getUserMailFromToken(jwt);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(mail);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request's "Authorization" header.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     * @return The JWT token as a String, or null if no valid token is found.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}