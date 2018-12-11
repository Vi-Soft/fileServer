package com.visoft.utils;

/**
 * @author vlad
 *
 */
public interface Const {

	String DOC_TEMPLATES_MONGO_COLLECTION = "docTemplates";
	String REPORTS_MONGO_COLLECTION = "reports";
	String DOC_FILES_MONGO_COLLECTION = "docFiles";
	String USERS_MONGO_COLLECTION = "users"; 
	String AUTH_DATA = "authData";
	String CONTACTS_COLLECTION = "contacts";
	
	String API = "/api";
	String UPLOAD = "/upload";
	String UPLOAD_TEMPLATE = "/upload-template";
	String UPDATE_TEMPLATE = "/update-template";
	String DOWNLOAD = "/download/{fileId}";
	String DOWNLOAD_TEMPLATE = "/download-template/{templateId}";
	String DOWNLOAD_REPORT = "/download-report/{reportId}";
	String DOWNLOAD_PDF = "/download-pdf";
	String RESOURCES_DIR = "/src/main/resources/";
	String USER_DIR = "user.dir";
	String DOC_FILE_OUTPUT_DIR = "docFileOut";
	String PDF_TEMPLATE_OUTPUT_DIR = "pdfTemplates";
	String REPORT_OUTPUT_DIR = "reportOut";
	String PROJECT_ID = "projectId";
	String TASK_ID = "taskId";
	String Query = "Query";
	String Mutation = "Mutation";

	String _ID = "_id";
	String ID = "id";
	
	String CREATED_AT = "createdAt";
	String POSTED_BY = "postedBy";
	String FILE_LENGTH_IN_BYTES = "fileLengthInBytes";
	String ALL_DOC_FILES = "allDocFiles";
	String ALL_DOC_TEMPLATES = "allDocTemplates";
	String ALL_REPORTS	 = "allReports";
	String SAVE_DOC_FILE = "saveDocFile";
	String SAVE_DOC_TEMPLATE = "saveDocTemplate";
	String DELETE_DOC_FILE = "deleteDocFile";
	
	String FIND_USER_BY_ID = "findUserById";
	String FIND_USER_BY_EMAIL = "findUserByEmail";
	String FIND_BY_DOC_FILE_ID = "findByReportId";
	String FIND_BY_DOC_TEMPLATE_ID = "findByDocTemplateId";
	String FIND_BY_REPORT_ID = "findByDocFileId";
	
	String RECOVERY_DOC_FILE = "recoveryDocFile";
	String DELETE_DOC_TEMPLATE = "deleteDocTemplate";
	String RECOVERY_DOC_TEMPLATE = "recoveryDocTemplate";
	
	
	String TEMPLATE_TYPE = "templateType";
	String REPORT_TYPE = "reportType";
	String DOC_FILE_ID = "docFileId";
	String REPORT_ID = "reportId";
	String DOC_TEMPLATE_ID = "docTemplateId";
	String BUSINESS_TYPE = "businessType";
	
	String LINK_QUERY = "link";
	String ALL_LINKS = "allLinks";
	String FIND_LINK_BY_ID = "findLinkById";
	String CREATE_LINK = "createLink";
	
	String USER_QUERY = "user";
	String SIGNIN_USER = "signinUser";
	String CREATE_USER = "createUser";
	String CREATE_AUTH_DATA = "createAuthData";
	String AUTH = "auth";
	String AUTH_PROVIDER = "authProvider";
	
	String FIRST_NAME = "firstName";
	String MIDDLE_NAME = "middleName";
	String LAST_NAME = "lastName";
	String EMAIL = "email";
	String AUTH_EMAIL = "authEmail";
	String EMAIL_ADDRESS = "emailAddress";
	String PASSWD = "passwd";
	String URL = "url";
	String DESCRIPTION = "description";
	String ALL_USERS = "allUsers";
	String ALL_AUTH_DATA = "allAuthData";
	
	String CREATE_VOTE = "createVote";
	
	String DESCRIPTION_CONTAINS = "description_contains";
	String URL_CONTAINS = "url_contains";
	
	String FILTER = "filter";
	String SKIP = "skip";
	String LIMIT = "limit";
	
	String FILE_CONTENT_HASH = "fileContentHash";
	String FILE_NAME = "fileName";
	String DELETED = "deleted";
	
	
}
