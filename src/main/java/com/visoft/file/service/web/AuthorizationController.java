package com.visoft.file.service.web;

import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.service.authorization.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AuthorizationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginDto dto) {
        return authenticationService.login(dto);
    }

    @PreAuthorize("authentication.principal!=null")
    @GetMapping("/logout")
    public Long logout() {
        return authenticationService.logout();
    }
}