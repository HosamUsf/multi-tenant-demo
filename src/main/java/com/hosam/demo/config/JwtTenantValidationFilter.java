//package com.hosam.demo.config;
//
//import com.sigma.newerp.services.JwtService;
//import com.sigma.newerp.tenantConfig.TenantContext;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@Component
//@Order(1)  // this runs before TenantFilter
//public class JwtTenantValidationFilter extends OncePerRequestFilter {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtTenantValidationFilter.class);
//    private final JwtService jwtService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        // First try to get tenant from JWT token
//        if (header != null && header.startsWith("Bearer ")) {
//            try {
//                // Extract tenant ID from the token
//                String tenantId = jwtService.extractTenantId(header.substring(7));
//                if (tenantId != null) {
//                    logger.debug("Setting tenant from JWT: {}", tenantId);
//                    TenantContext.setCurrentTenant(tenantId);
//                    filterChain.doFilter(request, response);
//                    return;
//                }
//            } catch (Exception e) {
//                logger.warn("Failed to extract tenant from JWT", e);
//                // Continue to next tenant resolution method
//            }
//        }
//
//        // Let the TenantFilter handle further tenant resolution logic
//        filterChain.doFilter(request, response);
//    }
//
//}
//
