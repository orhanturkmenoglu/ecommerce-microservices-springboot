package com.example.spring.boot.model;

import com.example.spring.boot.enums.CargoStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Cargo implements Serializable {

    private String id;

    private String orderId;

    private String trackingNumber;

    private CargoStatus status;

    private LocalDateTime lastUpdated;
}
