package com.mrsarayra.agileengine.classes;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
public final class ResponseImages {

    private List<Picture> pictures;

    private boolean hasMore;

}
