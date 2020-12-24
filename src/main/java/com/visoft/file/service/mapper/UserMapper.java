package com.visoft.file.service.mapper;

import com.visoft.file.service.dto.user.UserCreateDto;
import com.visoft.file.service.dto.user.UserOutcomeDto;
import com.visoft.file.service.dto.user.UserUpdateDto;
import com.visoft.file.service.persistence.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "folders", source = "folders")
    UserOutcomeDto toDto(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "folders", source = "folders")
    User toUser(UserCreateDto userCreateDto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "authorities", source = "authorities")
    @Mapping(target = "deleted", source = "deleted")
    @Mapping(target = "folders", source = "folders")
    User toUser(UserUpdateDto userUpdateDto);
}
