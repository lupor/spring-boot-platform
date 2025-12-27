/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvDefaultSapObjectReader extends CsvDefaultObjectReader<CsvDefaultObjectReader.CsvRecord> {

    public static final char CSV_SAP_DEFAULT_COLUMN_SEPARATOR = '|';
    public static final String CSV_SAP_DEFAULT_LINE_SEPARATOR = "\n";
    public static final boolean CSV_SAP_DEFAULT_STRICT_MODE = false;
    public static final boolean CSV_SAP_DEFAULT_USE_HEADER = false;
    public static final int CSV_SAP_DEFAULT_BATCH_SIZE = 1000;

    private final int batchSize;

    public CsvDefaultSapObjectReader() {
        this(CSV_SAP_DEFAULT_BATCH_SIZE);
    }

    public CsvDefaultSapObjectReader(int batchSize) {
        super();
        this.batchSize = batchSize;
        this.columnSeparator(CSV_SAP_DEFAULT_COLUMN_SEPARATOR);
        this.lineSeparator(CSV_SAP_DEFAULT_LINE_SEPARATOR);
        this.strictMode(CSV_SAP_DEFAULT_STRICT_MODE);
        this.useHeader(CSV_SAP_DEFAULT_USE_HEADER);
        this.enableFeature(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        this.enableFeature(CsvParser.Feature.SKIP_EMPTY_LINES);
        this.disableFeature(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS);
        this.schemaClass(CsvRecord.class);
        this.build();
    }

    public long readValues(File source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(File source, String charsetName, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, charsetName, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(File source, Charset charset, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, charset, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(byte[] source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(String source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(InputStream source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(Reader source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(URL source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    public long readValues(DataInput source, Consumer<List<CsvSapEntity>> consumer) throws IOException {
        return readValues(source, createSapCsvIteratorCallback(consumer));
    }

    @SuppressWarnings("java:S3776")
    protected CsvDefaultObjectReader.IteratorCallback<CsvRecord> createSapCsvIteratorCallback(
            Consumer<List<CsvSapEntity>> consumer) {
        return new CsvDefaultObjectReader.IteratorCallback<>(batchSize) {

            private CsvSapEntity currentEntity = null;

            @Override
            public boolean apply(List<CsvRecord> values) {
                List<CsvSapEntity> entityList = new ArrayList<>(batchSize);
                for (CsvRecord value : values) {
                    String id = value.getCol01();
                    if ("01".equals(id)) {
                        if (currentEntity != null) {
                            entityList.add(currentEntity);
                        }
                        currentEntity = new CsvSapEntity();
                    }
                    if (currentEntity.getRecords() == null) {
                        currentEntity.setRecords(new ArrayList<>());
                    }
                    currentEntity.getRecords().add(value);
                }
                if (!entityList.isEmpty()) {
                    consumer.accept(entityList);
                }
                return true;
            }

            @Override
            public void onFileReaded() {
                if (currentEntity != null) {
                    List<CsvSapEntity> entityList = new ArrayList<>();
                    entityList.add(currentEntity);
                    consumer.accept(entityList);
                    currentEntity = null;
                }
            }
        };
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @JsonPropertyOrder({"records"})
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CsvSapEntity {

        @JsonProperty("records")
        private List<CsvRecord> records;
    }
}
