package com.visoft.file.service.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserUpdateDto {

    private String id;

    private String login;

    private String password;

    private List<String> folders;
}