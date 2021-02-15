package com.mrsarayra.agileengine.services;

import com.mrsarayra.agileengine.dao.AbstractDbEntity;
import com.mrsarayra.agileengine.repositories.AbstractRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public abstract class AbstractService<T extends AbstractDbEntity> {

    public abstract Class<T> getEntityClass();

    public abstract AbstractRepository<T> getRepository();


    public T create(T entity) {
        Assert.notNull(entity, "Entity cannot be null");
        log.info("{} Creating new Entity...", this.getClass().getSimpleName());
        entity.validate(true);
        return getRepository().save(entity);
    }


    public T update(T entity) {
        Assert.notNull(entity, "Entity cannot be null");
        log.info("{} Updating Entity...", this.getClass().getSimpleName());
        entity.validate(false);
        T dbEntity = findById(entity.getId());
        Assert.notNull(dbEntity, "Cannot update non-existing entity");
        return getRepository().save(entity);
    }


    public T findById(String id) {
        Assert.hasText(id, "Id cannot be blank");
        log.info("{} Searching for Entity with Id {}", this.getClass().getSimpleName(), id);
        return getRepository().findById(id).orElse(null);
    }

}
