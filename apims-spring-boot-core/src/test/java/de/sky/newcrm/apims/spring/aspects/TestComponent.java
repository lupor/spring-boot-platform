///*
// * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
// * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
// */
//package de.sky.newcrm.apims.spring.aspects;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TestComponent {
//    public static final String OUTPUT_MESSAGE_PREFIX = "RETURN ";
//    private List<String> inputMessages = new ArrayList<>();
//    private List<String> outputMessages = new ArrayList<>();
//
//    public List<String> getInputMessages() {
//        return inputMessages;
//    }
//
//    public List<String> getOutputMessages() {
//        return outputMessages;
//    }
//
//    @ApimsAroundNewTraceId
//    public String testMethod(String inputMessage) {
//        noTrackingTest(inputMessage);
//        String outputMessage = OUTPUT_MESSAGE_PREFIX + inputMessage;
//        outputMessages.add(outputMessage);
//        return outputMessage;
//    }
//
//    public void testVoidMethod(String inputMessage) {
//        noTrackingTest(inputMessage);
//    }
//
//    protected void noTrackingTest(String inputMessage) {
//        inputMessages.add(inputMessage);
//    }
//}
