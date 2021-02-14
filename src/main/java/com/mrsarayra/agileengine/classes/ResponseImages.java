package com.mrsarayra.agileengine.classes;

import lombok.Data;

import java.util.List;

@Data
public class ResponseImages {

    private List<Picture> pictures;

    private boolean hasMore;

}
