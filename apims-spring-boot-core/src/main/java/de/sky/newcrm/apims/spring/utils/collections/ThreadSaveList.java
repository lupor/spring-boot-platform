/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class ThreadSaveList<T> {

    final Object lock = new Object();

    ThreadSaveList() {}

    public int size() {
        synchronized (lock) {
            return getEntitiesInternal().size();
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            return getEntitiesInternal().isEmpty();
        }
    }

    public boolean add(T t) {
        synchronized (lock) {
            return getEntitiesInternal().add(t);
        }
    }

    public boolean remove(T t) {
        synchronized (lock) {
            return getEntitiesInternal().remove(t);
        }
    }

    public boolean replace(T t) {
        synchronized (lock) {
            final List<T> list = getEntitiesInternal();
            list.remove(t);
            return list.add(t);
        }
    }

    public boolean replaceAll(Collection<? extends T> c) {
        synchronized (lock) {
            for (T t : c) {
                final List<T> list = getEntitiesInternal();
                list.remove(t);
                list.add(t);
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends T> c) {
        synchronized (lock) {
            return getEntitiesInternal().addAll(c);
        }
    }

    public boolean removeAll(Collection<?> c) {
        synchronized (lock) {
            return getEntitiesInternal().removeAll(c);
        }
    }

    public boolean removeIf(Predicate<? super T> filter) {
        synchronized (lock) {
            return getEntitiesInternal().removeIf(filter);
        }
    }

    public boolean retainAll(Collection<?> c) {
        synchronized (lock) {
            return getEntitiesInternal().retainAll(c);
        }
    }

    public void clear() {
        synchronized (lock) {
            getEntitiesInternal().clear();
        }
    }

    public List<T> getAll() {
        synchronized (lock) {
            return getEntitiesInternal().stream().toList();
        }
    }

    @SuppressWarnings("java:S3958")
    public List<T> filter(Predicate<? super T> filter) {
        return stream().filter(filter).toList();
    }

    public Stream<T> stream() {
        return getAll().stream();
    }

    public void sort(Comparator<T> c) {
        synchronized (lock) {
            getEntitiesInternal().sort(c);
        }
    }

    public void accept(Consumer<List<T>> consumer) {
        synchronized (lock) {
            consumer.accept(getEntitiesInternal());
        }
    }

    abstract List<T> getEntitiesInternal();
}
