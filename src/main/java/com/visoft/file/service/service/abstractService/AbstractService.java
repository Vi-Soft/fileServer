package com.visoft.file.service.service.abstractService;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Interface for entity actions
 *
 * @param <T> describes entity parameter
 */
public interface AbstractService<T> {

    /**
     * Create
     *
     * @param t entity
     */
    void create(T t);

    /**
     * Search all entity by type
     *
     * @return {@link List List of entity} or null
     */
    List<T> findAll();

    /**
     * Search all entity by type where deleted equals true
     *
     * @return {@link List List of entity} or null if not found
     */
    List<T> findAllDeleted();

    /**
     * Search entity by id
     *
     * @param id filed id
     * @return T or null if not found
     */
    T findById(ObjectId id);

    /**
     * Search entity by id where deleted equals true
     *
     * @param id filed id
     * @return T or null if not found
     */
    T findByIdNotDeleted(ObjectId id);

    /**
     * Search  entity by filter
     *
     * @param bsonFilter search filter
     * @return entity by filter or null if not found
     */
    T getObject(Bson bsonFilter);

    /**
     * Search  entity by filter
     *
     * @param bsonFilter search filter
     * @return {@link List List of entity} or null if not found
     */
    List<T> getListObject(Bson bsonFilter);

    /**
     * Search entity by filter
     *
     * @param filter search filter
     * @return true if exists else false
     */
    boolean isExists(Bson filter);

    /**
     * Update entity field
     *
     * @param id    id
     * @param name  field name
     * @param value field value
     * @return true if update success else false
     */
    boolean update(ObjectId id, String name, Object value);

    /**
     * Update full entity
     *
     * @param t  entity
     * @param id id
     * @return true if update success else false
     */
    long update(T t, ObjectId id);

    /**
     * Delete
     *
     * @param id filed id
     */
    void delete(ObjectId id);
}