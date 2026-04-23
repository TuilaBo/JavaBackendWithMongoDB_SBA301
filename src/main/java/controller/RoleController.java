package controller;
import dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pojo.Role;
import service.RoleService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "APIs for managing roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * Endpoint to insert a new role.
     */

    @Operation(summary = "Create a new role", description = "Creates a new role in the system")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Role created successfully",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> insertRole(@RequestBody Role role) {
        Role data = roleService.insertRole(role);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Role created successfully", data));
    }

    @Operation(summary = "Get role by ID", description = "Retrieves a role by its ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role found",
            content = @Content(schema = @Schema(implementation = Role.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable String id) {
        Role data = roleService.getRoleById(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Role retrieved successfully", data));
    }

    @Operation(summary = "Update an existing role", description = "Updates the details of an existing role")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Role updated successfully",
                    content = @Content(schema = @Schema(implementation = Role.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(@PathVariable String id, @RequestBody Role role) {
        Role data = roleService.updateRole(id, role);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Role updated successfully", data));
    }

    @Operation(summary = "Delete a role", description = "Deletes a role from the system")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Role deleted successfully")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Role not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRole(@PathVariable String id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Role deleted successfully", null));
    }


}
