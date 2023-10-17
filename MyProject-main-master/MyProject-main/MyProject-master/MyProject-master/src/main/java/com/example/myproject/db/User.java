package com.example.myproject.db;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    private String userId;
    private String userName;
    private String userPassword;
    private String userEmail;
    private String userDob;
    private String userLocation;
    private String userInfo;
    private String userRole;
}
