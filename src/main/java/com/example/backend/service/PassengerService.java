package com.example.backend.service;

import com.example.backend.dto.request.PassengerCreateRequest;
import com.example.backend.dto.request.PassengerUpdateRequest;

import java.util.Map;

public interface PassengerService {
    Map<String, Object> listPassengers(String search);

    Map<String, Object> createPassenger(PassengerCreateRequest request);

    Map<String, Object> getPassenger(Integer id);

    Map<String, Object> updatePassenger(Integer id, PassengerUpdateRequest request);

    Map<String, Object> deletePassenger(Integer id);

    Map<String, Object> getPassengerBookings(Integer passengerId);

    Map<String, Object> getPassengerBookingHistory(Integer passengerId);
}

