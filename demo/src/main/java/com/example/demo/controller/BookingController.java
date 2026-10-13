package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.model.Venue;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired(required = false)
    private VenueRepository venueRepository;

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String username) {
        
        if (userId != null) {
            return ResponseEntity.ok(bookingRepository.findByUserId(userId));
        }
        
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("=== RECEIVED BOOKING PAYLOAD ===");
            System.out.println(payload);

            Booking booking = new Booking();

        // 1. Map userId safely with a fallback default of 1
            if (payload.get("userId") != null) {
                try {
                    booking.setUserId(Long.valueOf(payload.get("userId").toString()));
                } catch (Exception e) {
                    booking.setUserId(1L);
                }
            } else {
                booking.setUserId(1L); // Prevents "Column user_id cannot be null" error
            }

            // 2. Map Venue using venueId
            if (payload.get("venueId") != null && venueRepository != null) {
                Long venueId = Long.valueOf(payload.get("venueId").toString());
                Optional<Venue> venueOpt = venueRepository.findById(venueId);
                if (venueOpt.isPresent()) {
                    booking.setVenue(venueOpt.get());
                } else {
                    List<Venue> allVenues = venueRepository.findAll();
                    if (!allVenues.isEmpty()) {
                        booking.setVenue(allVenues.get(0));
                    }
                }
            }

            // 3. Map Event Date safely
            if (payload.get("date") != null) {
                try {
                    String dateStr = payload.get("date").toString().split("T")[0];
                    booking.setEventDate(LocalDate.parse(dateStr));
                } catch (Exception e) {
                    booking.setEventDate(LocalDate.now());
                }
            } else {
                booking.setEventDate(LocalDate.now());
            }

            // 4. Map Guest Count safely
            if (payload.get("guests") != null) {
                try {
                    booking.setGuestCount(Integer.valueOf(payload.get("guests").toString()));
                } catch (Exception e) {
                    booking.setGuestCount(1);
                }
            } else {
                booking.setGuestCount(1);
            }

            // 5. Map Total Price safely
            if (payload.get("total") != null) {
                try {
                    booking.setTotalPrice(new BigDecimal(payload.get("total").toString()));
                } catch (Exception e) {
                    booking.setTotalPrice(new BigDecimal("5000"));
                }
            } else {
                booking.setTotalPrice(new BigDecimal("5000"));
            }

            booking.setStatus("CONFIRMED");

            Booking savedBooking = bookingRepository.save(booking);
            return ResponseEntity.ok(savedBooking);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error saving booking: " + e.getMessage());
        }
    }
}