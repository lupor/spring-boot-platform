/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import java.util.List;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S112", "java:S2388", "java:S6548"})
public class FunctionUtils {

    public static final FunctionUtils INSTANCE = new FunctionUtils();

    private FunctionUtils() {}

    public <T> FunctionUtils acceptIfCondition(
            boolean condition, boolean suppressExceptions, @Nullable T value, FailableConsumer<T> consumer) {
        if (condition) {
            consumer.accept(value, suppressExceptions);
        }
        return this;
    }

    public <T> FunctionUtils acceptIfNotNull(@Nullable T value, FailableConsumer<T> consumer) {
        return acceptIfNotNull(value, false, consumer);
    }

    public <T> FunctionUtils acceptIfNotNull(
            @Nullable T value, boolean suppressExceptions, FailableConsumer<T> consumer) {
        if (value != null) {
            consumer.accept(value, suppressExceptions);
        }
        return this;
    }

    public FunctionUtils acceptIfHasText(String value, FailableConsumer<String> consumer) {
        return acceptIfHasText(value, false, consumer);
    }

    public FunctionUtils acceptIfHasText(String value, boolean suppressExceptions, FailableConsumer<String> consumer) {
        if (StringUtils.hasText(value)) {
            consumer.accept(value, suppressExceptions);
        }
        return this;
    }

    public <T> FunctionUtils acceptIfNotEmpty(List<T> value, FailableConsumer<List<T>> consumer) {
        if (!CollectionUtils.isEmpty(value)) {
            consumer.accept(value, false);
        }
        return this;
    }

    public <T> FunctionUtils acceptIfNotEmpty(T[] value, FailableConsumer<T[]> consumer) {
        if (!org.springframework.util.ObjectUtils.isEmpty(value)) {
            consumer.accept(value, false);
        }
        return this;
    }

    public <T1, T2> FunctionUtils acceptIfCondition(
            boolean condition, boolean suppressExceptions, T1 t1, T2 t2, FailableBiConsumer<T1, T2> consumer) {
        if (condition) {
            consumer.accept(t1, t2, suppressExceptions);
        }
        return this;
    }

    public <T1, T2> FunctionUtils acceptIfCondition(
            boolean condition, T1 t1, T2 t2, FailableBiConsumer<T1, T2> consumer) {
        if (condition) {
            consumer.accept(t1, t2, false);
        }
        return this;
    }

    public <T1, T2> FunctionUtils acceptIfNotNull(T1 t1, T2 t2, FailableBiConsumer<T1, T2> consumer) {
        if (t2 != null) {
            consumer.accept(t1, t2, false);
        }
        return this;
    }

    public <T> FunctionUtils acceptIfHasText(T t1, String value, FailableBiConsumer<T, String> consumer) {
        if (StringUtils.hasText(value)) {
            consumer.accept(t1, value, false);
        }
        return this;
    }

    public static <R> R call(FailableCallable<R> delegate) {
        return call(delegate, null);
    }

    public static <R> R call(FailableCallable<R> delegate, Class<? extends RuntimeException> throwRuntimeException) {
        return delegate.call(throwRuntimeException);
    }

    public static <R, T> R apply(T input, FailableFunction<T, R> delegate) {
        return delegate.apply(input, null);
    }

    public static <R, T> R apply(
            Optional<T> optional, FailableFunction<T, R> presentDelegate, FailableCallable<R> notPresentDelegate) {
        return optional.isPresent() ? presentDelegate.apply(optional.get(), null) : notPresentDelegate.call(null);
    }

    public static <R, T> R applyIfPresent(Optional<T> optional, FailableFunction<T, R> delegate) {
        return optional.map(t -> delegate.apply(t, null)).orElse(null);
    }

    public static <R, T> R applyIfPresent(
            Optional<T> optional,
            Class<? extends RuntimeException> notPresentException,
            FailableFunction<T, R> delegate) {
        return applyIfPresent(optional, (RuntimeException) ObjectUtils.createInstance(notPresentException), delegate);
    }

    public static <R, T> R applyIfPresent(
            Optional<T> optional, RuntimeException notPresentException, FailableFunction<T, R> delegate) {
        if (optional.isEmpty()) {
            throw notPresentException;
        }
        return optional.map(t -> delegate.apply(t, null)).orElse(null);
    }

    public static <R> R execute(ExecuteCallback<R> delegate) {
        return execute(delegate, new ExecuteCallbackExceptionHandler<>());
    }

    public static <R> R execute(ExecuteCallback<R> delegate, Class<? extends RuntimeException> throwRuntimeException) {
        return execute(delegate, new ExecuteCallbackExceptionHandler<>(throwRuntimeException));
    }

