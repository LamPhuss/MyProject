package com.example.myproject.Service;

import com.example.myproject.db.User;
import com.example.myproject.db.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service

public class UserService {
    @Autowired
    private UserMapper userMapper;

    public boolean addUser(User user) throws Exception {
        String userName = user.getUserName();
        String id = generateRandomString(40);
        String password = user.getUserPassword();
        String email = user.getUserEmail();
        Map<String, Object> reqMap = new HashMap<>();
        try {
            if (!StringUtils.hasText(userName)) {
                return false;
            } else {
                reqMap.put("userName", userName);
                reqMap.put("userId", id);
            }
            if (!StringUtils.hasText(password)){
                return false;
            } else {
                reqMap.put("userPassword", password);
            }
            if (!StringUtils.hasText(email)){
                return false;
            } else {
                reqMap.put("userEmail", email);
            }
            userMapper.insert(reqMap);
            return true;

        } catch (Exception e){
            throw e;
        }
    }
    public User findUserByEmail(String email) throws Exception{
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("userEmail",email);
        return userMapper.findUserByEmail(reqMap);
    }

    public User loginUser(User user) throws Exception{
        String password = user.getUserPassword();
        String email = user.getUserEmail();
        Map<String, Object> reqMap = new HashMap<>();
        try {
            reqMap.put("userEmail", email);
            reqMap.put("userPassword", password);
            User checkUser = userMapper.findUser(reqMap);
            return checkUser;

        } catch (Exception e){
            throw e;
        }
    }
    public User findUserById(String id) throws Exception{
        Map<String, Object> reqMap = new HashMap<>();
        try {
            reqMap.put("userId", id);
            User checkUser = userMapper.findUserById(reqMap);
            return checkUser;

        } catch (Exception e){
            throw e;
        }
    }
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
    public boolean updateUser(User user) throws Exception {
        String userName = user.getUserName();
        String password = user.getUserPassword();
        String email = user.getUserEmail();
        String dob = user.getUserDob();
        String location = user.getUserLocation();
        String info = user.getUserInfo();
        Map<String, Object> reqMap = new HashMap<>();

        try {
            if (!StringUtils.hasText(userName)) {
                return false;
            } else {
                reqMap.put("userName", userName);
            }
            if (!StringUtils.hasText(password)){
                return false;
            } else {
                reqMap.put("userPassword", password);
            }
            if (!StringUtils.hasText(email)){
                return false;
            } else {
                reqMap.put("userEmail", email);
            }
            if (!StringUtils.hasText(dob)){
                return false;
            } else {
                reqMap.put("userDob", dob);
            }
            if (!StringUtils.hasText(location)){
                return false;
            } else {
                reqMap.put("userLocation", location);
            }
            if (!StringUtils.hasText(info)){
                return false;
            } else {
                reqMap.put("userInfo", info);
            }
            reqMap.put("userId", user.getUserId());
            userMapper.updateUser(reqMap);
            return true;

        } catch (Exception e){
            throw e;
        }
    }
}
