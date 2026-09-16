package com.example.backend.controller;

import com.example.backend.dto.request.BookingCreateRequest;
import com.example.backend.dto.request.BookingUpdateRequest;
import com.example.backend.service.BookingService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings")
    public Map<String, Object> listBookings() {
        return bookingService.listBookings();
    }

    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody BookingCreateRequest request) {
        return bookingService.createBooking(request);
    }

    @GetMapping("/bookings/{bookingId}")
    public Map<String, Object> getBooking(@PathVariable String bookingId) {
        return bookingService.getBooking(bookingId);
    }

    @PatchMapping("/bookings/{bookingId}")
    public Map<String, Object> updateBooking(@PathVariable String bookingId, @RequestBody BookingUpdateRequest request) {
        return bookingService.updateBooking(bookingId, request);
    }

    @DeleteMapping("/bookings/{bookingId}")
    public Map<String, Object> deleteBooking(@PathVariable String bookingId) {
        return bookingService.deleteBooking(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public Map<String, Object> cancelBooking(@PathVariable String bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/undo")
    public Map<String, Object> undoCancellation(@PathVariable String bookingId) {
        return bookingService.undoCancellation(bookingId);
    }
}