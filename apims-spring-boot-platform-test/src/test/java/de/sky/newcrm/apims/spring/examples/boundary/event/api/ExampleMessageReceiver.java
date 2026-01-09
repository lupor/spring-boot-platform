/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.examples.boundary.event.api;

import de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;

public interface ExampleMessageReceiver {
    public void onCreateCustomer(CreateCustomer createCustomer);

    public void onDeleteCustomer(DeleteCustomer deleteCustomer);

    default void processEvent(ConsumerRecord<String, Payload> record) {
        String message = record.value().getMessage();
        BeanWrapper wrapper = new BeanWrapperImpl(record.value());
        Object obj = wrapper.getPropertyValue(message);
        TypeDescriptor descriptor = wrapper.getPropertyTypeDescriptor(message);
        if (descriptor != null) {
            if ("de.sky.newcrm.examples.boundary.event.model.CreateCustomer".equals(descriptor.getName())) {
                onCreateCustomer((CreateCustomer) obj);
            }

            if ("de.sky.newcrm.examples.boundary.event.model.DeleteCustomer".equals(descriptor.getName())) {
                onDeleteCustomer((DeleteCustomer) obj);
            }
        }
    }
}
