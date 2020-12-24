package com.visoft.file.service.web;

import com.visoft.file.service.dto.LoginDto;
import com.visoft.file.service.service.authorization.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
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