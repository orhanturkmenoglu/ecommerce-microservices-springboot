package com.example.cargo_service.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CargoStatus {
    PENDING("PENDING"),     // Beklemede
    SHIPPED("SHIPPED"),    // Gönderildi
    IN_TRANSIT("IN_TRANSIT"),// Yolda
    DELIVERED("DELIVERED"),   // Teslim Edildi
    RETURNED("RETURNED"),    // İade Edildi
    CANCELLED("CANCELLED");   // İptal Edildi

    private final String value;

    CargoStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
