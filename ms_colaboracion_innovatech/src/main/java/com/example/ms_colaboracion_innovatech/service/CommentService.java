package com.example.ms_colaboracion_innovatech.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ms_colaboracion_innovatech.dto.CommentRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.CommentResponseDTO;
import com.example.ms_colaboracion_innovatech.model.CollaborationThread;
import com.example.ms_colaboracion_innovatech.model.Comment;
import com.example.ms_colaboracion_innovatech.repository.CollaborationThreadRepository;
import com.example.ms_colaboracion_innovatech.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CollaborationThreadRepository threadRepository;

    public CommentService(CommentRepository commentRepository, CollaborationThreadRepository threadRepository) {
        this.commentRepository = commentRepository;
        this.threadRepository = threadRepository;
    }

    public List<CommentResponseDTO> findAll() {
        return commentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public CommentResponseDTO findById(Long id) {
        return commentRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public CommentResponseDTO save(CommentRequestDTO requestDTO) {
        CollaborationThread thread = threadRepository.findById(requestDTO.getThreadId()).orElse(null);
        if (thread == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setThread(thread);
        comment.setAuthorResourceId(requestDTO.getAuthorResourceId());
        comment.setContent(requestDTO.getContent());
        comment.setIsEdited(Boolean.FALSE);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        comment.setDeletedAt(null);
        return toResponse(commentRepository.save(comment));
    }

    public CommentResponseDTO update(Long id, CommentRequestDTO requestDTO) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            return null;
        }
        CollaborationThread thread = threadRepository.findById(requestDTO.getThreadId()).orElse(null);
        if (thread == null) {
            return null;
        }
        comment.setThread(thread);
        comment.setAuthorResourceId(requestDTO.getAuthorResourceId());
        comment.setContent(requestDTO.getContent());
        comment.setIsEdited(Boolean.TRUE);
        comment.setUpdatedAt(LocalDateTime.now());
        return toResponse(commentRepository.save(comment));
    }

    public boolean delete(Long id) {
        if (!commentRepository.existsById(id)) {
            return false;
        }
        commentRepository.deleteById(id);
        return true;
    }

    public List<CommentResponseDTO> findByThreadId(Long threadId) {
        return commentRepository.findByThreadThreadId(threadId).stream().map(this::toResponse).toList();
    }

    private CommentResponseDTO toResponse(Comment comment) {
        Long threadId = comment.getThread() != null ? comment.getThread().getThreadId() : null;
        return new CommentResponseDTO(
                comment.getCommentId(),
                threadId,
                comment.getAuthorResourceId(),
                comment.getContent(),
                comment.getIsEdited(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getDeletedAt());
    }
}
