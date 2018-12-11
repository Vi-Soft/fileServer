package com.visoft.files.service.DI;

import com.visoft.files.service.*;

public interface DependencyInjectionService {

    UserService USER_SERVICE = new UserServiceImpl();

    TokenService TOKEN_SERVICE = new TokenServiceImpl();

    FolderService FOLDER_SERVICE = new FolderServiceImpl();
}
