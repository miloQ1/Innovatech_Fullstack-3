package com.example.ms_colaboracion_innovatech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_colaboracion_innovatech.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByCommentCommentId(Long commentId);
}
