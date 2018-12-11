package com.visoft.files.repository;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public interface Repository<T> {

    void create(T t);

    List<T> findAll();

    List<T> findAllDeleted();

    T findById(ObjectId id);

    List<T> getListObject(Bson bsonFilter);

    T getObject(Bson bsonFilter);

    boolean isExists(Bson filter);

    boolean update(ObjectId id, String name, Object value);
}
