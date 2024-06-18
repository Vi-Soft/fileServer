package com.visoft.file.service.persistance.entity;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.Instant;

@Data
public class Folder {

    private ObjectId id;

    private String folder;

    private String mutualFolder;

    private Instant date;

    private String projectName;

    private String taskName;

    private Boolean winFolder;

    @BsonCreator
    public Folder(
            @BsonProperty(GeneralConst.ID) ObjectId id,
            @BsonProperty(FolderConst.FOLDER) String folder,
            @BsonProperty(FolderConst.MUTUAL_FOLDER) String mutualFolder,
            @BsonProperty(FolderConst.PROJECT_NAME) String projectName,
            @BsonProperty(FolderConst.TASK_NAME) String taskName,
            @BsonProperty(FolderConst.DATE) Instant date,
            @BsonProperty(FolderConst.WIN_FOLDER) Boolean winFolder
    ) {
        this.id = id;
        this.folder = folder;
        this.mutualFolder = mutualFolder;
        this.projectName = projectName;
        this.taskName = taskName;
        this.date = date;
        this.winFolder = winFolder;
    }

    public Folder(
            String folder,
            String mutualFolder,
            String projectName,
            String taskName,
            Instant date,
            boolean winFolder
    ) {
        this(
            ObjectId.get(),
            folder,
            mutualFolder,
            projectName,
            taskName,
            date,
            winFolder
        );
    }
}