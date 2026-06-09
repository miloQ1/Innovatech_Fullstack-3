package com.innovatech.analitica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innovatech.analitica.model.DashboardLayout;

@Repository
public interface DashboardLayoutRepository extends JpaRepository<DashboardLayout, Long> {
}
