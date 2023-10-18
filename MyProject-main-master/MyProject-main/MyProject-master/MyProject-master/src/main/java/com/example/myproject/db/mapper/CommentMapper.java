package com.example.myproject.db.mapper;

import com.example.myproject.db.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface CommentMapper {
    void insert(Map<String, Object> reqMap) throws Exception;
    List<Comment> getPostDetail(Map<String, Object> reqMap) throws Exception;
    void updateCommentOfPost(Map<String, Object> reqMap) throws Exception;
    void deleteCommentByPost(Map<String, Object> reqMap) throws Exception;
}
