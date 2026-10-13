package com.example.demo.controller;

import com.example.demo.model.Venue;
import com.example.demo.repository.VenueRepository;
import com.example.demo.common.enums.VenueStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    @GetMapping
    public List<Venue> getAllVenues() {
        // Only return approved venues to regular users
        return venueRepository.findByStatus(VenueStatus.APPROVED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        return venueRepository.findById(id)
                .filter(venue -> venue.getStatus() == VenueStatus.APPROVED)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/city")
    public List<Venue> getVenuesByCity(@RequestParam String city) {
        return venueRepository.findByCityIgnoreCaseAndStatus(city, VenueStatus.APPROVED);
    }

    @GetMapping("/search/type")
    public List<Venue> getVenuesByType(@RequestParam String type) {
        return venueRepository.findByTypeIgnoreCaseAndStatus(type, VenueStatus.APPROVED);
    }

    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody Venue venue) {
        // New venues are created in PENDING status by default
        venue.setStatus(VenueStatus.PENDING);
        Venue savedVenue = venueRepository.save(venue);
        return ResponseEntity.ok(savedVenue);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Venue> getVenuesByOwner(@PathVariable Long ownerId) {
        return venueRepository.findByOwnerId(ownerId);
    }
}