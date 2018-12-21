package com.visoft.file.service.service.DI;

import com.visoft.file.service.service.folder.FolderService;
import com.visoft.file.service.service.folder.FolderServiceImpl;
import com.visoft.file.service.service.token.TokenService;
import com.visoft.file.service.service.token.TokenServiceImpl;
import com.visoft.file.service.service.user.UserService;
import com.visoft.file.service.service.user.UserServiceImpl;

public interface DependencyInjectionService {

    UserService USER_SERVICE = new UserServiceImpl();

    TokenService TOKEN_SERVICE = new TokenServiceImpl();

    FolderService FOLDER_SERVICE = new FolderServiceImpl();
}