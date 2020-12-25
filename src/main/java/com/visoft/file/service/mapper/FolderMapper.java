package com.visoft.file.service.mapper;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;
import com.visoft.file.service.persistence.entity.Folder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface FolderMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "folder", source = "folder")
    FolderOutcomeDto toDto(Folder folder);
}
