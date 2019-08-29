package com.visoft.file.service.service;

import com.visoft.file.service.dto.FormType;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormTypeService {

    public FormType getFormType(Map<String,FormType> formTypes, String path) {
        return formTypes.get(path);
    }
}
