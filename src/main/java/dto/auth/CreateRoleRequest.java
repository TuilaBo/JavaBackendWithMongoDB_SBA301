package dto.auth;

import jakarta.validation.constraints.NotBlank;

public class CreateRoleRequest {
    @NotBlank(message = "Role name is required")
    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
