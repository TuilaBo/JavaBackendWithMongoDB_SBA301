package service;

import org.springframework.security.core.Authentication;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String email, String password);
    Map<String, Object> register(String email, String password, String accountName);
    Map<String, Object> createRole(String roleName);
    Map<String, Object> updateUserRole(String userId, String roleId);
    Map<String, Object> validateToken(String authHeader);
    Map<String, Object> getCurrentUser(Authentication authentication);
}
