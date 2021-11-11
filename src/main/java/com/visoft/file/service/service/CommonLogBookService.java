package com.visoft.file.service.service;

import com.visoft.file.service.dto.CommonLogBook;

import java.util.Map;

public class CommonLogBookService {
    public CommonLogBook getCommonLogBook(Map<String, CommonLogBook> logBooks, String path) {
        return logBooks.get(path);
    }

}
