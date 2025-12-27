/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import lombok.Builder;
import lombok.Getter;

public class SingleObjectFinder<T> extends ObjectWalker<T> {

    private final Predicate<SingleObjectFinderResult> predicate;
    private final Object defaultValue;
    private SingleObjectFinderResult result = null;

    public SingleObjectFinder(Predicate<SingleObjectFinderResult> predicate) {
        this(null, predicate);
    }

    public SingleObjectFinder(Object defaultValue, Predicate<SingleObjectFinderResult> predicate) {
        this.defaultValue = defaultValue;
        this.predicate = predicate;
    }

    @Override
    protected boolean handleValue(AcceptCallback<T> callback, T root, String parentKey, String key, Object value) {
        return true;
    }

    public SingleObjectFinder<T> walk(T root) {
        walk(root, new SingleObjectFinderAcceptCallback());
        return this;
    }

    public SingleObjectFinderResult getResult() {
        return result == null
                ? SingleObjectFinderResult.builder().value(defaultValue).build()
                : result;
    }

    @Builder
    @Getter
    public static class SingleObjectFinderResult {
        private String path;
        private String key;
        private Object value;
    }

    private class SingleObjectFinderAcceptCallback implements AcceptCallback<T> {

        @Override
        public boolean acceptMapItem(T root, String path, String key, Map<?, ?> value) {
            return evaluate(path, key, value);
        }

        @Override
        public boolean acceptCollectionItem(T root, String path, String key, Collection<?> value) {
            return evaluate(path, key, value);
        }

        @Override
        public boolean acceptValue(T root, String path, String key, Object value) {
            return evaluate(path, key, value);
        }

        protected boolean evaluate(String path, String key, Object value) {
            if (result == null) {
                SingleObjectFinderResult object = SingleObjectFinderResult.builder()
                        .path(path)
                        .key(key)
                        .value(value)
                        .build();
                if (predicate.test(object)) {
                    result = object;
                }
            }
            return result == null;
        }
    }
}
