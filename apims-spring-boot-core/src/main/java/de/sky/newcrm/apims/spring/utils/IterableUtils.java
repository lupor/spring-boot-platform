/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("java:S1610")
public abstract class IterableUtils {

    private IterableUtils() {}

    @SuppressWarnings({"unchecked", "java:S1168"})
    public static <T> List<T> toList(Iterable<? extends T> iterable) {
        return iterable instanceof List<?> ? (List<T>) iterable : createArrayList(iterable);
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> toCollection(Iterable<? extends T> iterable) {
        return iterable instanceof Collection ? (Collection<T>) iterable : createArrayList(iterable);
    }

    static <T> List<T> createArrayList(Iterable<? extends T> iterable) {
        ArrayList<T> list = new ArrayList<>();
        if (iterable != null) {
            iterable.forEach(list::add);
        }
        return list;
    }
}
