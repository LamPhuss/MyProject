package com.example.myproject.Service;

import com.example.myproject.db.Comment;
import com.example.myproject.db.Post;
import com.example.myproject.db.mapper.CommentMapper;
import com.example.myproject.db.mapper.PostMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    public boolean addComment(Comment comment) throws Exception
    {
        String commentBy = comment.getCommentBy();
        String commentTo = comment.getCommentTo();
        String commentContent = comment.getCommentContent();
        int id = comment.getCommentId();
        int commentOfPost = comment.getCommentOfPost();
        Map<String, Object> reqMap = new HashMap<>();
        try {
            if (id < 0){
                return false;
            }
            else{
                reqMap.put("commentId", id);
            }
            if (!StringUtils.hasText(commentContent)) {
                return false;
            } else {
                reqMap.put("commentContent", commentContent);
            }
            if (commentOfPost < 0){
                return false;
            } else {
                reqMap.put("commentOfPost", commentOfPost);
            }
            if (!StringUtils.hasText(commentTo)){
                return false;
            } else {
                reqMap.put("commentBy", commentBy);
            }
            if (!StringUtils.hasText(commentTo)){
                return false;
            } else {
                reqMap.put("commentTo", commentTo);
            }
            commentMapper.insert(reqMap);
            return true;

        } catch (Exception e){
            throw e;
        }
    }
    public List<Comment> listCommentByPost(int id) throws Exception{
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("commentOfPost", id);
        return commentMapper.getPostDetail(reqMap);
    }
    public boolean updateCommentOfPost(String commentContent, int commentOfPost) throws Exception
    {
        Map<String, Object> reqMap = new HashMap<>();
        try {
            if (!StringUtils.hasText(commentContent)) {
                return false;
            } else {
                reqMap.put("commentContent", commentContent);
            }
            if (commentOfPost < 0){
                return false;
            } else {
                reqMap.put("commentOfPost", commentOfPost);
            }
            commentMapper.updateCommentOfPost(reqMap);
            return true;

        } catch (Exception e){
            throw e;
        }
    }
}
