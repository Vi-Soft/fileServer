package com.visoft.file.service.service.abstractService;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public interface AbstractService<T> {

    void create(T t);

    List<T> findAll();

    List<T> findAllDeleted();

    T findById(ObjectId id);

    T findByIdNotDeleted(ObjectId id);

    T getObject(Bson bsonFilter);

    List<T> getListObject(Bson bsonFilter);

    boolean isExists(Bson filter);

    boolean update(ObjectId id, String name, Object value);

    long update(T t, ObjectId id);

    void delete(ObjectId id);
}