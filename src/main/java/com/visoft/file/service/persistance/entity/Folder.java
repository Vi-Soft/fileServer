package com.visoft.file.service.persistance.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class Folder {

    private ObjectId id;

    private String folder;

    @BsonCreator
    public Folder(
            @BsonProperty(GeneralConst.ID) ObjectId id,
            @BsonProperty(FolderConst.FOLDER) String folder
    ) {
        this.id = id;
        this.folder = folder;
    }

    public Folder(@BsonProperty(FolderConst.FOLDER) String folder) {
        this(
                ObjectId.get(),
                folder
                );
    }
}