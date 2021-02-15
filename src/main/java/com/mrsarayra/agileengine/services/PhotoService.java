package com.mrsarayra.agileengine.services;

import com.mrsarayra.agileengine.dao.PhotoEntity;
import com.mrsarayra.agileengine.repositories.AbstractRepository;
import com.mrsarayra.agileengine.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.mrsarayra.agileengine.dao.PhotoEntity.ORIGINAL_ID_FIELD;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
public class PhotoService extends AbstractService<PhotoEntity> {

    @Autowired
    private PhotoRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public Class<PhotoEntity> getEntityClass() {
        return PhotoEntity.class;
    }


    @Override
    public AbstractRepository<PhotoEntity> getRepository() {
        return repository;
    }


    public List<PhotoEntity> findPhotoByTerm(String term) {
        Assert.hasText(term, "Term cannot be blank");
        String[] termDef = term.split(":");
        Assert.isTrue(termDef.length == 2, "Term must have value");
        Query query = new Query(where(termDef[0]).is(termDef[1]));
        return mongoTemplate.find(query, getEntityClass());
    }


    public PhotoEntity findByOriginalId(String originalId) {
        Assert.hasText(originalId, "Id cannot be blank");
        Query query = new Query(where(ORIGINAL_ID_FIELD).is(originalId));
        List<PhotoEntity> photoEntities = mongoTemplate.find(query, getEntityClass());
        return !photoEntities.isEmpty() ? photoEntities.get(0) : null;
    }

}
