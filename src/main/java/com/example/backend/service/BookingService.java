package com.example.backend.service;

import com.example.backend.dto.request.BookingCreateRequest;
import com.example.backend.dto.request.BookingUpdateRequest;

import java.util.Map;

public interface BookingService {
    Map<String, Object> listBookings();

    Map<String, Object> createBooking(BookingCreateRequest request);

    Map<String, Object> getBooking(String bookingId);

    Map<String, Object> updateBooking(String bookingId, BookingUpdateRequest request);

    Map<String, Object> deleteBooking(String bookingId);

    Map<String, Object> cancelBooking(String bookingId);

    Map<String, Object> undoCancellation(String bookingId);
}

