package main.model;

public enum Permissions {
    USER("user:write"),
    MODERATE("user:moderate");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
