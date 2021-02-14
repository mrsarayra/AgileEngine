package com.mrsarayra.agileengine.dao;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.domain.Persistable;
import org.springframework.util.Assert;

import javax.persistence.Id;
import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
public class AbstractDbEntity implements Serializable, Persistable<String> {

    public static final String CODE_FIELD_ID = "id";
    public static final String FIELD_ID = "_id";
    @Id
    private String id;


    public void validate(boolean isNew) {
        if (isNew) {
            Assert.isNull(id, "Id must be null for new entity");
        } else {
            Assert.hasText(id, "Id cannot be blank for updating entity");
        }
    }


    @JsonIgnore
    @Override
    public boolean isNew() {
        return isBlank(id);
    }

}
