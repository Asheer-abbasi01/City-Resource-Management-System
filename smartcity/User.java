package smartcity;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1941305537978736861L;
    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}