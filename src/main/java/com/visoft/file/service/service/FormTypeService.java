package com.visoft.file.service.service;

import com.visoft.file.service.dto.FormType;

import java.nio.file.Paths;
import java.util.List;

public class FormTypeService {

    public FormType getFormType(List<FormType> formTypes, String path) {
        if (path==null)return null;
        for (FormType formType : formTypes) {
            if (Paths.get(path).toString().equals(Paths.get(formType.getPath()).toString())) {
                return formType;
            }
        }
        return null;
    }
}
