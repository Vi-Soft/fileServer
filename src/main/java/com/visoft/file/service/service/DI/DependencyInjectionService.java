package com.visoft.file.service.service.DI;

import com.visoft.file.service.service.*;

public interface DependencyInjectionService {

    UserService USER_SERVICE = new UserServiceImpl();

    TokenService TOKEN_SERVICE = new TokenServiceImpl();

    FolderService FOLDER_SERVICE = new FolderServiceImpl();
}
