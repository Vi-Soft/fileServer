package com.visoft.files.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static com.visoft.files.entity.GeneralConst.*;

public class AbstractRepository<T> implements Repository<T> {

    private final MongoCollection<T> mongoCollection;

    public AbstractRepository(MongoCollection<T> companyMongoCollection) {
        this.mongoCollection = companyMongoCollection;
    }

    @Override
    public void create(T t) {
        mongoCollection.insertOne(t);
    }

    @Override
    public List<T> findAll() {
        Bson bsonFilter = eq(DELETED, false);
        return getListObject(bsonFilter);
    }

    @Override
    public List<T> findAllDeleted() {
        Bson bsonFilter = eq(DELETED, true);
        return getListObject(bsonFilter);
    }

    @Override
    public T findById(ObjectId id) {
        Bson bsonFilter = eq(_ID, id);
        return getObject(bsonFilter);
    }

    @Override
    public List<T> getListObject(Bson bsonFilter) {
        return StreamSupport
                .stream(mongoCollection.find(bsonFilter).spliterator(), true)
                .collect(Collectors.toList());
    }

    @Override
    public T getObject(Bson bsonFilter) {
        return mongoCollection.find(bsonFilter).first();
    }

    @Override
    public boolean isExists(Bson filter) {
        return mongoCollection.count(filter) > 0;
    }

    @Override
    public boolean update(ObjectId id, String name, Object value) {
        Bson bisonFilter = eq(_ID, id);
        UpdateResult updateResult = mongoCollection.updateOne(bisonFilter,
                set(name, value));
        return updateResult.getModifiedCount()>0;
    }
}
