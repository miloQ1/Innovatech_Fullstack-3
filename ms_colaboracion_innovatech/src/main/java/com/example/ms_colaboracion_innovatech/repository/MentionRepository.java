package com.example.ms_colaboracion_innovatech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_colaboracion_innovatech.model.Mention;

public interface MentionRepository extends JpaRepository<Mention, Long> {

    List<Mention> findByMentionedResourceId(Long resourceId);
}
