package com.visoft.file.service.service;

import com.visoft.file.service.dto.FormType;

import java.util.Map;

public class FormTypeService {

    public static FormType getFormType(Map<String,FormType> formTypes, String path) {
        return formTypes.get(path);
    }
}
