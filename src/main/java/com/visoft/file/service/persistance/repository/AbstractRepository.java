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

/**
 * Abstract class for entity actions
 *
 * @param <T> describes entity parameter
 */
public class AbstractRepository<T> implements Repository<T> {

    private final MongoCollection<T> mongoCollection;

    AbstractRepository(MongoCollection<T> companyMongoCollection) {
        this.mongoCollection = companyMongoCollection;
    }

    @Override
    public void create(T t) {
        mongoCollection.insertOne(t);
    }

    @Override
    public void delete(ObjectId id) {
        mongoCollection.deleteOne(eq(_ID, id));
    }

    @Override
    public List<T> findAll() {
        return StreamSupport
                .stream(
                        mongoCollection
                                .find()
                                .spliterator(),
                        true)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findAllDeleted() {
        return getListObject(eq(DELETED, true));
    }

    @Override
    public T findById(ObjectId id) {
        return getObject(eq(_ID, id));
    }

    @Override
    public T findByIdNotDeleted(ObjectId id) {
        Bson bsonFilter = and(
                eq(_ID, id),
                eq(DELETED, false)
        );
        return getObject(bsonFilter);
    }

    @Override
    public List<T> getListObject(Bson bsonFilter) {
        return StreamSupport
                .stream(mongoCollection
                                .find(bsonFilter)
                                .spliterator(),
                        true
                )
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
        UpdateResult updateResult = mongoCollection.updateOne(
                eq(_ID, id),
                set(name, value)
        );
        return updateResult.getModifiedCount() > 0;
    }

    @Override
    public long update(final T t, ObjectId id) {
        UpdateResult updateResult =
                mongoCollection.replaceOne(
                        eq(_ID, id),
                        t
                );
        return updateResult.getModifiedCount();
    }
}