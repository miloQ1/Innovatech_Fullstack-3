package com.innovatech.analitica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innovatech.analitica.model.Widget;

@Repository
public interface WidgetRepository extends JpaRepository<Widget, Long> {
    List<Widget> findByIsActiveTrue();
}
