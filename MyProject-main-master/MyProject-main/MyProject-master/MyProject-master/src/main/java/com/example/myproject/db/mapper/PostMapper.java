package com.example.myproject.db.mapper;

import com.example.myproject.db.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {
    List<Post> listBySubject (Map<String,Object> reqMap) throws Exception;
    void insertPost (Map<String,Object> reqMap) throws Exception;
    void updatePostReplies (Map<String,Object> reqMap) throws Exception;
    List<Post> listTopViewedPost () throws Exception;
    List<Post> listMostRecentPost () throws Exception;
    Post findPostBySubjectOrId (Map<String,Object> reqMap) throws Exception;
    List<Post> listPostByUser (Map<String, Object> reqMap) throws Exception;
    void updatePostById (Map<String,Object> reqMap) throws Exception;
    void deletePost(Map<String,Object> reqMap) throws Exception;
}
