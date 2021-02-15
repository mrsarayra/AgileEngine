package com.mrsarayra.agileengine.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("photos")
@CompoundIndex(def = "{'originalId' : 1}", unique = true)
public class PhotoEntity extends AbstractDbEntity {

    public static final String ORIGINAL_ID_FIELD = "originalId";

    private String originalId;
    private String author;
    private String camera;
    private String croppedPicture;
    private String fullPicture;
    private Set<String> tags;


}
