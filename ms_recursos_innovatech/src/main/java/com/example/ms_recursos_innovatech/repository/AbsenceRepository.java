package com.example.ms_recursos_innovatech.repository;

import com.example.ms_recursos_innovatech.model.Absence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    List<Absence> findByProfessionalResourceId(Long resourceId);

    List<Absence> findByAbsenceType(String absenceType);
}
