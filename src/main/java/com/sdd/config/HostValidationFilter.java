package com.sdd.config;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class HostValidationFilter extends OncePerRequestFilter {
    //private static final String ALLOWED_HOST = "icg.net.in";
    private static final String ALLOWED_HOST = "icg.net.in";
    //private static final String ALLOWED_HOST = "http://localhost:4200";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String host = request.getHeader("Host");
        if (host == null || !host.equals(ALLOWED_HOST)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Host Header");
            return;
        }
        filterChain.doFilter(request, response);
    }
}