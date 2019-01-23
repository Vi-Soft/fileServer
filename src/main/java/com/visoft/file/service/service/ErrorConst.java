package com.visoft.file.service.service;

public interface ErrorConst {

    String LOGIN_NOT_CORRECT = "Login must be not null and not empty";

    String PASSWORD_NOT_CORRECT = "Password must be not null and not empty";

    String LOGIN_EXISTS = "Login exists";

    String PROJECT_NAME_NOT_CORRECT = "Project name must be not null and not empty";

    String COMPANY_NAME_NOT_CORRECT = "Company name must be not null and not empty";

    String ARCHIVE_NAME_NOT_CORRECT = "Archive name must be not null and not empty";

    String TASK_NAME_NOT_CORRECT = "Task name must be not null and not empty";

    String TASK_ID_NOT_CORRECT = "Task id must be not null and > 0";

    String MORE_ONE_EQUALS_TASK_ID = "More equals task id";

    String PARENT_TASK_ID_NOT_CORRECT = "Parent task id must be not null and not equals 0 and < -1";

    String MORE_ONE_EQUALS_PARENT_TASK_ID = "More one equals parent task id";

    String ORDER_IN_GROUP_NOT_CORRECT = "Order in group be not null";

    String ICON_NOT_CORRECT = "Icon must be not null and > -1 and < 3";

    String ALREADY_UNZIP = "Already unzip";

    String RETURN = "return: ";

    String SUCCESS = "success";

    int UNAUTHORIZED = 401;

    int FORBIDDEN = 403;

    int BAD_REQUEST = 400;

    int CREATE = 201;

    int OK = 200;

    int NOT_FOUND = 404;
}
