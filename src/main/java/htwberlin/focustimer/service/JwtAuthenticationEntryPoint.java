package htwberlin.focustimer.service;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JwtAuthenticationEntryPoint is a component that handles unauthorized access 
 * by sending an "Unauthorized" response to clients in cases of authentication failure.
 * 
 * Inspired by: https://www.youtube.com/watch?v=mn5UZYtPLjg
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Invoked when an authentication exception occurs, sending an "Unauthorized" response.
     *
     * @param request The HttpServletRequest object representing the client request.
     * @param response The HttpServletResponse object for sending the response.
     * @param authException The AuthenticationException that triggered the entry point.
     * @throws IOException If an I/O error occurs while sending the response.
     * @throws ServletException If a servlet-related exception occurs.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

}