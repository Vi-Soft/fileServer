package com.visoft.file.service.service;

import com.visoft.file.service.dto.AttachmentDocument;
import com.visoft.file.service.dto.FormType;

import java.util.Map;

public class AttachmentDocumentService {

    public AttachmentDocument getAttachmentDocument(Map<String, AttachmentDocument> attachmentDocumentMap, String path) {
        return attachmentDocumentMap.get(path);
    }
}
