package com.visoft.file.service.service;

import com.visoft.file.service.dto.FormType;

import java.util.List;

public class FormTypeService {

    public FormType getFormType(List<FormType> formTypes, String path) {
        for (FormType formType : formTypes) {
            if (formType.getPath().equals(path)) {
                return formType;
            }
        }
        return null;
    }
}
