package com.example.myproject.Service;

import com.example.myproject.db.Post;
import com.example.myproject.db.User;
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
public class PostService {
    @Autowired
    private PostMapper postMapper;
    public List<Post> listPostBySubject(String keyword) throws Exception{
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("keyword", keyword);
        return postMapper.listBySubject(reqMap);
    }
    public boolean addPost(Post post) throws Exception {
        String subject = post.getPostSubject();
        String content = post.getPostContent();
        Map<String, Object> reqList= new HashMap<>();
        reqList.put("keyword","");
        int id = (postMapper.listBySubject(reqList)).size() + 1;
        String postBy = post.getPostBy();
        int postReplies = post.getPostReplies();
        Map<String, Object> reqMap = new HashMap<>();
        try {
            if (!StringUtils.hasText(subject)) {
                return false;
            } else {
                reqMap.put("postSubject",subject );
                reqMap.put("postId", id);
            }
            if (!StringUtils.hasText(content)){
                return false;
            } else {
                reqMap.put("postContent", content);
            }
            if (!StringUtils.hasText(postBy)){
                return false;
            } else {
                reqMap.put("postBy", postBy);
            }
            if (postReplies < 0){
                return false;
            } else {
                reqMap.put("postReplies", postReplies);
            }
            postMapper.insertPost(reqMap);
            return true;

        } catch (Exception e){
            throw e;
        }
    }

    public boolean updatePostReplies(int postId, int replies) throws Exception {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("postId", postId);
        if (replies <= 0) {
            return false;
        } else {
            reqMap.put("postReplies", replies);
            postMapper.updatePostReplies(reqMap);
            return true;
        }
    }
    public List<Post> listTopViewedPost() throws Exception{
        return postMapper.listTopViewedPost();
    }
    public List<Post> listMostRecentPost() throws Exception{
        return postMapper.listMostRecentPost();
    }
    public Post findPostBySubjectOrId(String subject,String id,String postId) throws Exception{
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("postSubject",subject);
        reqMap.put("postBy",id);
        reqMap.put("postId",postId);
        return postMapper.findPostBySubjectOrId(reqMap);
    }
    public List<Post> listPostByUser(String id) throws Exception{
        Map<String,Object> reqMap = new HashMap<>();
        reqMap.put("postBy", id);
        return postMapper.listPostByUser(reqMap);
    }
    public boolean updatePostById(Post post) throws Exception {
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("postSubject", post.getPostSubject());
        reqMap.put("postContent", post.getPostContent());
        reqMap.put("postId", post.getPostId());
        postMapper.updatePostById(reqMap);
        return true;

    }
}
