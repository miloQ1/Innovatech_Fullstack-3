package com.example.ms_colaboracion_innovatech.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.ms_colaboracion_innovatech.dto.MentionRequestDTO;
import com.example.ms_colaboracion_innovatech.dto.MentionResponseDTO;
import com.example.ms_colaboracion_innovatech.model.Comment;
import com.example.ms_colaboracion_innovatech.model.Mention;
import com.example.ms_colaboracion_innovatech.repository.CommentRepository;
import com.example.ms_colaboracion_innovatech.repository.MentionRepository;

@Service
public class MentionService {

    private final MentionRepository mentionRepository;
    private final CommentRepository commentRepository;

    public MentionService(MentionRepository mentionRepository, CommentRepository commentRepository) {
        this.mentionRepository = mentionRepository;
        this.commentRepository = commentRepository;
    }

    public List<MentionResponseDTO> findAll() {
        return mentionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public MentionResponseDTO findById(Long id) {
        return mentionRepository.findById(id).map(this::toResponse).orElse(null);
    }

    public MentionResponseDTO save(MentionRequestDTO requestDTO) {
        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElse(null);
        if (comment == null) {
            return null;
        }
        Mention mention = new Mention();
        mention.setComment(comment);
        mention.setMentionedResourceId(requestDTO.getMentionedResourceId());
        mention.setMentionStatus(requestDTO.getMentionStatus());
        mention.setCreatedAt(LocalDateTime.now());
        return toResponse(mentionRepository.save(mention));
    }

    public MentionResponseDTO update(Long id, MentionRequestDTO requestDTO) {
        Mention mention = mentionRepository.findById(id).orElse(null);
        if (mention == null) {
            return null;
        }
        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElse(null);
        if (comment == null) {
            return null;
        }
        mention.setComment(comment);
        mention.setMentionedResourceId(requestDTO.getMentionedResourceId());
        mention.setMentionStatus(requestDTO.getMentionStatus());
        return toResponse(mentionRepository.save(mention));
    }

    public boolean delete(Long id) {
        if (!mentionRepository.existsById(id)) {
            return false;
        }
        mentionRepository.deleteById(id);
        return true;
    }

    public List<MentionResponseDTO> findByResourceId(Long resourceId) {
        return mentionRepository.findByMentionedResourceId(resourceId).stream().map(this::toResponse).toList();
    }

    private MentionResponseDTO toResponse(Mention mention) {
        Long commentId = mention.getComment() != null ? mention.getComment().getCommentId() : null;
        return new MentionResponseDTO(
                mention.getMentionId(),
                commentId,
                mention.getMentionedResourceId(),
                mention.getMentionStatus(),
                mention.getCreatedAt());
    }
}
