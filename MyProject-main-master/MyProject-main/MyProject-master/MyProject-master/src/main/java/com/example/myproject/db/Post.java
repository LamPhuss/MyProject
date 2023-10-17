package com.example.myproject.db;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Post {
    private int postId;
    private String postSubject;
    private String postContent;
    private String postBy;
    private String postDate;
    private int postReplies;
    private String postByName;
}
