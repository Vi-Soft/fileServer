package com.visoft.file.service.persistence.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public abstract class IdEntity {

    @Id
    private String id;
}