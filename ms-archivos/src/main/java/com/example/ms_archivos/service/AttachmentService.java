package com.example.ms_archivos.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ms_archivos.dto.AttachmentRequestDTO;
import com.example.ms_archivos.dto.AttachmentResponseDTO;
import com.example.ms_archivos.model.Attachment;
import com.example.ms_archivos.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    public List<AttachmentResponseDTO> findAll() {
        return attachmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AttachmentResponseDTO findById(Long id) {
        return attachmentRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public AttachmentResponseDTO save(AttachmentRequestDTO requestDTO) {
        Attachment attachment = new Attachment();
        attachment.setCommentId(requestDTO.getCommentId());
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
        attachment.setCommentId(requestDTO.getCommentId());
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
        return attachmentRepository.findByCommentId(commentId).stream().map(this::toResponse).toList();
    }

    private AttachmentResponseDTO toResponse(Attachment attachment) {
        return new AttachmentResponseDTO(
                attachment.getAttachmentId(),
                attachment.getCommentId(),
                attachment.getFileName(),
                attachment.getFileUrl(),
                attachment.getMimeType(),
                attachment.getSizeBytes(),
                attachment.getUploadedAt());
    }
}
