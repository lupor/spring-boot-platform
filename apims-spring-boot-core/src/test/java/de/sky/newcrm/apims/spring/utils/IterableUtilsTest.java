/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class IterableUtilsTest {

    @Test
    void collectionTest() {

        assertTrue(IterableUtils.toCollection(null).isEmpty());
        assertFalse(IterableUtils.toCollection(List.of("Test")).isEmpty());
        assertFalse(IterableUtils.toCollection(Set.of("Test")).isEmpty());
        assertFalse(
                IterableUtils.toCollection(new TestIterable(List.of("Test"))).isEmpty());
    }

    @Test
    void listTest() {

        assertTrue(IterableUtils.toList(null).isEmpty());
        assertFalse(IterableUtils.toList(List.of("Test")).isEmpty());
        assertFalse(IterableUtils.toList(Set.of("Test")).isEmpty());
        assertFalse(IterableUtils.toList(new TestIterable(List.of("Test"))).isEmpty());
    }

    @Test
    void createArrayListTest() {

        assertTrue(IterableUtils.createArrayList(null).isEmpty());
        assertFalse(IterableUtils.createArrayList(List.of("Test")).isEmpty());
        assertFalse(IterableUtils.createArrayList(Set.of("Test")).isEmpty());
        assertFalse(
                IterableUtils.createArrayList(new TestIterable(List.of("Test"))).isEmpty());
    }

    private static class TestIterable implements Iterable<String> {

        private final List<String> data = new ArrayList<>();

        public TestIterable(Collection<String> data) {
            this.data.addAll(data);
        }

        @Override
        public Iterator<String> iterator() {
            return data.iterator();
        }

        @Override
        public void forEach(Consumer<? super String> action) {
            data.forEach(action);
        }

        @Override
        public Spliterator<String> spliterator() {
            return data.spliterator();
        }
    }
}
