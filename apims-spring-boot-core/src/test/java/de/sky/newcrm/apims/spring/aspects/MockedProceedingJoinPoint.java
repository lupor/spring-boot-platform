/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

public class MockedProceedingJoinPoint implements ProceedingJoinPoint {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final Object target;
    private final Method targetMethod;
    private final Object[] args;

    private Object[] targetMethodArgs;

    private Signature signature;

    private SourceLocation sourceLocation;

    public MockedProceedingJoinPoint(Object target, Method targetMethod) {
        this(target, targetMethod, new Object[0]);
    }

    public MockedProceedingJoinPoint(Object target, Method targetMethod, Object[] args) {
        this.target = target;
        this.targetMethod = targetMethod;
        this.args = args;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getTargetMethodArgs() {
        return targetMethodArgs;
    }

    public void setTargetMethodArgs(Object... targetMethodArgs) {
        this.targetMethodArgs = targetMethodArgs;
    }

    @Override
    public void set$AroundClosure(AroundClosure arc) {}

    @Override
    public Object proceed() throws Throwable {
        return ReflectionUtils.invokeMethod(
                targetMethod, target, targetMethodArgs == null ? new Object[] {} : targetMethodArgs);
    }

    @Override
    public Object proceed(Object[] args) throws Throwable {
        return ReflectionUtils.invokeMethod(targetMethod, target, args);
    }

    @Override
    public String toShortString() {
        return null;
    }

    @Override
    public String toLongString() {
        return null;
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    @Override
    public Signature getSignature() {
        if (this.signature == null) {
            this.signature = new MethodSignatureImpl();
        }
        return this.signature;
    }

    @Override
    public SourceLocation getSourceLocation() {
        if (this.sourceLocation == null) {
            this.sourceLocation = new SourceLocationImpl();
        }
        return this.sourceLocation;
    }

    @Override
    public String getKind() {
        return null;
    }

    @Override
    public StaticPart getStaticPart() {
        return null;
    }

    private class MethodSignatureImpl implements MethodSignature {

        private volatile String[] parameterNames;

        @Override
        public String getName() {
            return getMethod().getName();
        }

        @Override
        public int getModifiers() {
            return targetMethod.getModifiers();
        }

        @Override
        public Class<?> getDeclaringType() {
            return target.getClass();
        }

        @Override
        public String getDeclaringTypeName() {
            return target.getClass().getName();
        }

        @Override
        public Class<?> getReturnType() {
            return targetMethod.getReturnType();
        }

        @Override
        public Method getMethod() {
            return targetMethod;
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return targetMethod.getParameterTypes();
        }

        @Override
        public String[] getParameterNames() {
            String[] parameterNames = this.parameterNames;
            if (parameterNames == null) {
                parameterNames = parameterNameDiscoverer.getParameterNames(getMethod());
                this.parameterNames = parameterNames;
            }
            return parameterNames;
        }

        @Override
        public Class<?>[] getExceptionTypes() {
            return targetMethod.getExceptionTypes();
        }

        @Override
        public String toShortString() {
            return toString(false, false, false, false);
        }

        @Override
        public String toLongString() {
            return toString(true, true, true, true);
        }

        @Override
        public String toString() {
            return toString(false, true, false, true);
        }

        private String toString(
                boolean includeModifier,
                boolean includeReturnTypeAndArgs,
                boolean useLongReturnAndArgumentTypeName,
                boolean useLongTypeName) {

            StringBuilder sb = new StringBuilder();
            if (includeModifier) {
                sb.append(Modifier.toString(getModifiers()));
                sb.append(' ');
            }
            if (includeReturnTypeAndArgs) {
                appendType(sb, getReturnType(), useLongReturnAndArgumentTypeName);
                sb.append(' ');
            }
            appendType(sb, getDeclaringType(), useLongTypeName);
            sb.append('.');
            sb.append(getMethod().getName());
            sb.append('(');
            Class<?>[] parametersTypes = getParameterTypes();
            appendTypes(sb, parametersTypes, includeReturnTypeAndArgs, useLongReturnAndArgumentTypeName);
            sb.append(')');
            return sb.toString();
        }

        private void appendTypes(
                StringBuilder sb, Class<?>[] types, boolean includeArgs, boolean useLongReturnAndArgumentTypeName) {

            if (includeArgs) {
                for (int size = types.length, i = 0; i < size; i++) {
                    appendType(sb, types[i], useLongReturnAndArgumentTypeName);
                    if (i < size - 1) {
                        sb.append(',');
                    }
                }
            } else {
                if (types.length != 0) {
                    sb.append("..");
                }
            }
        }

        private void appendType(StringBuilder sb, Class<?> type, boolean useLongTypeName) {
            if (type.isArray()) {
                appendType(sb, type.getComponentType(), useLongTypeName);
                sb.append("[]");
            } else {
                sb.append(useLongTypeName ? type.getName() : type.getSimpleName());
            }
        }
    }

    private class SourceLocationImpl implements SourceLocation {

        @Override
        public Class<?> getWithinType() {
            return target.getClass();
        }

        @Override
        public String getFileName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getLine() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Deprecated
        public int getColumn() {
            throw new UnsupportedOperationException();
        }
    }
}
