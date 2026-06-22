package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.Professional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

    List<Professional> findByStatus(String status);

    List<Professional> findByRoleName(String roleName);

    List<Professional> findBySeniority(String seniority);

    Optional<Professional> findByEmployeeCode(String employeeCode);
}
