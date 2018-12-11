package com.visoft.files.repository;

import com.visoft.files.entity.Folder;
import com.visoft.files.entity.Token;
import com.visoft.files.entity.User;

public interface Repositories {

    AbstractRepository<User> USER_REPOSITORY = new UserRepository();

    AbstractRepository<Token> TOKEN_REPOSITORY = new TokenRepository();

    AbstractRepository<Folder> FOLDER_REPOSITORY = new FolderRepository();
}
