package com.mrsarayra.agileengine.repositories;

import com.mrsarayra.agileengine.dao.AbstractDbEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AbstractRepository<T extends AbstractDbEntity> extends MongoRepository<T, String> {

}
