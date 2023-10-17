package com.example.myproject.db;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Comment {
    private int commentId;
    private int commentOfPost;
    private String commentTo;
    private String commentBy;
    private String commentContent;
    private String commentDate;
    private String userCommentToName;
    private String userCommentByName;
}
