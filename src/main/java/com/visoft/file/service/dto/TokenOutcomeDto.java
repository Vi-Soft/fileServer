package com.visoft.file.service.dto;

import com.visoft.file.service.persistance.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenOutcomeDto {

    private String token;

    private Role role;
}
