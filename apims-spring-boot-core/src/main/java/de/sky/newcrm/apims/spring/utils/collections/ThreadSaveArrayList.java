/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ThreadSaveArrayList<T> extends ThreadSaveList<T> {

    private final List<T> entities = Collections.synchronizedList(new ArrayList<>());

    public ThreadSaveArrayList() {}

    public ThreadSaveArrayList(Collection<? extends T> c) {
        addAll(c);
    }

    List<T> getEntitiesInternal() {
        return entities;
    }
}
