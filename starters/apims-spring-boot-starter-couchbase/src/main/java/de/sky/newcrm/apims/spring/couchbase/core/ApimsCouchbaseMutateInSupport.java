/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.couchbase.core;


import com.couchbase.client.java.codec.JsonSerializer;
import com.couchbase.client.java.kv.MutateInSpec;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;

import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInSpec;
import de.sky.newcrm.apims.spring.couchbase.core.entity.ApimsMutateInType;
import de.sky.newcrm.apims.spring.utils.AssertUtils;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.core.TypeInformation;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.core.mapping.CouchbaseDocument;
import org.springframework.data.couchbase.core.mapping.event.*;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.util.ReflectionUtils;

@Slf4j
@SuppressWarnings({"java:S119"})
public class ApimsCouchbaseMutateInSupport implements ApplicationContextAware {

    private final boolean mutateInCustomConversionsEnabled;
    private ApplicationContext applicationContext;

    private final MappingCouchbaseConverter mappingCouchbaseConverter;
    private final CustomConversions customConversions;
    private final JsonSerializer jsonSerializer;

    private EntityCallbacks entityCallbacks;

    public ApimsCouchbaseMutateInSupport(
            boolean mutateInCustomConversionsEnabled,
            MappingCouchbaseConverter mappingCouchbaseConverter,
            CustomConversions customConversions,
            JsonSerializer jsonSerializer) {
        this.mutateInCustomConversionsEnabled = mutateInCustomConversionsEnabled;
        this.mappingCouchbaseConverter = mappingCouchbaseConverter;
        this.customConversions = customConversions;
        this.jsonSerializer = jsonSerializer;
    }

    public boolean isMutateInCustomConversionsEnabled() {
        return mutateInCustomConversionsEnabled;
    }

    public MappingCouchbaseConverter getMappingCouchbaseConverter() {
        return mappingCouchbaseConverter;
    }

    public CustomConversions getCustomConversions() {
        return customConversions;
    }

    protected JsonSerializer getJsonSerializer() {
        return jsonSerializer;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        entityCallbacks = EntityCallbacks.create(applicationContext);
    }

    public String toString(ApimsMutateInSpec... updates) {

        convertMutateInSpecsToWrite(updates);
        StringBuilder buf = new StringBuilder(100);
        for (ApimsMutateInSpec update : updates) {
            if (buf.length() != 0) {
                buf.append(", ");
            }
            buf.append("[")
                    .append(update.getType())
                    .append("->")
                    .append(update.getPath() == null ? "" : update.getPath());
            if (!ApimsMutateInType.REMOVE.equals(update.getType())) {
                byte[] fragment =
                        update.getValue() == null ? null : getJsonSerializer().serialize(update.getValue());
                buf.append("=").append(fragment == null ? null : new String(fragment, StandardCharsets.UTF_8));
            }
            buf.append("]");
        }
        return buf.toString();
    }

    public CouchbaseDocument encodeEntity(String id, Object entityToEncode) {
        maybeEmitEvent(new BeforeConvertEvent<>(entityToEncode));
        Object maybeNewEntity = maybeCallBeforeConvert(entityToEncode, "");
        final CouchbaseDocument converted = new CouchbaseDocument(id);
        mappingCouchbaseConverter.write(maybeNewEntity, converted);
        maybeCallAfterConvert(entityToEncode, converted, "");
        maybeEmitEvent(new BeforeSaveEvent<>(entityToEncode, converted));
        return converted;
    }

    protected List<MutateInSpec> translate(ApimsMutateInSpec... inSpecs) {
        List<com.couchbase.client.java.kv.MutateInSpec> outSpecs = new ArrayList<>();
        if (inSpecs != null) {
            convertMutateInSpecsToWrite(inSpecs);
            for (ApimsMutateInSpec inSpec : inSpecs) {
                com.couchbase.client.java.kv.MutateInSpec outSpec = null;
                if (ApimsMutateInType.UPSERT.equals(inSpec.getType())) {
                    outSpec = com.couchbase.client.java.kv.MutateInSpec.upsert(inSpec.getPath(), inSpec.getValue());
                } else if (ApimsMutateInType.ARRAY_APPEND.equals(inSpec.getType())) {
                    outSpec = com.couchbase.client.java.kv.MutateInSpec.arrayAppend(
                            inSpec.getPath(), (List<?>) inSpec.getValue());
                } else if (ApimsMutateInType.REMOVE.equals(inSpec.getType())) {
                    outSpec = com.couchbase.client.java.kv.MutateInSpec.remove(inSpec.getPath());
                }
                if (outSpec != null) {
                    outSpecs.add(outSpec);
                }
            }
        }
        return outSpecs;
    }

