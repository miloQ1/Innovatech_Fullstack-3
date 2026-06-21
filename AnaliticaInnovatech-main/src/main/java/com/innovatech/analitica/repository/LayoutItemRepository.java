package com.innovatech.analitica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.innovatech.analitica.model.LayoutItem;

@Repository
public interface LayoutItemRepository extends JpaRepository<LayoutItem, Long> {
    List<LayoutItem> findByDashboardLayoutLayoutIdOrderByDisplayOrderAsc(Long layoutId);
}
