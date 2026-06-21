package com.example.ms_archivos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_archivos.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByCommentId(Long commentId);
}
