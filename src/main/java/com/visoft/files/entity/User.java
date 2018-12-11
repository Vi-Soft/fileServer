package com.visoft.files.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.List;

import static com.visoft.files.entity.UserConst.*;

@Data
@BsonDiscriminator
public class User {

    private ObjectId id;

    private Boolean deleted;

    private String login;

    private String password;

    private Role role;

    private List<ObjectId> folders;

    @BsonCreator
    public User(
            @BsonProperty(ID) ObjectId id,
            @BsonProperty(DELETED) Boolean deleted,
            @BsonProperty(LOGIN) String login,
            @BsonProperty(PASSWORD) String password,
            @BsonProperty(ROLE) Role role,
            @BsonProperty(FOLDERS) List<ObjectId> folders
    ) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.deleted = deleted;
        this.folders = folders;
    }

    public User(
            @BsonProperty(LOGIN) String login,
            @BsonProperty(PASSWORD) String password,
            @BsonProperty(ROLE) Role role,
            @BsonProperty(FOLDERS) List<ObjectId> folders
    ) {
        this(
                ObjectId.get(),
                false,
                login,
                password,
                role,
                folders
        );
    }
}
