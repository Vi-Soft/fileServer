package com.visoft.file.service.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@CompoundIndexes({
        @CompoundIndex(name = "index", def = "{'token' : 1, 'userId': 1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token extends IdEntity {

    protected Instant expiration;

    @Indexed(unique = true)
    private String token;

    @Indexed(unique = true)
    private String userId;
}