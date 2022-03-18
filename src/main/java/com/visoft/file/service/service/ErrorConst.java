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

    String RETURN = "Return";

    String SUCCESS = "Success";

    String DELETE = "Delete";

    String INVALID_TOKEN = "Invalid token";

    String ARCHIVE_NAME_IS_EMPTY = "Archive name is empty";

    String REPORT_DTO_IS_EMPTY = "Report dto is empty";

    String NOT_CORRECT_REPORT_DTO = "Not correct report dto";

    String NOT_CORRECT_ZIP = "Not correct zip";

    String NOT_FORM_TYPE_PATH = "Not formType path";

    String SEND_ERROR_EMAIL = "Send error email";

    String MAX_DOWNLOADS_PER_PROJECT = "Max downloads per project already running";

    String FOLDER_NOT_FOUND = "Folder not found";

    String DTO_IS_EMPTY = "Dto is empty";

    String USER_UPDATE_DTO_ID_IS_EMPTY = "User updateDto id is empty";

    String USER_LOGIN_IS_EMPTY = "User login is empty";

    String USER_PASSWORD_IS_EMPTY = "User password is empty";

    String FOLDER_DTO_IS_EMPTY = "Folder dto is empty";

    String FOLDER_DTO_DUPLICATE = "Folder dto has duplicates";

    String FOLDER_DOES_NOT_EXIST = "Folder does not exist";

    String USER_DOES_NOT_EXIST = "User does not exist";

    String LOGIN_NOT_VALID = "Login not valid";

    String TOKEN_NOT_FOUND = "Token not found";

    String ADMIN_ALREADY_EXISTS = "Admin already exists";

    String ATTACHMENT_DOCUMENT_PATH_NOT_FOUND = "Attachment document path not found";

    int UNAUTHORIZED = 401;

    int FORBIDDEN = 403;

    int BAD_REQUEST = 400;

    int CREATE = 201;

    int OK = 200;

    int NOT_FOUND = 404;
}
