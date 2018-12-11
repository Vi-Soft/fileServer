package com.visoft.files.service.abstractService;

import com.visoft.files.repository.AbstractRepository;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;

public abstract class AbstractServiceImpl<T> implements AbstractService<T> {

    private AbstractRepository<T> repository;

    public AbstractServiceImpl(AbstractRepository<T> repository) {
        this.repository = repository;
    }


    @Override
    public void create(T t) {
        repository.create(t);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAllDeleted() {
        return repository.findAllDeleted();
    }

    @Override
    public T findById(ObjectId id) {
        return repository.findById(id);
    }

    @Override
    public T getObject(Bson bsonFilter) {
        return repository.getObject(bsonFilter);
    }

    @Override
    public boolean isExists(Bson filter) {
        return repository.isExists(filter);
    }

    @Override
    public boolean update(ObjectId id, String name, Object value) {
        return repository.update(id, name, value);
    }
}
