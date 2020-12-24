package com.visoft.file.service.persistence.repository;

import com.visoft.file.service.persistence.entity.Folder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FolderRepository extends MongoRepository<Folder, String> {
}
