package com.skillbridge.model;

public enum Role {
    STUDENT,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
