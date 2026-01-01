/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.kafka.core;

import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

@SuppressWarnings({"java:S6212"})
public class ApimsKafkaListenerAnnotationBeanPostProcessor<K, V>
        extends KafkaListenerAnnotationBeanPostProcessor<K, V> {

    private static final String DEFAULT_SPRING_BOOT_KAFKA_TEMPLATE_NAME = "kafkaTemplate";
    private static final Method resolveTopics;

    static {
        resolveTopics = ObjectUtils.findMethod(
                KafkaListenerAnnotationBeanPostProcessor.class, "resolveTopics", true, KafkaListener.class);
        Assert.notNull(resolveTopics, "[Assertion failed] - Method 'resolveTopics' is required; it must not be null");
    }

    private BeanFactory beanFactory;

    @Override
    public synchronized void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        super.setBeanFactory(beanFactory);
    }

    @Override
    protected synchronized void processKafkaListener(
            KafkaListener kafkaListener, Method method, Object bean, String beanName) {

        RetryableTopic retryableTopic = AnnotationUtils.findAnnotation(method, RetryableTopic.class);
        if (retryableTopic == null) {
            ApimsRetryAndDeadletterTopic apimsRetryAndDeadletterTopic =
                    AnnotationUtils.findAnnotation(method, ApimsRetryAndDeadletterTopic.class);
            if (apimsRetryAndDeadletterTopic != null) {
                ConfigurableListableBeanFactory configurableListableBeanFactory =
                        (ConfigurableListableBeanFactory) this.beanFactory;
                String retryTopicConfigurationBeanName =
                        method.getDeclaringClass().getName() + "." + method.getName() + ".retryTopicConfiguration";
                RetryTopicConfiguration retryTopicConfiguration = (RetryTopicConfiguration)
                        configurableListableBeanFactory.getSingleton(retryTopicConfigurationBeanName);
                if (retryTopicConfiguration == null) {
                    Assert.notNull(
                            resolveTopics,
                            "[Assertion failed] - Method 'resolveTopics' is required; it must not be null");
                    String[] topics = ObjectUtils.invokeMethod(resolveTopics, this, kafkaListener);
                    for (String topic : topics) {
                        retryTopicConfiguration = createRetryTopicConfiguration(
                                apimsRetryAndDeadletterTopic, kafkaListener, method, bean, beanName, topic);
                        configurableListableBeanFactory.registerSingleton(
                                retryTopicConfigurationBeanName, retryTopicConfiguration);
                    }
                }
            }
        }
        super.processKafkaListener(kafkaListener, method, bean, beanName);
    }

    public ApimsRetryTopicConfigurationResolver getRetryAndDeadletterTopicConfigurationResolver() {
        return beanFactory.getBean(ApimsRetryTopicConfigurationResolver.class);
    }

    protected RetryTopicConfiguration createRetryTopicConfiguration(
            ApimsRetryAndDeadletterTopic annotation,
            KafkaListener kafkaListener,
            Method method,
            Object bean,
            String beanName,
            String topic) {

        return getRetryAndDeadletterTopicConfigurationResolver()
                .createConfiguration(annotation, kafkaListener, getKafkaTemplate(), method, bean, beanName, topic);
    }

    @SuppressWarnings("unchecked")
    protected KafkaOperations<Object, Object> getKafkaTemplate() {
        try {
            return this.beanFactory.getBean(
                    RetryTopicInternalBeanNames.DEFAULT_KAFKA_TEMPLATE_BEAN_NAME, KafkaOperations.class);
        } catch (NoSuchBeanDefinitionException ex) {
            try {
                return this.beanFactory.getBean(DEFAULT_SPRING_BOOT_KAFKA_TEMPLATE_NAME, KafkaOperations.class);
            } catch (NoSuchBeanDefinitionException exc) {
                exc.addSuppressed(ex);
                throw new BeanInitializationException(
                        "Could not find a KafkaTemplate to configure the retry topics.", // NOSONAR (lost stack trace)
                        exc);
            }
        }
    }
}
