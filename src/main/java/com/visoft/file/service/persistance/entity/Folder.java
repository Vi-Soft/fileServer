package com.visoft.file.service.persistance.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@Data
public class Folder {

    private ObjectId id;

    private String folder;

    private String projectName;

    private String taskName;

    @BsonCreator
    public Folder(
            @BsonProperty(GeneralConst.ID) ObjectId id,
            @BsonProperty(FolderConst.FOLDER) String folder,
            @BsonProperty(FolderConst.PROJECT_NAME) String projectName,
            @BsonProperty(FolderConst.TASK_NAME) String taskName
    ) {
        this.id = id;
        this.folder = folder;
        this.projectName = projectName;
        this.taskName = taskName;
    }

    public Folder(
            String folder,
            String projectName,
            String taskName

    ) {
        this(
                ObjectId.get(),
                folder,
                projectName,
                taskName
        );
    }
}