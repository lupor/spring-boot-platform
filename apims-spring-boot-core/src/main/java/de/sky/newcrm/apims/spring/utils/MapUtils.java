/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public interface MapUtils {

    @SafeVarargs
    static <K, V> Map<K, V> ofTreeMapEntries(Map.Entry<? extends K, ? extends V>... entries) {
        return new TreeMap<>(ofEntries(new HashMap<>(entries.length), entries));
    }

    @SafeVarargs
    static <K, V> Map<K, V> ofLinkedHashMapEntries(Map.Entry<? extends K, ? extends V>... entries) {
        return ofEntries(new LinkedHashMap<>(entries.length), entries);
    }

    @SafeVarargs
    static <K, V> Map<K, V> ofEntries(Map<K, V> target, Map.Entry<? extends K, ? extends V>... entries) {
        for (Map.Entry<? extends K, ? extends V> entry : entries) {
            target.put(entry.getKey(), entry.getValue());
        }
        return target;
    }
}
