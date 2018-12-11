package com.visoft.files.service;

import com.networknt.config.Config;
import com.visoft.files.entity.Folder;
import com.visoft.files.service.abstractService.AbstractServiceImpl;
import io.undertow.server.HttpServerExchange;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;
import static com.visoft.files.entity.FolderConst.FOLDER;
import static com.visoft.files.entity.GeneralConst._ID;
import static com.visoft.files.repository.Repositories.FOLDER_REPOSITORY;

public class FolderServiceImpl extends AbstractServiceImpl<Folder> implements FolderService {

    public FolderServiceImpl() {
        super(FOLDER_REPOSITORY);
    }

    @Override
    public boolean existsFolder(ObjectId folder) {
        Bson filter = eq(_ID, folder);
        return isExists(filter);
    }

    private static String getRequestBody(HttpServerExchange exchange) {
        exchange.startBlocking();
        InputStream is = exchange.getInputStream();
        String s = (new Scanner(is, "UTF-8")).useDelimiter("\\A").next();
        try {
            return Config.getInstance().getMapper().readValue(s, String.class);
        } catch (IOException e) {
            return null;
        }
    }
}
