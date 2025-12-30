/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.tasking.core;

import static org.junit.jupiter.api.Assertions.*;

import de.sky.newcrm.apims.spring.tasking.core.pool.ApimsResourcePoolImpl;
import de.sky.newcrm.apims.spring.utils.FunctionUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class PoolTest {

    @Test
    @SuppressWarnings("java:S2925")
    void resourcePoolTest() throws Exception {
        TestResourcePoolImpl instance = new TestResourcePoolImpl(1, -1);
        instance.afterPropertiesSet();
        TestResource testResource = instance.allocate();
        assertNotNull(testResource);
        assertEquals(testResource, instance.allocate());
        instance.release(testResource);
        assertEquals(testResource, instance.allocate());
        instance.resourceValid = false;
        assertNotEquals(testResource, instance.allocate());
        instance.destroy();
        instance.resourceValid = true;
        testResource = instance.allocate();
        instance.release(testResource);
        instance.destroy();

        instance = new TestResourcePoolImpl(0, -1);
        testResource = instance.allocate();
        assertNotNull(testResource);
        assertEquals(testResource, instance.allocate());
        instance.release(testResource);
        assertNotEquals(testResource, instance.allocate());
        instance.destroy();

        instance = new TestResourcePoolImpl(1, 1);
        testResource = instance.allocate();
        assertNotNull(testResource);
        assertEquals(testResource, instance.allocate());
        instance.release(testResource);
        TimeUnit.SECONDS.sleep(2);
        assertNotEquals(testResource, instance.allocate());
        instance.destroy();
    }

    @Test
    void resourcePoolThreadTest() throws Exception {
        final TestResourcePoolImpl instance = new TestResourcePoolImpl(1, -1);
        final TestResource testResource = instance.allocate();
        ApimsExecutor executor = new ApimsExecutor(1, 1);
        instance.disposeException = true;
        executor.execute(() -> {
            TestResource testResource2 = instance.allocate();
            assertNotEquals(testResource, testResource2);
            FunctionUtils.execute(instance::destroy);
        });
        instance.resourceValid = true;
        instance.release(testResource);
        instance.destroy();
    }

    static class TestResourcePoolImpl extends ApimsResourcePoolImpl<TestResource> {

        boolean resourceValid = true;
        boolean disposeException = false;

        public TestResourcePoolImpl(int maxAvailableResources, int maxResourceLifetimeSeconds) {
            super(maxAvailableResources, maxResourceLifetimeSeconds);
        }

        @Override
        protected TestResource createResource() {
            return new TestResource();
        }

        @Override
        protected boolean isResourceValid(TestResource resource) {
            return resourceValid;
        }

        @Override
        protected void disposeResource(TestResource resource) {
            if (disposeException) {
                throw new IllegalStateException("test");
            }
            resource.close();
        }
    }

    static class TestResource {
        private String id = UUID.randomUUID().toString();

        void close() {}

        @Override
        public boolean equals(Object obj) {
            return id.equals(((TestResource) obj).id);
        }
    }
}