    public static <R> R execute(ExecuteCallback<R> delegate, ExecuteCallbackExceptionFunction<R> exceptionFunction) {
        return execute(delegate, new ExecuteCallbackExceptionHandler<>(exceptionFunction));
    }

    public static <R> R execute(ExecuteCallback<R> delegate, ExecuteCallbackExceptionHandler<R> exceptionHandler) {
        return delegate.execute(exceptionHandler);
    }

    public static <R> R executeIfNull(R value, ExecuteCallback<R> delegate) {
        return executeIfCondition(value == null, value, delegate, new ExecuteCallbackExceptionHandler<>());
    }

    public static <R> R executeIfNotNull(Object value, R defaultValue, ExecuteCallback<R> delegate) {
        return executeIfNotNull(value, defaultValue, delegate, new ExecuteCallbackExceptionHandler<>());
    }

    public static <R> R executeIfNotNull(
            Object value,
            R defaultValue,
            ExecuteCallback<R> delegate,
            Class<? extends RuntimeException> throwRuntimeException) {
        return executeIfCondition(
                value != null, defaultValue, delegate, new ExecuteCallbackExceptionHandler<>(throwRuntimeException));
    }

    public static <R> R executeIfNotNull(
            Object value,
            R defaultValue,
            ExecuteCallback<R> delegate,
            ExecuteCallbackExceptionFunction<R> exceptionFunction) {
        return executeIfCondition(
                value != null, defaultValue, delegate, new ExecuteCallbackExceptionHandler<>(exceptionFunction));
    }

    public static <R> R executeIfNotNull(
            Object value,
            R defaultValue,
            ExecuteCallback<R> delegate,
            ExecuteCallbackExceptionHandler<R> exceptionHandler) {
        return executeIfCondition(value != null, defaultValue, delegate, exceptionHandler);
    }

    public static <R> R executeIfCondition(boolean condition, R defaultValue, ExecuteCallback<R> delegate) {
        return executeIfCondition(condition, defaultValue, delegate, new ExecuteCallbackExceptionHandler<>());
    }

    public static <R> R executeIfCondition(
            boolean condition,
            R defaultValue,
            ExecuteCallback<R> delegate,
            ExecuteCallbackExceptionFunction<R> exceptionFunction) {
        return executeIfCondition(
                condition, defaultValue, delegate, new ExecuteCallbackExceptionHandler<>(exceptionFunction));
    }

    public static <R> R executeIfCondition(
            boolean condition,
            R defaultValue,
            ExecuteCallback<R> delegate,
            ExecuteCallbackExceptionHandler<R> exceptionHandler) {
        if (condition) {
            return execute(delegate, exceptionHandler);
        } else {
            return defaultValue;
        }
    }

    public static void execute(VoidCallback delegate, boolean suppressExceptions) {
        execute(new VoidCallbackExceptionHandler(suppressExceptions), delegate);
    }

    public static void execute(VoidCallback delegate, Class<? extends RuntimeException> throwRuntimeException) {
        execute(new VoidCallbackExceptionHandler(throwRuntimeException), delegate);
    }

    public static void executeIfNotNull(Object value, VoidCallback delegate) {
        executeIfCondition(value != null, false, delegate);
    }

    public static void executeIfNull(Object value, VoidCallback delegate) {
        executeIfCondition(value == null, false, delegate);
    }

    public static void executeIfCondition(boolean condition, boolean suppressExceptions, VoidCallback delegate) {
        if (condition) {
            execute(new VoidCallbackExceptionHandler(suppressExceptions), delegate);
        }
    }

    public static void execute(VoidCallback... delegates) {
        execute(new VoidCallbackExceptionHandler(), delegates);
    }

    public static void execute(VoidCallbackExceptionFunction exceptionFunction, VoidCallback... delegates) {
        execute(new VoidCallbackExceptionHandler(exceptionFunction), delegates);
    }

    public static void execute(VoidCallbackExceptionHandler exceptionHandler, VoidCallback... delegates) {
        for (VoidCallback delegate : delegates) {
            if (!delegate.execute(exceptionHandler)) {
                return;
            }
        }
    }

    @FunctionalInterface
    public interface FailableConsumer<T> extends org.apache.commons.lang3.function.FailableConsumer<T, Exception> {

        default void accept(T t, boolean suppressExceptions) {
            try {
                accept(t);
            } catch (Throwable e) {
                if (!suppressExceptions) {
                    throw ExceptionUtils.resolveAsRuntimeException(e);
                }
            }
        }
    }

