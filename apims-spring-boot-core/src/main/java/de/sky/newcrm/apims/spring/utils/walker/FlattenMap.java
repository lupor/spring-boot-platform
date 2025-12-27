/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.walker;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FlattenMap extends TreeMap<String, Object> {

    public FlattenMap() {}

    public FlattenMap(Comparator<String> comparator) {
        super(comparator);
    }

    public FlattenMap(Map<String, ?> m) {
        super(m);
    }

    public FlattenMap(SortedMap<String, ?> m) {
        super(m);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path, T defaultValue) {
        return (T) super.getOrDefault(path, defaultValue);
    }
}
