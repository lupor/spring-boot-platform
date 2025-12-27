/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.aspects.core.around;

import de.sky.newcrm.apims.spring.flow.ApimsFlowContextHolder;
import de.sky.newcrm.apims.spring.flow.ApimsFlowMethodReference;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.Ordered;

@SuppressWarnings({"java:S3776", "java:S6201", "java:S6212"})
public class ApimsAroundFlowContextListener implements ApimsAroundListener {

    private static final Field methodInvocationField;

    static {
        methodInvocationField = ObjectUtils.findField(MethodInvocationProceedingJoinPoint.class, "methodInvocation");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void beforeAroundMethod(ApimsAroundContext context) {
        if (context.getActiveCallsCount() == 1) {

            //        TODO: extract this stuff to dedicated starters / modules
            //            if (ApimsAspectType.RESTCONTROLLER.equals(context.getType())) {
            //                ApimsFlowContextHolder.setInboundHttpServletRequest(
            //                        ((ServletRequestAttributes)
            // RequestContextHolder.currentRequestAttributes()).getRequest());
            //            } else if (ApimsSftpDownloadedFileReceiver.class.isAssignableFrom(context.getDeclaringType()))
            // {
            //                ApimsFlowContextHolder.setInboundSftpDownloadedFile(
            //                        getSftpDownloadedFile(context.getProceedingJoinPoint().getArgs()));
            //            } else if (context.getProceedingJoinPoint().getArgs().length != 0) {
            //                ConsumerRecord<?, ?> consumerRecord =
            //                        getConsumerRecord(context.getProceedingJoinPoint().getArgs());
            //                if (consumerRecord != null) {
            //                    ApimsFlowContextHolder.setInboundConsumerRecord(consumerRecord);
            //                } else {
            //                    PubsubMessage pubsubMessage =
            //                            getPubsubMessage(context.getProceedingJoinPoint().getArgs());
            //                    if (pubsubMessage != null) {
            //                        ApimsFlowContextHolder.setInboundPubsubMessage(pubsubMessage);
            //                    }
            //                }
            //            }
            //        }
        }
        Method method = null;
        if (methodInvocationField != null
                && context.getProceedingJoinPoint() instanceof MethodInvocationProceedingJoinPoint) {
            Object target = ObjectUtils.getField(methodInvocationField, context.getProceedingJoinPoint());
            method = target instanceof ProxyMethodInvocation pmi ? pmi.getMethod() : null;
        }
        ApimsFlowContextHolder.pushFlowMethodReference(ApimsFlowMethodReference.builder()
                .method(method)
                .args(context.getProceedingJoinPoint().getArgs())
                .build());
    }

    @Override
    public void afterAroundMethod(ApimsAroundContext context, Object result, Exception resultError) {

        ApimsFlowContextHolder.popFlowMethodReference();
        if (context.getActiveCallsCount() == 1) {
            ApimsFlowContextHolder.resetFlowContext();
        }
    }
    //    TODO: Same. Extract to dedicated starters / modules.
    //    private ConsumerRecord<?, ?> getConsumerRecord(Object[] arguments) {
    //        for (Object object : arguments) {
    //            if (object instanceof ConsumerRecord<?, ?> record) {
    //                return record;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    private PubsubMessage getPubsubMessage(Object[] arguments) {
    //        for (Object object : arguments) {
    //            if (object instanceof PubsubMessage message) {
    //                return message;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    private ApimsSftpDownloadedFile getSftpDownloadedFile(Object[] arguments) {
    //        for (Object object : arguments) {
    //            if (object instanceof ApimsSftpDownloadedFile file) {
    //                return file;
    //            }
    //        }
    //        return null;
    //    }
}
