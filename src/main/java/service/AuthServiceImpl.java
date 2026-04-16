package service;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pojo.Account;
import pojo.Role;
import security.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final SystemAccountService systemAccountService;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            SystemAccountService systemAccountService,
            JwtUtil jwtUtil,
            RoleService roleService
    ) {
        this.authenticationManager = authenticationManager;
        this.systemAccountService = systemAccountService;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
    }

    @Override
    public Map<String, Object> login(String email, String password) {
        Account account = systemAccountService.findByEmail(email);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email is not registered");
        }
        if (!account.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your account has been deactivated");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtUtil.generateToken(userDetails));
        response.put("email", userDetails.getUsername());
        response.put("accountName", account.getAccountName());
        response.put("accountId", account.getId());
        response.put("role", resolveRoleName(account.getRoleId()));
        response.put("isActive", account.isActive());
        response.put("authorities", userDetails.getAuthorities());
        response.put("message", "Login successful");
        return response;
    }

    @Override
    public Map<String, Object> register(String email, String password, String accountName) {
        requireNotBlank(email, "Email is required");
        requireNotBlank(password, "Password is required");
        requireNotBlank(accountName, "Account name is required");

        if (systemAccountService.findByEmail(email) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Role userRole = roleService.getRole("USER");
        if (userRole == null) {
            Role defaultRole = new Role();
            defaultRole.setRoleName("USER");
            userRole = roleService.insertRole(defaultRole);
        }

        Account createdAccount = systemAccountService.createUser(email, password, userRole.getId(), accountName);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("accountId", createdAccount.getId());
        response.put("email", createdAccount.getEmail());
        response.put("accountName", createdAccount.getAccountName());
        response.put("role", resolveRoleName(createdAccount.getRoleId()));
        response.put("isActive", createdAccount.isActive());
        return response;
    }

    @Override
    public Map<String, Object> createRole(String roleName) {
        requireNotBlank(roleName, "Role name is required");
        if (roleService.getRole(roleName) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role with name '" + roleName + "' already exists");
        }

        Role role = new Role();
        role.setRoleName(roleName);
        Role createdRole = roleService.insertRole(role);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role created successfully");
        response.put("roleId", createdRole.getId());
        response.put("roleName", createdRole.getRoleName());
        return response;
    }

    @Override
    public Map<String, Object> updateUserRole(String userId, String roleId) {
        requireNotBlank(roleId, "Role ID is required");
        Role role = roleService.getRoleById(roleId);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role with ID " + roleId + " not found");
        }

        Account updatedAccount;
        try {
            updatedAccount = systemAccountService.updateUserRole(userId, roleId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User role updated successfully");
        response.put("accountId", updatedAccount.getId());
        response.put("email", updatedAccount.getEmail());
        response.put("accountName", updatedAccount.getAccountName());
        response.put("role", resolveRoleName(updatedAccount.getRoleId()));
        response.put("isActive", updatedAccount.isActive());
        return response;
    }

    @Override
    public Map<String, Object> validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        if (email == null || !jwtUtil.validateToken(token, email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Account account = systemAccountService.findByEmail(email);
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("email", email);
        response.put("accountId", account.getId());
        response.put("accountName", account.getAccountName());
        response.put("role", resolveRoleName(account.getRoleId()));
        response.put("isActive", account.isActive());
        return response;
    }

    @Override
    public Map<String, Object> getCurrentUser(Authentication authentication) {
        Account account = systemAccountService.findByEmail(authentication.getName());
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Failed to get user info");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("accountId", account.getId());
        response.put("email", account.getEmail());
        response.put("accountName", account.getAccountName());
        response.put("role", resolveRoleName(account.getRoleId()));
        response.put("isActive", account.isActive());
        response.put("authorities", authentication.getAuthorities());
        response.put("principal", authentication.getPrincipal());
        return response;
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private String resolveRoleName(String roleId) {
        if (roleId == null) {
            return "USER";
        }
        try {
            return roleService.getRoleById(roleId).getRoleName();
        } catch (IllegalArgumentException ex) {
            return "USER";
        }
    }
}
