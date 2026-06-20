package com.example.ms_colaboracion_innovatech.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ms_colaboracion_innovatech.dto.AttachmentRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.AttachmentResponseDTO;
import com.example.ms_colaboracion_innovatech.model.Attachment;
import com.example.ms_colaboracion_innovatech.model.Comment;
import com.example.ms_colaboracion_innovatech.repository.AttachmentRepository;
import com.example.ms_colaboracion_innovatech.repository.CommentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, CommentRepository commentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.commentRepository = commentRepository;
    }

    public List<AttachmentResponseDTO> findAll() {
        return attachmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AttachmentResponseDTO findById(Long id) {
        return attachmentRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public AttachmentResponseDTO save(AttachmentRequestDTO requestDTO) {
        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElse(null);
        if (comment == null) {
            return null;
        }
        Attachment attachment = new Attachment();
        attachment.setComment(comment);
        attachment.setFileName(requestDTO.getFileName());
        attachment.setFileUrl(requestDTO.getFileUrl());
        attachment.setMimeType(requestDTO.getMimeType());
        attachment.setSizeBytes(requestDTO.getSizeBytes());
        attachment.setUploadedAt(LocalDateTime.now());
        return toResponse(attachmentRepository.save(attachment));
    }

    public AttachmentResponseDTO update(Long id, AttachmentRequestDTO requestDTO) {
        Attachment attachment = attachmentRepository.findById(id).orElse(null);
        if (attachment == null) {
            return null;
        }
        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElse(null);
        if (comment == null) {
            return null;
        }
        attachment.setComment(comment);
        attachment.setFileName(requestDTO.getFileName());
        attachment.setFileUrl(requestDTO.getFileUrl());
        attachment.setMimeType(requestDTO.getMimeType());
        attachment.setSizeBytes(requestDTO.getSizeBytes());
        return toResponse(attachmentRepository.save(attachment));
    }

    public boolean delete(Long id) {
        if (!attachmentRepository.existsById(id)) {
            return false;
        }
        attachmentRepository.deleteById(id);
        return true;
    }

    public List<AttachmentResponseDTO> findByCommentId(Long commentId) {
        return attachmentRepository.findByCommentCommentId(commentId).stream().map(this::toResponse).toList();
    }

    private AttachmentResponseDTO toResponse(Attachment attachment) {
        Long commentId = attachment.getComment() != null ? attachment.getComment().getCommentId() : null;
        return new AttachmentResponseDTO(
                attachment.getAttachmentId(),
                commentId,
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getMimeType(),
                attachment.getSizeBytes(),
                attachment.getUploadedAt());
    }
}
