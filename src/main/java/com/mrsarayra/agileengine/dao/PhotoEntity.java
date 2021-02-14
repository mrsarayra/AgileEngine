package com.mrsarayra.agileengine.dao;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Document("photos")
public class PhotoEntity extends AbstractDbEntity {


    private String originalId;
    private String author;
    private String camera;
    private String croppedPicture;
    private String fullPicture;

    private Set<String> tags;


}
