package com.example.myproject.db.mapper;
import com.example.myproject.db.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserMapper {
    void insert (Map<String,Object> reqMap) throws Exception;
    User findUser (Map<String,Object> reqMap) throws Exception;
    void updateUser(Map<String,Object> reqMap) throws Exception;
    User findUserById (Map<String,Object> reqMap) throws Exception;
    User findUserByEmail (Map<String,Object> reqMap) throws Exception;
}
