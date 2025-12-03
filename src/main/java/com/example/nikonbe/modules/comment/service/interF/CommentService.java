package com.example.nikonbe.modules.comment.service.interF;

import com.example.nikonbe.modules.comment.dto.request.CommentCreateDTO;
import com.example.nikonbe.modules.comment.dto.request.CommentReplyDTO;
import com.example.nikonbe.modules.comment.dto.response.CommentResponseDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface CommentService {

  CommentResponseDTO create(CommentCreateDTO dto);

  CommentResponseDTO reply(CommentReplyDTO dto, Authentication authentication);

  CommentResponseDTO getById(Integer id);

  List<CommentResponseDTO> getByBlogId(Integer blogId, Boolean status);

  Page<CommentResponseDTO> getAll(Integer blogId, Boolean status, Pageable pageable);

  void delete(Integer id);

  CommentResponseDTO updateStatus(Integer id, Boolean status);
}


