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


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String host = request.getHeader("Host");
        if (host != null && host.contains(":")) {
            host = host.split(":")[0];  // Remove port if present
        }
        if (host == null || !host.equals(ALLOWED_HOST)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Host Header");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

//package com.sdd.config;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Set;
//import java.util.logging.Logger;
//
//@Component
//public class HostValidationFilter extends OncePerRequestFilter {
//    private static final Logger LOGGER = Logger.getLogger(HostValidationFilter.class.getName());
//
//    // Define allowed hosts without ports
//    private static final Set<String> ALLOWED_HOSTS = Set.of("icg.net.in", "localhost");
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String host = request.getHeader("Host");
//
//        if (host != null && host.contains(":")) {
//            host = host.split(":")[0];  // Remove port if present
//        }
//
//        if (host == null || !ALLOWED_HOSTS.contains(host.toLowerCase())) {
//            LOGGER.warning("Blocked request with invalid Host header: " + request.getHeader("Host"));
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Host Header");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

