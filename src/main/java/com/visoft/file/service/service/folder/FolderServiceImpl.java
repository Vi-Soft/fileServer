package com.visoft.file.service.service.folder;

import com.visoft.file.service.dto.folder.FolderOutcomeDto;
import com.visoft.file.service.mapper.FolderMapper;
import com.visoft.file.service.persistence.repository.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;

    private final FolderMapper folderMapper;

    @Override
    public FolderOutcomeDto create(String folder, String projectName, String taskName) {
        return null;
    }

    @Override
    public FolderOutcomeDto findById(String id) {
        return null;
    }

    @Override
    public boolean folderExistsById(String id) {
        return false;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public List<FolderOutcomeDto> findAll() {
        return null;
    }
}
