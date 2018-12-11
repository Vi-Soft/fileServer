package com.visoft.files.service.util;

import com.google.gson.Gson;

public class JsonService {

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
