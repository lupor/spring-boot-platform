/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.util.Collection;
import java.util.Map;

import org.springframework.data.core.TypeInformation;
import org.springframework.util.StringUtils;

public abstract class ObjectWalker<T> {

    protected ObjectWalker() {}

    public void walk(T root, AcceptCallback<T> callback) {
        if (callback == null) {
            callback = new AcceptCallback<>() {};
        }
        if (callback.acceptRootItem(root)) {
            walk(callback, root, "", "", root);
        }
    }

    protected boolean isMap(Object value) {
        final TypeInformation<?> type = getValueType(value);
        return type != null && type.isMap();
    }

    protected boolean isCollectionLike(Object value) {
        final TypeInformation<?> type = getValueType(value);
        return type != null && type.isCollectionLike();
    }

    @SuppressWarnings("java:S1452")
    protected TypeInformation<?> getValueType(final Object value) {
        return FunctionUtils.executeIfNotNull(value, null, () -> TypeInformation.of(value.getClass()));
    }

    protected boolean walk(AcceptCallback<T> callback, T root, String path, String key, Object value) {
        if (isMap(value)) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (!callback.acceptMapItem(root, path, key, map)) {
                return false;
            }
            if (!walkMap(callback, root, path, (Map<?, ?>) value)) {
                return false;
            }
        } else if (isCollectionLike(value)) {
            Collection<?> collection = ObjectUtils.asCollection(value);
            if (!callback.acceptCollectionItem(root, path, key, collection)) {
                return false;
            }
            if (!walkCollection(callback, root, path, collection)) {
                return false;
            }
        } else {
            if (!callback.acceptValue(root, path, key, value)) {
                return false;
            }
            handleValue(callback, root, path, key, value);
        }
        return true;
    }

    protected boolean walkMap(AcceptCallback<T> callback, T root, String path, Map<?, ?> value) {
        if (StringUtils.hasLength(path)) {
            path += ".";
        } else {
            path = "";
        }
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            String itemKey = String.valueOf(entry.getKey());
            String itemPath = path + itemKey;
            if (!walk(callback, root, itemPath, itemKey, entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    protected boolean walkCollection(AcceptCallback<T> callback, T root, String path, Collection<?> value) {
        int index = 0;
        for (Object collectionValue : value) {
            String itemKey = "[" + index + "]";
            String itemPath = path + itemKey;
            if (!walk(callback, root, itemPath, itemKey, collectionValue)) {
                return false;
            }
            index++;
        }
        return true;
    }

    protected abstract boolean handleValue(
            AcceptCallback<T> callback, T root, String parentKey, String key, Object value);

    public interface AcceptCallback<T> {

        default boolean acceptRootItem(T root) {
            return root != null;
        }

        default boolean acceptMapItem(T root, String path, String key, Map<?, ?> value) {
            return true;
        }

        default boolean acceptCollectionItem(T root, String path, String key, Collection<?> value) {
            return true;
        }

        default boolean acceptValue(T root, String path, String key, Object value) {
            return true;
        }
    }
}
