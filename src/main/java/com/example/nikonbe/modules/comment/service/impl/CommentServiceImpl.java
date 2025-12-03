package com.example.nikonbe.modules.comment.service.impl;

import com.example.nikonbe.common.exceptions.ResourceNotFoundException;
import com.example.nikonbe.modules.blog.repository.BlogRepository;
import com.example.nikonbe.modules.comment.dto.request.CommentCreateDTO;
import com.example.nikonbe.modules.comment.dto.request.CommentReplyDTO;
import com.example.nikonbe.modules.comment.dto.response.CommentResponseDTO;
import com.example.nikonbe.modules.comment.entity.Comment;
import com.example.nikonbe.modules.comment.mapper.CommentMapper;
import com.example.nikonbe.modules.comment.repository.CommentRepository;
import com.example.nikonbe.modules.comment.service.interF.CommentService;
import com.example.nikonbe.modules.customer.repository.CustomerRepository;
import com.example.nikonbe.modules.staff.entity.Staff;
import com.example.nikonbe.modules.staff.repository.StaffRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;
  private final BlogRepository blogRepository;
  private final CustomerRepository customerRepository;
  private final StaffRepository staffRepository;

  @Override
  public CommentResponseDTO create(CommentCreateDTO dto) {
    blogRepository
        .findById(dto.getBlogId())
        .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));

    if (dto.getCustomerId() != null && dto.getCustomerId() != 1) {
      customerRepository
          .findById(dto.getCustomerId())
          .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));
    }

    if (dto.getCustomerId() == null) {
      dto.setCustomerId(1);
    }

    Comment comment = commentMapper.toEntity(dto);
    Comment savedComment = commentRepository.save(comment);

    Comment commentWithRelations =
        commentRepository
            .findByIdWithRelations(savedComment.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy comment"));

    return commentMapper.toDto(commentWithRelations);
  }

  @Override
  public CommentResponseDTO reply(CommentReplyDTO dto, Authentication authentication) {
    blogRepository
        .findById(dto.getBlogId())
        .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));

    commentRepository
        .findById(dto.getParentId())
        .orElseThrow(() -> new ResourceNotFoundException("Comment cha không tồn tại"));

    Comment reply = commentMapper.toEntityFromReply(dto);

    if (authentication != null && authentication.isAuthenticated()) {
      try {
        String login = authentication.getName();
        Optional<Staff> staffOpt = staffRepository.findByUsername(login);
        if (staffOpt.isEmpty()) {
          staffOpt = staffRepository.findByEmail(login);
        }
        
        if (staffOpt.isPresent()) {
          Staff currentStaff = staffOpt.get();
          reply.setStaff(currentStaff);
          reply.setStatus(true);
          if (dto.getUserComment() == null || dto.getUserComment().isEmpty()) {
            reply.setUserComment("Chăm sóc khách hàng");
          } else {
            reply.setUserComment(dto.getUserComment());
          }
        }
      } catch (Exception e) {
        log.warn("Không thể lấy thông tin staff: {}", e.getMessage());
      }
    } else {
      if (dto.getUserComment() != null) {
        reply.setUserComment(dto.getUserComment());
      }
    }

    if (reply.getStaff() == null) {
      if (dto.getCustomerId() != null && dto.getCustomerId() != 1) {
        customerRepository
            .findById(dto.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Khách hàng không tồn tại"));
      }

      if (dto.getCustomerId() == null) {
        dto.setCustomerId(1);
      }
    }

    Comment savedReply = commentRepository.save(reply);

    Comment replyWithRelations =
        commentRepository
            .findByIdWithRelations(savedReply.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy reply"));

    return commentMapper.toDto(replyWithRelations);
  }

  @Override
  @Transactional(readOnly = true)
  public CommentResponseDTO getById(Integer id) {
    Comment comment =
        commentRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment không tồn tại"));

    if (comment.getParent() == null) {
      List<Comment> replies = commentRepository.findRepliesByParentId(id);
      comment.setReplies(replies);
    }

    return commentMapper.toDto(comment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CommentResponseDTO> getByBlogId(Integer blogId, Boolean status) {
    List<Comment> topLevelComments;
    if (status != null) {
      topLevelComments =
          commentRepository.findTopLevelCommentsByBlogIdAndStatus(blogId, status);
    } else {
      topLevelComments = commentRepository.findTopLevelCommentsByBlogId(blogId);
    }

    return topLevelComments.stream()
        .map(
            comment -> {
              List<Comment> replies = commentRepository.findRepliesByParentId(comment.getId());
              comment.setReplies(replies);
              return commentMapper.toDto(comment);
            })
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CommentResponseDTO> getAll(Integer blogId, Boolean status, Pageable pageable) {
    Page<Comment> comments = commentRepository.findAllWithFilters(blogId, status, pageable);
    return comments.map(
        comment -> {
          if (comment.getParent() == null) {
            List<Comment> replies = commentRepository.findRepliesByParentId(comment.getId());
            comment.setReplies(replies);
          }
          return commentMapper.toDto(comment);
        });
  }

  @Override
  public void delete(Integer id) {
    Comment comment =
        commentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment không tồn tại"));
    commentRepository.delete(comment);
  }

  @Override
  public CommentResponseDTO updateStatus(Integer id, Boolean status) {
    Comment comment =
        commentRepository
            .findByIdWithRelations(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment không tồn tại"));

    comment.setStatus(status);
    Comment updatedComment = commentRepository.save(comment);

    if (comment.getParent() == null) {
      List<Comment> replies = commentRepository.findRepliesByParentId(id);
      updatedComment.setReplies(replies);
    }

    return commentMapper.toDto(updatedComment);
  }
}


