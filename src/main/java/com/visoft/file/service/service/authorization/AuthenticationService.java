package com.visoft.file.service.service.authorization;


import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.persistence.entity.user.User;

public interface AuthenticationService {

    String login(LoginDto loginDto);

    Long logout();

    User getActorFromContext();
}
