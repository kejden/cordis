package io.ndk.cordis_backend.service;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(String username);
    String extractUserName(String token);
    boolean validateToken(String token, UserDetails userDetails);
    String parseJwt(StompHeaderAccessor accessor);
    boolean validateJwtToken(String authToken);
}
