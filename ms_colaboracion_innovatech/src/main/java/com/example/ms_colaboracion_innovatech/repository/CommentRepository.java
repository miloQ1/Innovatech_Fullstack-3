package com.example.ms_colaboracion_innovatech.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.ms_colaboracion_innovatech.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByThreadThreadId(Long threadId);
}
