package com.visoft.file.service.service.folder;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;

import java.util.List;

public interface FolderService {

    FolderOutcomeDto create(String folder, String projectName, String taskName);

    FolderOutcomeDto findById(String id);

    boolean folderExistsById(String id);

    void delete(String id);

    List<FolderOutcomeDto> findAll();
}
