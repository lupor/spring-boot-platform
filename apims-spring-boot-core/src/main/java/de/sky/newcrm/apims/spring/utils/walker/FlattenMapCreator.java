/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import java.util.Collection;
import org.springframework.util.StringUtils;

public class FlattenMapCreator<T> extends ObjectWalker<T> {

    private final FlattenMap result = new FlattenMap();

    public FlattenMapCreator<T> walk(T root) {
        walk(root, null);
        return this;
    }

    @Override
    protected boolean walkCollection(AcceptCallback<T> callback, T root, String path, Collection<?> value) {
        final String sizePath = (StringUtils.hasLength(path)) ? path + ".size" : "size";

        result.put(sizePath, value.size());
        return super.walkCollection(callback, root, path, value);
    }

    @Override
    protected boolean handleValue(AcceptCallback<T> callback, T root, String path, String key, Object value) {
        result.put(path, value);
        return true;
    }

    public FlattenMap getResult() {
        return result;
    }
}
