package com.visoft.file.service.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

import static com.visoft.file.service.entity.TokenConst.*;

@Data
public class Token {

    private ObjectId id;

    private Instant expiration;

    private String token;

    private ObjectId userId;

    @BsonCreator
    public Token(
            @BsonProperty(ID) ObjectId id,
            @BsonProperty(EXPIRATION) Instant expiration,
            @BsonProperty(TOKEN) String token,
            @BsonProperty(USER_ID) ObjectId userId
    ) {
        this.id = id;
        this.expiration = expiration;
        this.token = token;
        this.userId = userId;
    }

    public Token(
            @BsonProperty(TOKEN) String token,
            @BsonProperty(value = USER_ID) ObjectId userId
    ) {
        this(ObjectId.get(),
                Instant.now().plusSeconds(10800L),
                token, userId
        );
    }
}
