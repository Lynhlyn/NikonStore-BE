package com.example.nikonbe.modules.comment.mapper;

import com.example.nikonbe.modules.blog.entity.Blog;
import com.example.nikonbe.modules.comment.dto.request.CommentCreateDTO;
import com.example.nikonbe.modules.comment.dto.request.CommentReplyDTO;
import com.example.nikonbe.modules.comment.dto.response.CommentResponseDTO;
import com.example.nikonbe.modules.comment.entity.Comment;
import com.example.nikonbe.modules.customer.entity.Customer;
import com.example.nikonbe.modules.customer.mapper.CustomerMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface CommentMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blog", expression = "java(fromBlogId(dto.getBlogId()))")
  @Mapping(target = "customer", expression = "java(fromCustomerId(dto.getCustomerId()))")
  @Mapping(target = "parent", ignore = true)
  @Mapping(target = "replies", ignore = true)
  @Mapping(target = "status", constant = "false")
  Comment toEntity(CommentCreateDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "blog", expression = "java(fromBlogId(dto.getBlogId()))")
  @Mapping(target = "customer", expression = "java(fromCustomerId(dto.getCustomerId()))")
  @Mapping(target = "parent", expression = "java(fromParentId(dto.getParentId()))")
  @Mapping(target = "replies", ignore = true)
  @Mapping(target = "status", constant = "false")
  Comment toEntityFromReply(CommentReplyDTO dto);

  @Mapping(target = "blogId", source = "blog.id")
  @Mapping(target = "parentId", source = "parent.id")
  @Mapping(target = "replies", expression = "java(mapReplies(entity.getReplies()))")
  CommentResponseDTO toDto(Comment entity);

  default Blog fromBlogId(Integer id) {
    if (id == null) return null;
    Blog blog = new Blog();
    blog.setId(id);
    return blog;
  }

  default Customer fromCustomerId(Integer id) {
    if (id == null) {
      Customer customer = new Customer();
      customer.setId(1);
      return customer;
    }
    Customer customer = new Customer();
    customer.setId(id);
    return customer;
  }

  default Comment fromParentId(Integer id) {
    if (id == null) return null;
    Comment parent = new Comment();
    parent.setId(id);
    return parent;
  }

  default List<CommentResponseDTO> mapReplies(List<Comment> replies) {
    if (replies == null || replies.isEmpty()) return null;
    return replies.stream().map(this::toDto).collect(Collectors.toList());
  }
}