    protected void convertMutateInSpecsToWrite(ApimsMutateInSpec... inSpecs) {
        if (!mutateInCustomConversionsEnabled) {
            return;
        }
        for (ApimsMutateInSpec inSpec : inSpecs) {
            if (!inSpec.isConvertedForWrite()) {
                inSpec.setValue(convertMutateInObjectToWrite(inSpec.getValue()));
                inSpec.setConvertedForWrite(true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Object convertRootObjectToWrite(String id, Object entityToEncode) {
        if (entityToEncode == null) {
            return null;
        }
        CouchbaseDocument doc = encodeEntity(id, entityToEncode);
        return doc.export();
    }

    @SuppressWarnings({"unchecked", "java:S1602"})
    protected Object convertMutateInObjectToWrite(Object object) {
        if (object == null) {
            return null;
        }
        CustomConversions conversions = getCustomConversions();
        Class<?> sourceClass = object.getClass();
        TypeInformation<?> valueType = TypeInformation.of(sourceClass);
        if (valueType.isCollectionLike()) {
            java.util.Collection<?> collection = ObjectUtils.asCollection(object);
            if (collection.isEmpty()) {
                return collection;
            }
            List<Object> list = new ArrayList<>();
            for (Object o : collection) {
                list.add(convertMutateInObjectToWrite(o));
            }
            return list;
        } else if (valueType.isMap()) {
            Map<Object, Object> data = (Map<Object, Object>) object;
            Map<Object, Object> targetData = new TreeMap<>();
            for (Map.Entry<Object, Object> entry : data.entrySet()) {
                targetData.put(entry.getKey(), convertMutateInObjectToWrite(entry.getValue()));
            }
            return targetData;
        }

        if (conversions.isSimpleType(sourceClass)) {
            Optional<Class<?>> target = conversions.getCustomWriteTarget(object.getClass());
            if (target.isPresent()) {
                return getMappingCouchbaseConverter().getConversionService().convert(object, target.get());
            }
            return object;
        }
        Map<String, Object> data = new TreeMap<>();
        ReflectionUtils.doWithFields(sourceClass, field -> {
            data.put(field.getName(), convertMutateInObjectToWrite((ObjectUtils.getField(field, object, true))));
        });
        return data;
    }

    public <T> T mutateInByCrudOperations(Object repository, Object id, ApimsMutateInSpec... specs) {
        AssertUtils.notNullCheck("repository", repository);
        return mutateInByCrudOperations(new DefaultCrudOperationsRepositoryCallback(repository), id, specs);
    }

    @SuppressWarnings({"unchecked", "java:S2259", "java:S1874"})
    public <T> T mutateInByCrudOperations(RepositoryCallback callback, Object id, ApimsMutateInSpec... specs) {
        AssertUtils.notNullCheck("callback", callback);
        AssertUtils.notNullCheck("id", id);
        AssertUtils.notNullCheck("specs", specs);
        Object entity = callback.findById(id).orElse(null);
        if (specs.length == 0) {
            return (T) entity;
        }
        AssertUtils.notNullCheck("entity", entity);
        entity = ApimsCouchbaseUtils.updateEntityBySpecs(entity, specs);
        return callback.save(entity);
    }

    public interface RepositoryCallback {

        Optional<Object> findById(Object id);

        <T> T save(Object entity);
    }

    public static class DefaultCrudOperationsRepositoryCallback implements RepositoryCallback {

        private final Object repository;

        public DefaultCrudOperationsRepositoryCallback(Object repository) {
            this.repository = repository;
        }

        @Override
        public Optional<Object> findById(Object id) {
            Method method = ObjectUtils.findMethod(repository.getClass(), "findById", Object.class);
            AssertUtils.notNullCheck(repository.getClass() + ".findById(ID)", method);
            return ObjectUtils.invokeMethod(method, repository, id);
        }

        @Override
        public <T> T save(Object entity) {
            Method method = ObjectUtils.findMethod(repository.getClass(), "save", Object.class);
            AssertUtils.notNullCheck(repository.getClass() + ".save(T)", method);
            return ObjectUtils.invokeMethod(method, repository, entity);
        }
    }

    protected void maybeEmitEvent(CouchbaseMappingEvent<?> event) {
        if (canPublishEvent()) {
            try {
                this.applicationContext.publishEvent(event);
            } catch (Exception e) {
                log.warn("{} thrown during {}", e, event);
                throw e;
            }
        } else {
            log.info("maybeEmitEvent called, but entityCallbacks not initialized by applicationContext");
        }
    }

    protected boolean canPublishEvent() {
        return this.applicationContext != null;
    }

    protected <T> T maybeCallBeforeConvert(T object, String collection) {
        if (entityCallbacks != null) {
            return entityCallbacks.callback(BeforeConvertCallback.class, object, collection);
        } else {
            log.info("maybeCallBeforeConvert called, but entityCallbacks not initialized by applicationContext");
        }
        return object;
    }

    protected <T> T maybeCallAfterConvert(T object, CouchbaseDocument document, String collection) {
        if (null != entityCallbacks) {
            return entityCallbacks.callback(AfterConvertCallback.class, object, document, collection);
        } else {
            log.info("maybeCallAfterConvert called, but entityCallbacks not initialized by applicationContext");
        }
        return object;
    }
}
