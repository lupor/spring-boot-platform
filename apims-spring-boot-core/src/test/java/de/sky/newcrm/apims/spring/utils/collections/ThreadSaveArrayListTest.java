/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.collections;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

class ThreadSaveArrayListTest {

    @Test
    void threadSaveArrayListConstructorTest() {
        ThreadSaveArrayList<TestData> list =
                new ThreadSaveArrayList<>(List.of(TestData.builder().build()));
        assertFalse(list.isEmpty());
    }

    @Test
    void threadSaveArrayListTest() {
        ThreadSaveArrayList<TestData> list = new ThreadSaveArrayList<>();
        assertTrue(list.isEmpty());
        list.add(TestData.builder().id("id1").name("name1").build());
        assertEquals(1, list.size());
        list.addAll(List.of(
                TestData.builder().id("id2").name("name2").build(),
                TestData.builder().id("id3").name("name3").build(),
                TestData.builder().id("id5").name("name5").build(),
                TestData.builder().id("id4").name("name4").build()));
        assertEquals(5, list.size());
        assertEquals(5, list.getAll().size());
        assertEquals("id1", list.getAll().get(0).getId());
        assertEquals("id5", list.getAll().get(3).getId());
        assertEquals("id4", list.getAll().get(4).getId());
        list.sort(Comparator.comparing(TestData::getId));
        assertEquals("id1", list.getAll().get(0).getId());
        assertEquals("id5", list.getAll().get(4).getId());
        assertEquals(1, list.filter(e -> "id3".equals(e.getId())).size());
        assertEquals("id3", list.filter(e -> "id3".equals(e.getId())).get(0).getId());
        list.accept(items -> items.remove(4));
        assertEquals(4, list.size());
        list.replace(TestData.builder().id("id1").name("name1").build());
        assertEquals(4, list.size());
        list.replace(TestData.builder().id("id5").name("name5").build());
        assertEquals(5, list.size());
        list.remove(TestData.builder().id("id5").name("name5").build());
        assertEquals(4, list.size());
        list.add(TestData.builder().id("id5").name("name5").build());
        assertEquals(5, list.size());
        list.removeAll(List.of(
                TestData.builder().id("id4").name("name4").build(),
                TestData.builder().id("id5").name("name5").build()));
        assertEquals(3, list.size());
        list.replaceAll(List.of(
                TestData.builder().id("id2").name("name2").build(),
                TestData.builder().id("id3").name("name3").build(),
                TestData.builder().id("id4").name("name4").build(),
                TestData.builder().id("id5").name("name5").build()));
        assertEquals(5, list.size());
        list.retainAll(List.of(
                TestData.builder().id("id1").name("name1").build(),
                TestData.builder().id("id2").name("name2").build()));
        assertEquals(2, list.size());
        list.removeIf(e -> "id2".equals(e.getId()));
        assertEquals(1, list.size());
        assertEquals(1, list.stream().toList().size());

        list.clear();
        assertTrue(list.isEmpty());
        assertTrue(list.stream().toList().isEmpty());
    }

    @Builder
    @Getter
    @Setter
    @EqualsAndHashCode
    private static class TestData {
        private String id;
        private String name;
    }
}
