package com.akertesz.task_manager_api.dto;

import java.util.Optional;

public class LoginResponse {
    private Optional<String> token;
    private String message;

    public LoginResponse(Optional<String> token, String message) {
        this.token = token;
        this.message = message;
    }

    public Optional<String> getToken() {
        return token;
    }

    public void setToken(Optional<String> token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
