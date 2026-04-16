package controller;

import dto.auth.CreateRoleRequest;
import dto.auth.LoginRequest;
import dto.auth.RegisterRequest;
import dto.auth.UpdateUserRoleRequest;
import dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pojo.Role;
import service.AuthService;
import service.RoleService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and role management")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleService roleService;

    @Operation(summary = "User login", description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> data = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Login successful", data));
    }

    @Operation(summary = "User registration", description = "Registers a new user account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User registered successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Map<String, Object> data = authService.register(
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getAccountName()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "User registered successfully", data));
    }

    @Operation(summary = "Create new role", description = "Creates a new role")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created successfully",
                    content = @Content(schema = @Schema(implementation = Role.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Role already exists")
    })
    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRole(@Valid @RequestBody CreateRoleRequest createRoleRequest) {
        Map<String, Object> data = authService.createRole(createRoleRequest.getRoleName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Role created successfully", data));
    }

    @Operation(summary = "Get all roles", description = "Returns list of all roles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved roles")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        List<Role> data = roleService.getAllRoles();
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Roles retrieved successfully", data));
    }

    @Operation(summary = "Update user role", description = "Updates user's role")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User or role not found")
    })
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUserRole(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        Map<String, Object> data = authService.updateUserRole(userId, request.getRoleId());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "User role updated successfully", data));
    }

    @Operation(summary = "Validate token", description = "Validates the JWT token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token is valid"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is invalid")
    })
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> data = authService.validateToken(authHeader);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Token is valid", data));
    }

    @Operation(summary = "Get current user info", description = "Returns current user information and authorities")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> data = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Current user retrieved successfully", data));
    }

    @Operation(summary = "Test admin access", description = "Test endpoint for admin access")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Admin access granted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/test-admin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testAdminAccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Admin access granted");
        data.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Admin access granted", data));
    }

    @Operation(summary = "Test user access", description = "Test endpoint for user access")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User access granted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/test-user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testUserAccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "User access granted");
        data.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "User access granted", data));
    }

    @Operation(summary = "Test authenticated access", description = "Test endpoint for any authenticated user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access granted"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/test-auth")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testAuthenticatedAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Authenticated access granted");
        data.put("user", authentication.getName());
        data.put("authorities", authentication.getAuthorities());
        data.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Authenticated access granted", data));
    }
}
