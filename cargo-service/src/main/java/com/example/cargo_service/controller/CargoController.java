package com.example.cargo_service.controller;

import com.example.cargo_service.dto.CargoRequestDto;
import com.example.cargo_service.dto.CargoResponseDto;
import com.example.cargo_service.dto.CargoUpdateRequestDto;
import com.example.cargo_service.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cargo")
@RequiredArgsConstructor
public class CargoController {

    private final CargoService cargoService;

    @PostMapping
    @Operation(summary = "Create a new cargo",
            description = "Creates a new cargo entry in the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cargo created successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<CargoResponseDto> createCargo(@RequestBody CargoRequestDto cargoRequestDto) {
        CargoResponseDto cargo = cargoService.createCargo(cargoRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cargo);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all cargos",
            description = "Retrieves a list of all cargos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of cargos retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "No cargos found")
            })
    public ResponseEntity<List<CargoResponseDto>> getCargoList() {
        List<CargoResponseDto> cargoList = cargoService.getCargoList();
        return ResponseEntity.ok(cargoList);
    }

    @GetMapping("/{cargoId}")
    @Operation(summary = "Get cargo by ID",
            description = "Retrieves a cargo entry by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cargo retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cargo not found")
            })
    public ResponseEntity<CargoResponseDto> getCargoById(@PathVariable("cargoId") String cargoId) {
        CargoResponseDto cargo = cargoService.getCargoById(cargoId);
        return ResponseEntity.ok(cargo);
    }

    @GetMapping("order/{orderId}")
    @Operation(summary = "Get cargo by order ID",
            description = "Retrieves a cargo entry by its associated order ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cargo retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cargo not found")
            })
    public ResponseEntity<CargoResponseDto> getCargoOrderById(@PathVariable("orderId") String orderId) {
        CargoResponseDto cargo = cargoService.getCargoByOrderId(orderId);
        return ResponseEntity.ok(cargo);
    }

    @GetMapping("trackingNumber/{trackingNumber}")
    @Operation(summary = "Get cargo by tracking number",
            description = "Retrieves a cargo entry by its tracking number.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cargo retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cargo not found")
            })
    public ResponseEntity<CargoResponseDto> getCargoByTrackingNumber(@PathVariable("trackingNumber") String trackingNumber) {
        CargoResponseDto cargo = cargoService.getCargoByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(cargo);
    }

    @PutMapping
    @Operation(summary = "Update cargo",
            description = "Updates an existing cargo entry.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cargo updated successfully",
                            content = @Content(schema = @Schema(implementation = CargoResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    public ResponseEntity<CargoResponseDto> updateCargo(@RequestBody CargoUpdateRequestDto cargoUpdateRequestDto) {
        CargoResponseDto updatedCargo = cargoService.updateCargo(cargoUpdateRequestDto);
        return ResponseEntity.ok(updatedCargo);
    }

    @DeleteMapping("/{cargoId}")
    @Operation(summary = "Delete cargo by ID",
            description = "Deletes a cargo entry by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cargo deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Cargo not found")
            })
    public ResponseEntity<Void> deleteCargoById(@PathVariable("cargoId") String cargoId) {
        try {
            cargoService.deleteCargoById(cargoId);
            return ResponseEntity.noContent().build();
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

}
