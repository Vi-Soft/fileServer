package com.visoft.file.service.repository;

import com.visoft.file.service.entity.Token;
import com.visoft.file.service.entity.User;
import com.visoft.file.service.entity.Folder;

public interface Repositories {

    AbstractRepository<User> USER_REPOSITORY = new UserRepository();

    AbstractRepository<Token> TOKEN_REPOSITORY = new TokenRepository();

    AbstractRepository<Folder> FOLDER_REPOSITORY = new FolderRepository();
}
