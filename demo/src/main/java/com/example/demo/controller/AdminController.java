package com.example.demo.controller;

import com.example.demo.model.Venue;
import com.example.demo.model.Booking;
import com.example.demo.repository.VenueRepository;
import com.example.demo.repository.BookingRepository;
import com.example.demo.common.enums.VenueStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Get all pending venues for review
    @GetMapping("/venues/pending")
    public List<Venue> getPendingVenues() {
        return venueRepository.findByStatus(VenueStatus.PENDING);
    }

    // Get all approved venues
    @GetMapping("/venues/approved")
    public List<Venue> getApprovedVenues() {
        return venueRepository.findByStatus(VenueStatus.APPROVED);
    }

    // Get all rejected venues
    @GetMapping("/venues/rejected")
    public List<Venue> getRejectedVenues() {
        return venueRepository.findByStatus(VenueStatus.REJECTED);
    }

    // Approve a venue
    @PatchMapping("/venues/{id}/approve")
    public ResponseEntity<Venue> approveVenue(@PathVariable Long id) {
        return venueRepository.findById(id)
                .map(venue -> {
                    venue.setStatus(VenueStatus.APPROVED);
                    Venue updated = venueRepository.save(venue);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Reject a venue
    @PatchMapping("/venues/{id}/reject")
    public ResponseEntity<Venue> rejectVenue(@PathVariable Long id) {
        return venueRepository.findById(id)
                .map(venue -> {
                    venue.setStatus(VenueStatus.REJECTED);
                    Venue updated = venueRepository.save(venue);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all bookings
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get analytics dashboard
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalVenues", venueRepository.count());
        analytics.put("approvedVenues", venueRepository.findByStatus(VenueStatus.APPROVED).size());
        analytics.put("pendingVenues", venueRepository.findByStatus(VenueStatus.PENDING).size());
        analytics.put("rejectedVenues", venueRepository.findByStatus(VenueStatus.REJECTED).size());
        
        List<Booking> allBookings = bookingRepository.findAll();
        analytics.put("totalBookings", allBookings.size());
        
        // You can add more analytics like revenue, booking trends, etc.
        
        return ResponseEntity.ok(analytics);
    }
}