    @FunctionalInterface
    public interface FailableBiConsumer<T, U>
            extends org.apache.commons.lang3.function.FailableBiConsumer<T, U, Exception> {

        default void accept(T t, U u, boolean suppressExceptions) {
            try {
                accept(t, u);
            } catch (Throwable e) {
                if (!suppressExceptions) {
                    throw ExceptionUtils.resolveAsRuntimeException(e);
                }
            }
        }
    }

    @FunctionalInterface
    public interface FailableCallable<R> extends org.apache.commons.lang3.function.FailableCallable<R, Exception> {

        default R call(Class<? extends RuntimeException> throwRuntimeException) {
            try {
                return call();
            } catch (Exception e) {
                throw ExceptionUtils.resolveAsRuntimeException(e, throwRuntimeException);
            }
        }
    }

    @FunctionalInterface
    public interface FailableFunction<T, R>
            extends org.apache.commons.lang3.function.FailableFunction<T, R, Exception> {

        default R apply(T input, Class<? extends RuntimeException> throwRuntimeException) {
            try {
                return apply(input);
            } catch (Exception e) {
                throw ExceptionUtils.resolveAsRuntimeException(e, throwRuntimeException);
            }
        }
    }

    @FunctionalInterface
    public interface ExecuteCallback<R> {

        R execute() throws Exception;

        default R execute(ExecuteCallbackExceptionHandler<R> exceptionHandler) {
            try {
                return execute();
            } catch (Exception e) {
                return exceptionHandler.handleException(e);
            }
        }
    }

    @FunctionalInterface
    public interface VoidCallback {

        void execute() throws Exception;

        default boolean execute(VoidCallbackExceptionHandler exceptionHandler) {
            try {
                execute();
                return true;
            } catch (Exception e) {
                return exceptionHandler.handleException(e);
            }
        }
    }

    @FunctionalInterface
    public interface ExecuteCallbackExceptionFunction<R> {
        R handleException(Exception e);
    }

    public static class ExecuteCallbackExceptionHandler<R> {

        private final ExecuteCallbackExceptionFunction<R> exceptionFunction;
        private final Class<? extends RuntimeException> throwRuntimeException;

        public ExecuteCallbackExceptionHandler() {
            this.exceptionFunction = null;
            this.throwRuntimeException = null;
        }

        public ExecuteCallbackExceptionHandler(Class<? extends RuntimeException> throwRuntimeException) {
            this.exceptionFunction = null;
            this.throwRuntimeException = throwRuntimeException;
        }

        public ExecuteCallbackExceptionHandler(ExecuteCallbackExceptionFunction<R> exceptionFunction) {
            this.exceptionFunction = exceptionFunction;
            this.throwRuntimeException = null;
        }

        public Class<? extends RuntimeException> getThrowRuntimeException() {
            return throwRuntimeException;
        }

        public R handleException(Exception e) {
            if (exceptionFunction == null) {
                throw ExceptionUtils.resolveAsRuntimeException(e, getThrowRuntimeException());
            } else {
                return exceptionFunction.handleException(e);
            }
        }
    }

    @FunctionalInterface
    public interface VoidCallbackExceptionFunction {
        boolean handleException(Exception e);
    }

    public static class VoidCallbackExceptionHandler {

        private final VoidCallbackExceptionFunction exceptionFunction;
        private final boolean suppressExceptions;
        private final Class<? extends RuntimeException> throwRuntimeException;

        public VoidCallbackExceptionHandler() {
            this.exceptionFunction = null;
            this.suppressExceptions = false;
            this.throwRuntimeException = null;
        }

        public VoidCallbackExceptionHandler(boolean suppressExceptions) {
            this.exceptionFunction = null;
            this.suppressExceptions = suppressExceptions;
            this.throwRuntimeException = null;
        }

        public VoidCallbackExceptionHandler(Class<? extends RuntimeException> throwRuntimeException) {
            this.exceptionFunction = null;
            this.suppressExceptions = false;
            this.throwRuntimeException = throwRuntimeException;
        }

        public VoidCallbackExceptionHandler(VoidCallbackExceptionFunction exceptionFunction) {
            this.exceptionFunction = exceptionFunction;
            this.suppressExceptions = false;
            this.throwRuntimeException = null;
        }

        public boolean isSuppressExceptions() {
            return suppressExceptions;
        }

        public Class<? extends RuntimeException> getThrowRuntimeException() {
            return throwRuntimeException;
        }

        public boolean handleException(Exception e) {
            if (!isSuppressExceptions()) {
                if (exceptionFunction == null) {
                    throw ExceptionUtils.resolveAsRuntimeException(e, getThrowRuntimeException());
                } else {
                    return exceptionFunction.handleException(e);
                }
            }
            return true;
        }
    }
}
