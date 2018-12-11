package com.visoft.files.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import static com.visoft.files.entity.FolderConst.*;

@Data
@BsonDiscriminator
public class Folder {

    private ObjectId id;

    private String folder;

    private Boolean deleted;

    @BsonCreator
    public Folder(
            @BsonProperty(ID) ObjectId id,
            @BsonProperty(DELETED) Boolean deleted,
            @BsonProperty(FOLDER) String folder
    ) {
        this.id = id;
        this.deleted = deleted;
        this.folder = folder;
    }

    public Folder(@BsonProperty(FOLDER) String folder) {
        this(
                ObjectId.get(),
                false,
                folder
                );
    }
}
