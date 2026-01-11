package vn.hust.social.backend.security;

import jakarta.servlet.http.HttpServletRequest;

public class JwtHeaderUtils {

    public static String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    public static String extractEmail(HttpServletRequest request, JwtUtils jwtUtils) {
        String token = extractTokenFromHeader(request);
        if (token != null) {
            return jwtUtils.extractEmail(token);
        }
        return null;
    }
}
