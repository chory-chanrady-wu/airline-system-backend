package com.example.backend.controller;

import com.example.backend.dto.request.PassengerCreateRequest;
import com.example.backend.dto.request.PassengerUpdateRequest;
import com.example.backend.service.PassengerService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping("/passengers")
    public Map<String, Object> listPassengers(@RequestParam(required = false) String search) {
        return passengerService.listPassengers(search);
    }

    @PostMapping("/passengers")
    public Map<String, Object> createPassenger(@RequestBody PassengerCreateRequest request) {
        return passengerService.createPassenger(request);
    }

    @GetMapping("/passengers/{id}")
    public Map<String, Object> getPassenger(@PathVariable Integer id) {
        return passengerService.getPassenger(id);
    }

    @PatchMapping("/passengers/{id}")
    public Map<String, Object> updatePassenger(@PathVariable Integer id, @RequestBody PassengerUpdateRequest request) {
        return passengerService.updatePassenger(id, request);
    }

    @DeleteMapping("/passengers/{id}")
    public Map<String, Object> deletePassenger(@PathVariable Integer id) {
        return passengerService.deletePassenger(id);
    }

    @GetMapping("/passengers/{passengerId}/bookings")
    public Map<String, Object> getPassengerBookings(@PathVariable Integer passengerId) {
        return passengerService.getPassengerBookings(passengerId);
    }

    @GetMapping("/passengers/{passengerId}/bookings/history")
    public Map<String, Object> getPassengerBookingHistory(@PathVariable Integer passengerId) {
        return passengerService.getPassengerBookingHistory(passengerId);
    }
}
