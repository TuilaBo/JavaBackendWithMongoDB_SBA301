package dto.auth;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserRoleRequest {
    @NotBlank(message = "Role ID is required")
    private String roleId;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
