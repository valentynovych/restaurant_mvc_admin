package com.restaurant.restaurant_admin.model;

import lombok.Builder;

import java.util.Map;

@Builder
public class JsonResponse {
    private String status;
    private String message;
    private Map<String, String> fieldsErrorsMap;

    public JsonResponse() {
    }

    public JsonResponse(String status, String message, Map<String, String> fieldsErrorsMap) {
        this.status = status;
        this.message = message;
        this.fieldsErrorsMap = fieldsErrorsMap;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getFieldsErrorsMap() {
        return fieldsErrorsMap;
    }

    public void setFieldsErrorsMap(Map<String, String> fieldsErrorsMap) {
        this.fieldsErrorsMap = fieldsErrorsMap;
    }
}
