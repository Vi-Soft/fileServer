package com.visoft.files.dto;

import com.visoft.files.entity.Role;
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
