package com.mrsarayra.agileengine.services;

import com.mrsarayra.agileengine.dao.AbstractDbEntity;
import com.mrsarayra.agileengine.repositories.AbstractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
public abstract class AbstractService<T extends AbstractDbEntity> {

    public abstract Class<T> getEntityClass();

    public abstract AbstractRepository<T> getRepository();


    public void beforeCreate(T entity) {}


    public void beforeUpdate(T entity) {}


    public T create(T entity) {
        Assert.notNull(entity, "Entity cannot be null");
        log.info("{} Creating new Entity...", this.getClass().getSimpleName());
        entity.validate(true);
        beforeCreate(entity);
        return getRepository().save(entity);
    }


    public T update(T entity) {
        Assert.notNull(entity, "Entity cannot be null");
        log.info("{} Updating Entity...", this.getClass().getSimpleName());
        entity.validate(false);
        T dbEntity = findById(entity.getId());
        Assert.notNull(dbEntity, "Cannot update non-existing entity");
        beforeUpdate(entity);
        return getRepository().save(entity);
    }


    public T findById(String id) {
        Assert.hasText(id, "Id cannot be blank");
        log.info("{} Searching for Entity with Id {}", this.getClass().getSimpleName(), id);
        return getRepository().findById(id).orElse(null);
    }


    public List<T> findAll() {
        log.info("{} Searching for all Entities...", this.getClass().getSimpleName());
        return getRepository().findAll();
    }


    public Page<T> findAll(Pageable pageable) {
        Assert.notNull(pageable, "Pageable cannot be null");
        log.info("{} Searching for all Entities...", this.getClass().getSimpleName());
        return getRepository().findAll(pageable);
    }

//
//    public T getReference(String id) {
//        Assert.notNull(id, "Id cannot be blank");
//        return getRepository().getOne(id);
//    }


    public boolean exists(String id) {
        Assert.hasText(id, "Entity Id cannot be blank");
        log.info("{} Check entity with Id {} existence", this.getClass().getSimpleName(), id);
        return getRepository().existsById(id);
    }


    public long count() {
        return getRepository().count();
    }


    public void deleteById(String id) {
        Assert.notNull(id, "Id cannot be null");
        log.info("{} Deleting Entity with Id: {}", this.getClass().getSimpleName(), id);
        getRepository().deleteById(id);
    }


    public void delete(T entity) {
        Assert.notNull(entity, "Entity cannot be null");
        entity.validate(false);
        log.info("{} Deleting Entity with Id: {}", this.getClass().getSimpleName(), entity.getId());
        getRepository().delete(entity);
    }

}
