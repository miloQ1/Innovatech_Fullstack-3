package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.Availability;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByProfessionalResourceId(Long resourceId);

    List<Availability> findByAvailabilityType(String availabilityType);
}
