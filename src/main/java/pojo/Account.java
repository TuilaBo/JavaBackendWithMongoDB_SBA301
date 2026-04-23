package pojo;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "account")
public class Account {
    @Id
    private String id;

    private String accountName;

    private String email;

    private String password;

    private boolean isActive = true;

    private String roleId;


    public Account() {
    }

    public Account(String id, String accountName, String email, String password, boolean isActive, String roleId) {
        this.id = id;
        this.accountName = accountName;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
        this.roleId = roleId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
}
