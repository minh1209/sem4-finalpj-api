package com.example.finalpj.dto;

import com.example.finalpj.entity.Role;

import java.sql.Date;
import java.util.List;

public interface UserDto {
    String getId();
    String getUsername();
    String getEmail();
    Date getDob();
    String getPhone();
    String getIdentification();
    String getAvatar();
}
