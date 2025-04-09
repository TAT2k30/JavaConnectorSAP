package com.example.demo.util.enums;

public enum AuthEnums {
    INVALID_REFRESH_TOKEN("AUTH_E001", "Refresh token is invalid."),
    EXPIRED_REFRESH_TOKEN("AUTH_E002", "Refresh token has expired."),
    UNAUTHORIZED_ACCESS("AUTH_E003", "You are not authorized to perform this action."),
    USER_NOT_FOUND("AUTH_E004", "User not found."),
    SERVER_ERROR("AUTH_E005", "An unexpected error occurred. Please try again later."),
    INVALID_JWT_TOKEN_CLAIMS("AUTH_E006", "Invalid JWT Token or Claims."),
    INVALID_SESSION_ACCOUNT("AUTH_E007", "Invalid Session Account");

    private final String code;
    private final String message;

    AuthEnums(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
