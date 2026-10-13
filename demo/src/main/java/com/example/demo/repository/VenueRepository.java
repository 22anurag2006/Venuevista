package com.example.demo.repository;

import com.example.demo.model.Venue;
import com.example.demo.common.enums.VenueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCityIgnoreCase(String city);
    List<Venue> findByTypeIgnoreCase(String type);
    List<Venue> findByStatus(VenueStatus status);
    List<Venue> findByCityIgnoreCaseAndStatus(String city, VenueStatus status);
    List<Venue> findByTypeIgnoreCaseAndStatus(String type, VenueStatus status);
    List<Venue> findByOwnerIdAndStatus(Long ownerId, VenueStatus status);
    List<Venue> findByOwnerId(Long ownerId);
}