package com.visoft.file.service.persistance.repository;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.persistance.entity.Token;
import com.visoft.file.service.persistance.entity.User;

public interface Repositories {

    AbstractRepository<User> USER_REPOSITORY = new UserRepository();

    AbstractRepository<Token> TOKEN_REPOSITORY = new TokenRepository();

    AbstractRepository<Folder> FOLDER_REPOSITORY = new FolderRepository();
}
