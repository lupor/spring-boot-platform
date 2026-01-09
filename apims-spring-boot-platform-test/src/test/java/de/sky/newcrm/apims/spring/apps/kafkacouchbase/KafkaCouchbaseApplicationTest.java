/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.event.CustomerMessageReceiver;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.boundary.http.CustomerController;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.control.CustomerCouchbaseRepository;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.entity.CustomerDocument;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model.Customer;
import de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureJsonTesters
@SpringBootTest
@ActiveProfiles({"dev", "unit-test"})
// TODO: find out what it does and the required dependency
//@AutoConfigureMetrics
@TestPropertySource(
        properties = {
            "spring.config.location=classpath:testdata/applications/kafkacouchbase/application.yml,classpath:testdata/applications/kafkacouchbase/application-dev.yml,classpath:testdata/applications/kafkacouchbase/application-unit-test.yml"
        })
class KafkaCouchbaseApplicationTest {

    @MockitoBean
    KafkaTemplate<String, Payload> kafkaTemplate;

    @MockitoBean
    CustomerCouchbaseRepository repository;

    @Autowired
    CustomerMessageReceiver receiver;

    @Autowired
    CustomerController controller;

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void greetingShouldReturnDefaultMessage() {
        restTestClient.get().uri("/")
                .exchange()
                .expectBody(String.class)
                .isEqualTo("Hello, World");
    }

    @Value("${apims.kafka.producer.topics.customer-message}")
    String topic;

