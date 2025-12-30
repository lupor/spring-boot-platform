/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core.pool;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class ApimsResourcePoolImpl<T> implements ApimsResourcePool<T> {

    private final List<AllocatedResource<T>> availablePoolList = new ArrayList<>();
    private final Map<String, AllocatedResource<T>> allocatedPoolMap = new ConcurrentHashMap<>();
    private final int maxAvailableResources;
    private final int maxResourceLifetimeSeconds;

    protected ApimsResourcePoolImpl(int maxAvailableResources, int maxResourceLifetimeSeconds) {
        this.maxAvailableResources = maxAvailableResources;
        this.maxResourceLifetimeSeconds = maxResourceLifetimeSeconds;
    }

    protected abstract T createResource();

    protected abstract boolean isResourceValid(T resource);

    protected abstract void disposeResource(T resource);

    @Override
    public void afterPropertiesSet() throws Exception {}

    @Override
    public T allocate() {
        synchronized (allocatedPoolMap) {
            AllocatedResource<T> allocatedResource = null;
            final String allocatedKey = Thread.currentThread().getName();
            if (allocatedPoolMap.containsKey(allocatedKey)) {
                allocatedResource = allocatedPoolMap.remove(allocatedKey);
            }
            if (allocatedResource == null && !availablePoolList.isEmpty()) {
                allocatedResource = availablePoolList.remove(0);
            }
            if (allocatedResource != null
                    && (isResourceTimeout(allocatedResource) || !isResourceValid(allocatedResource.getResource()))) {
                disposeResource(allocatedResource.getResource());
                allocatedResource = null;
            }
            if (allocatedResource == null) {
                log.trace("no valid resource in pool. create new one...");
                T newResource = createResource();
                Assert.notNull(newResource, "New created resource must not be null!");
                allocatedResource = new AllocatedResource<>(newResource);
                allocatedResource.setAllocatedTimeMillis(System.currentTimeMillis());
            } else {
                log.trace("resource from pool...");
            }
            allocatedResource.setThreadName(allocatedKey);
            allocatedPoolMap.put(allocatedKey, allocatedResource);
            return allocatedResource.getResource();
        }
    }

    @Override
    public void release(T resource) {
        synchronized (allocatedPoolMap) {
            AllocatedResource<T> allocatedResource = null;
            final String allocatedKey = Thread.currentThread().getName();
            if (allocatedPoolMap.containsKey(allocatedKey)) {
                allocatedResource = allocatedPoolMap.remove(allocatedKey);
            }
            if (allocatedResource != null) {
                if (availablePoolList.size() >= maxAvailableResources
                        || isResourceTimeout(allocatedResource)
                        || !isResourceValid(allocatedResource.getResource())) {
                    log.trace("dispose resource...");
                    disposeResource(allocatedResource.getResource());
                } else {
                    log.trace("return resource in pool...");
                    availablePoolList.add(allocatedResource);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {

        synchronized (allocatedPoolMap) {
            for (AllocatedResource<T> allocatedResource : availablePoolList) {
                try {
                    log.trace("dispose pool resource...");
                    disposeResource(allocatedResource.getResource());
                } catch (Exception e) {
                    log.trace(
                            "dispose resource {} failed: {}",
                            allocatedResource.getResource().getClass().getSimpleName(),
                            e.getMessage());
                }
            }
            availablePoolList.clear();
            for (AllocatedResource<T> allocatedResource : allocatedPoolMap.values()) {
                try {
                    log.trace("dispose allocated resource...");
                    disposeResource(allocatedResource.getResource());
                } catch (Exception e) {
                    log.trace(
                            "dispose resource {} -> {} failed: {}",
                            allocatedResource.getThreadName(),
                            allocatedResource.getResource().getClass().getSimpleName(),
                            e.getMessage());
                }
            }
            allocatedPoolMap.clear();
        }
    }

    protected boolean isResourceTimeout(AllocatedResource<T> resource) {
        return maxResourceLifetimeSeconds > 0
                && resource.getAllocatedTimeMillis() + (1000L * maxResourceLifetimeSeconds)
                        < System.currentTimeMillis();
    }

    @Getter
    @Setter
    static class AllocatedResource<T> {
        private String threadName;
        private long allocatedTimeMillis;
        private final T resource;

        public AllocatedResource(T resource) {
            this.resource = resource;
        }
    }
}
