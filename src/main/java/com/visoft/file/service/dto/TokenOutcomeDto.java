package com.visoft.file.service.dto;

import com.visoft.file.service.persistence.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenOutcomeDto {

    private String token;

    private Role role;
}