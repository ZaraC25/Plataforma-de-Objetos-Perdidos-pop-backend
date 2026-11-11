package edu.universidad.pop.common.dto;

import java.util.Map;

/**
 * Formato uniforme de respuesta para todos los endpoints.
 * { success, message, data, errors }
 */
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Map<String, String> errors
) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, Map<String, String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }
}