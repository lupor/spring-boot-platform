/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.serialization.core.module;

import static org.junit.jupiter.api.Assertions.*;

import de.sky.newcrm.apims.spring.serialization.core.mapper.ObjectMapperUtils;
import de.sky.newcrm.apims.spring.utils.DateTimeUtc;
import de.sky.newcrm.apims.spring.utils.ObjectUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.*;
import org.junit.jupiter.api.Test;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings("java:S5961")
class ApimsDateTimeUtcModuleTest {

    @Test
    @SneakyThrows
    void moduleTest() {

        Date date = new Date();
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        TestClass testClass = TestClass.builder()
                .date1(date)
                .date2(date)
                .date3(offsetDateTime)
                .date4(offsetDateTime)
                .date5(localDate)
                .birthdate1(date)
                .birthdate2(date)
                .birthdate3(localDate)
                .birthdate4(localDate)
                .birthdate5(offsetDateTime)
                .time1(date)
                .time2(date)
                .time3(localTime)
                .calendar(calendar)
                .xmlCalendar(xmlCalendar)
                .build();
        Map<String, Object> data = ObjectMapperUtils.getValueAsMap(testClass);
        String dataValue = ObjectMapperUtils.writeValueAsString(data);
        Map<String, Object> data2 = ObjectMapperUtils.readMap(dataValue);
        assertTrue(ObjectUtils.isEquals(data, data2, true));
        TestClass testClass2 = ObjectMapperUtils.readValue(dataValue, TestClass.class);
        assertEquals(testClass.getDate1().toString(), testClass2.getDate1().toString());
        assertEquals(testClass.getDate2().toString(), testClass2.getDate2().toString());
        assertEquals(
                DateTimeUtc.format(testClass.getDate3(), DateTimeUtc.ISO.DATE_TIME_COMPLETE),
                DateTimeUtc.format(testClass2.getDate3(), DateTimeUtc.ISO.DATE_TIME_COMPLETE));
        assertEquals(DateTimeUtc.format(testClass.getDate4()), DateTimeUtc.format(testClass2.getDate4()));
        assertEquals(testClass.getDate5().toString(), testClass2.getDate5().toString());
        assertEquals(
                testClass.getBirthdate1().toString(), testClass2.getBirthdate1().toString());
        assertNotEquals(
                testClass.getBirthdate2().toString(), testClass2.getBirthdate2().toString());
        assertEquals(
                testClass.getBirthdate2().toString().substring(0, 11),
                testClass2.getBirthdate2().toString().substring(0, 11));
        assertEquals("00:00:00Z", DateTimeUtc.format(testClass2.getBirthdate2(), DateTimeUtc.ISO.TIME));
        assertEquals(
                testClass.getBirthdate3().toString(), testClass2.getBirthdate3().toString());
        assertEquals(
                testClass.getBirthdate4().toString(), testClass2.getBirthdate4().toString());
        assertTrue(testClass2
                .getBirthdate5()
                .toString()
                .startsWith(DateTimeUtc.format(testClass.getBirthdate5(), DateTimeUtc.ISO.DATE)));
        assertTrue(testClass2.getBirthdate5().toString().endsWith("T00:00Z"));
        assertEquals(testClass.getTime1().toString(), testClass2.getTime1().toString());
        assertEquals(
                testClass.getCalendar().getTime().toString(),
                testClass2.getCalendar().getTime().toString());
        assertEquals(
                testClass.getXmlCalendar().toGregorianCalendar().getTime().toString(),
                testClass2.getXmlCalendar().toGregorianCalendar().getTime().toString());

        data.put("date1", date.getTime());
        data.put("date2", "");
        data.put("date3", "");
        data.put("date4", "");
        data.put("date5", "");
        data.put("birthdate1", "");
        data.put("birthdate2", "");
        data.put("birthdate3", "");
        data.put("birthdate4", "");
        data.put("birthdate5", "");
        data.put("time1", "");
        data.put("time2", "");
        data.put("time3", "");
        data.put("calendar", "");
        data.put("xmlCalendar", "");
        dataValue = ObjectMapperUtils.writeValueAsString(data);
        testClass2 = ObjectMapperUtils.readValue(dataValue, TestClass.class);
        assertEquals(testClass.getDate1().toString(), testClass2.getDate1().toString());
        assertNull(testClass2.getDate2());
        assertNull(testClass2.getDate3());
        assertNull(testClass2.getDate4());
        assertNull(testClass2.getDate5());
        assertNull(testClass2.getBirthdate1());
        assertNull(testClass2.getBirthdate2());
        assertNull(testClass2.getBirthdate3());
        assertNull(testClass2.getBirthdate4());
        assertNull(testClass2.getBirthdate5());
        assertNull(testClass2.getTime1());
        assertNull(testClass2.getTime2());
        assertNull(testClass2.getTime3());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @EqualsAndHashCode
    static class TestClass {
        private Date nullDate;
        private OffsetDateTime nullOffsetDateTime;
        private LocalDate nullLocalDate;
        private Date date1;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private Date date2;

        private OffsetDateTime date3;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private OffsetDateTime date4;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDate date5;

        private Date birthdate1;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private Date birthdate2;

        private LocalDate birthdate3;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate birthdate4;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private OffsetDateTime birthdate5;

        private Date time1;

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
        private Date time2;

        private LocalTime time3;

        private Calendar calendar;

        private XMLGregorianCalendar xmlCalendar;
    }
}
