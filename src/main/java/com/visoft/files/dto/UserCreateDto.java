package com.visoft.files.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserCreateDto {

    private String login;

    private String password;

    private List<String> folders;
}
