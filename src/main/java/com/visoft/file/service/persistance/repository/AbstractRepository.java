package com.visoft.file.service.persistance.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import static com.visoft.file.service.persistance.entity.GeneralConst.DELETED;
import static com.visoft.file.service.persistance.entity.GeneralConst._ID;

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
    public T findByIdNotDeleted(ObjectId id) {
        Bson bsonFilter = and(eq(_ID, id), eq(DELETED, false));
        return getObject(bsonFilter);
    }

    @Override
    public List<T> getListObject(Bson bsonFilter) {
        return StreamSupport
                .stream(mongoCollection.find().spliterator(), true)
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
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public long update(final T t, ObjectId id) {
        final Bson bsonFilter = eq(_ID, id);
        UpdateResult updateResult =
                mongoCollection.replaceOne(bsonFilter, t);
        return updateResult.getModifiedCount();
    }

}