    @Test
    void getCustomerTest() throws Exception {
        String customerId = "42";
        Customer customer = Customer.builder()
                .name("Max Mustermann")
                .email("Max.Mustermann@test-emea.de")
                .build();

        restTestClient.get().uri("/example/customers/{customerId}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(customer.getName())
                .jsonPath("$.email").isEqualTo(customer.getEmail());
    }

//    @Test
//    void getCustomerCustomerNotFoundTest() throws Exception {
//        String customerId = "42";
//        mockMvc.perform(get("/example/customers/{customerId}", customerId).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.code").value("CUSTOMER_NOT_FOUND"));
//    }
//
//    @Test
//    void getCustomerControllerNotFoundTest() throws Exception {
//        mockMvc.perform(get("/example/customers/{customerId}", "").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void createCustomerTest() throws Exception {
//        testStart();
//        // prepare
//        String customerId = "42";
//        String documentId = CustomerDocument.createDocumentId(customerId);
//        Customer customer = Customer.builder()
//                .name("Max Mustermann")
//                .email("Max.Mustermann@test-emea.de")
//                .build();
//        // execute
//        mockMvc.perform(post("/example/customers/{customerId}", customerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(getJsonValue(customer)))
//                .andExpect(status().isNoContent());
//        // validate
//        assertEquals(1L, kafkaTemplateMockBeanHandler.count());
//        ProducerRecord<String, Payload> producerRecord =
//                kafkaTemplateMockBeanHandler.getEntities().get(0);
//        assertNotNull(producerRecord);
//        assertEquals(customerId, producerRecord.key());
//        assertEquals(topic, producerRecord.topic());
//        assertNotNull(producerRecord.value());
//        assertNotNull(producerRecord.value().getCreateCustomer());
//        assertNotNull(producerRecord.value().getCreateCustomer().getGeneral());
//        assertEquals(
//                customerId,
//                producerRecord.value().getCreateCustomer().getGeneral().getCustomerId());
//        assertEquals(
//                customer.getName(), producerRecord.value().getCreateCustomer().getName());
//        assertEquals(
//                customer.getEmail(), producerRecord.value().getCreateCustomer().getEmail());
//        // prepare
//        ConsumerRecord<String, Payload> consumerRecord =
//                new ConsumerRecord<>(topic, 1, 1, producerRecord.key(), producerRecord.value());
//        // execute
//        receiver.onEvent(consumerRecord);
//        // validate
//        assertEquals(1L, repository.count());
//        CustomerDocument customerDocument = repository.findById(documentId).orElse(null);
//        assertNotNull(customerDocument);
//        assertEquals(CustomerDocument.createDocumentId(customerId), customerDocument.getId());
//        assertEquals(customerId, customerDocument.getCustomerId());
//        assertEquals(customer.getName(), customerDocument.getName());
//        assertEquals(customer.getEmail(), customerDocument.getEmail());
//        assertNotNull(customerDocument.getCustomerTypeEnum());
//        assertNotNull(customerDocument.getSourceSystemTypeEnum());
//
//        logInteractions(kafkaTemplate, repository);
//    }
//
//    @Test
//    void updateCustomerTest() throws Exception {
//        testStart();
//        // prepare
//        String customerId = "42";
//        String documentId = CustomerDocument.createDocumentId(customerId);
//        Customer customer = Customer.builder()
//                .name("Max Mustermann")
//                .email("Max.Mustermann@test-emea.de")
//                .build();
//        Customer savedCustomer = Customer.builder()
//                .name("Max Mustermann1")
//                .email("Max.Mustermann1@test-emea.de")
//                .build();
//        repository.save(CustomerDocument.builder()
//                .id(documentId)
//                .customerId(customerId)
//                .name(savedCustomer.getName())
//                .email(savedCustomer.getEmail())
//                .build());
//        // execute
//        mockMvc.perform(post("/example/customers/{customerId}", customerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(getJsonValue(customer)))
//                .andExpect(status().isNoContent());
//        // validate
//        assertEquals(1L, kafkaTemplateMockBeanHandler.count());
//        ProducerRecord<String, Payload> producerRecord =
//                kafkaTemplateMockBeanHandler.getEntities().get(0);
//        assertNotNull(producerRecord);
//        assertEquals(customerId, producerRecord.key());
//        assertEquals(topic, producerRecord.topic());
//        assertNotNull(producerRecord.value());
//        assertNotNull(producerRecord.value().getCreateCustomer());
//        assertNotNull(producerRecord.value().getCreateCustomer().getGeneral());
//        assertEquals(
//                customerId,
//                producerRecord.value().getCreateCustomer().getGeneral().getCustomerId());
//        assertEquals(
//                customer.getName(), producerRecord.value().getCreateCustomer().getName());
//        assertEquals(
//                customer.getEmail(), producerRecord.value().getCreateCustomer().getEmail());
//        // prepare
//        ConsumerRecord<String, Payload> consumerRecord =
//                new ConsumerRecord<>(topic, 1, 1, producerRecord.key(), producerRecord.value());
//        // execute
//        receiver.onEvent(consumerRecord);
//        // validate
//        assertEquals(1L, repository.count());
//        CustomerDocument customerDocument = repository.findById(documentId).orElse(null);
//        assertNotNull(customerDocument);
//        assertEquals(CustomerDocument.createDocumentId(customerId), customerDocument.getId());
//        assertEquals(customerId, customerDocument.getCustomerId());
//        assertEquals(customer.getName(), customerDocument.getName());
//        assertEquals(customer.getEmail(), customerDocument.getEmail());
//    }
//
//    @Test
//    void createCustomerWithInvalidNameAndEmailPatternTest() throws Exception {
//        testStart();
//        // prepare
//        String customerId = "42";
//        Customer customer =
//                Customer.builder().name(null).email("invalidEmailAddress.de").build();
//        // execute
//        mockMvc.perform(post("/example/customers/{customerId}", customerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(getJsonValue(customer)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("INVALID_REQ_DATA"))
//                .andExpect(jsonPath("$.details.errors", hasSize(2)))
//                .andExpect(jsonPath("$.details.errors[0].field").value("email"))
//                .andExpect(jsonPath("$.details.errors[0].requires").value("Pattern"))
//                .andExpect(jsonPath("$.details.errors[1].field").value("name"))
//                .andExpect(jsonPath("$.details.errors[1].requires").value("NotNull"));
//    }
//
//    @Test
//    void createCustomerWithInvalidEmailTest() throws Exception {
//        testStart();
//        // prepare
//        String customerId = "42";
//        Customer customer = Customer.builder()
//                .name("Max Mustermann")
//                .email("Max.Mustermann@@invalidEmailAddress.de")
//                .build();
//        // execute
//        mockMvc.perform(post("/example/customers/{customerId}", customerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(getJsonValue(customer)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.code").value("INVALID_REQ_DATA"))
//                .andExpect(jsonPath("$.details.errors", hasSize(1)))
//                .andExpect(jsonPath("$.details.errors[0].field").value(oneOf("email")))
//                .andExpect(jsonPath("$.details.errors[0].requires").value("Email"));
//    }
//
//    @Test
//    void deleteCustomerTest() throws Exception {
//        testStart();
//        // prepare
//        String customerId = "42";
//        String documentId = CustomerDocument.createDocumentId(customerId);
//        Customer customer = Customer.builder()
//                .name("Max Mustermann")
//                .email("Max.Mustermann@test-emea.de")
//                .build();
//        repository.save(CustomerDocument.builder()
//                .id(documentId)
//                .customerId(customerId)
//                .name(customer.getName())
//                .email(customer.getEmail())
//                .build());
//        // execute
//        mockMvc.perform(delete("/example/customers/{customerId}", customerId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .content(getJsonValue(customer)))
//                .andExpect(status().isNoContent());
//        // validate
//        assertEquals(1L, kafkaTemplateMockBeanHandler.count());
//        ProducerRecord<String, Payload> producerRecord =
//                kafkaTemplateMockBeanHandler.getEntities().get(0);
//        assertNotNull(producerRecord);
//        assertEquals(customerId, producerRecord.key());
//        assertEquals(topic, producerRecord.topic());
//        assertNotNull(producerRecord.value());
//        assertNotNull(producerRecord.value().getDeleteCustomer());
//        assertNotNull(producerRecord.value().getDeleteCustomer().getGeneral());
//        assertEquals(
//                customerId,
//                producerRecord.value().getDeleteCustomer().getGeneral().getCustomerId());
//        assertEquals(
//                customer.getName(), producerRecord.value().getDeleteCustomer().getName());
//        assertEquals(
//                customer.getEmail(), producerRecord.value().getDeleteCustomer().getEmail());
//        // prepare
//        ConsumerRecord<String, Payload> incomingRecord =
//                new ConsumerRecord<>(topic, 1, 1, producerRecord.key(), producerRecord.value());
//        // execute
//        receiver.onEvent(incomingRecord);
//        // validate
//        assertEquals(0L, repository.count());
//
//        logInteractions(kafkaTemplate, repository);
//    }
}
