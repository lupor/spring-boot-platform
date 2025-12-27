/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvDefaultObjectWriter<T> extends CsvDefaultObjectBuilder {

    protected Class<T> schemaClass;
    protected Class<?> writerClass;
    protected CsvMapper mapper;
    protected CsvSchema schema;
    protected ObjectWriter targetWriter;
    protected List<CsvGenerator.Feature> enableFeatures = new ArrayList<>();
    protected List<CsvGenerator.Feature> disableFeatures = new ArrayList<>();

    public CsvDefaultObjectWriter() {
        enableFeature(
                CsvGenerator.Feature.STRICT_CHECK_FOR_QUOTING,
                CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS,
                CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS);
    }

    public Class<T> getSchemaClass() {
        return schemaClass;
    }

    public CsvDefaultObjectWriter<T> schemaClass(Class<T> schemaClass) {
        this.schemaClass = schemaClass;
        return this;
    }

    public CsvDefaultObjectWriter<T> useHeader(boolean header) {
        super.setUseHeader(header);
        return this;
    }

    public CsvDefaultObjectWriter<T> quoteStrings(boolean quoteStrings) {
        if (quoteStrings) {
            enableFeature(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS);
        } else {
            disableFeature(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS, CsvGenerator.Feature.ALWAYS_QUOTE_EMPTY_STRINGS);
        }
        return this;
    }

    public CsvDefaultObjectWriter<T> columnSeparator(char columnSeparator) {
        super.setColumnSeparator(columnSeparator);
        return this;
    }

    public CsvDefaultObjectWriter<T> lineSeparator(String lineSeparator) {
        super.setLineSeparator(lineSeparator);
        return this;
    }

    public CsvDefaultObjectWriter<T> escapeChar(String escapeChar) {
        super.setEscapeChar(escapeChar);
        return this;
    }

    public CsvDefaultObjectWriter<T> quoteChar(String quoteChar) {
        super.setQuoteChar(quoteChar);
        return this;
    }

    public CsvDefaultObjectWriter<T> enableFeature(CsvGenerator.Feature... features) {
        getDisableFeatures().removeAll(Arrays.asList(features));
        getEnableFeatures().addAll(Arrays.asList(features));
        return this;
    }

    public CsvDefaultObjectWriter<T> disableFeature(CsvGenerator.Feature... features) {
        getEnableFeatures().removeAll(Arrays.asList(features));
        getDisableFeatures().addAll(Arrays.asList(features));
        return this;
    }

    public List<CsvGenerator.Feature> getEnableFeatures() {
        return enableFeatures;
    }

    public void setEnableFeatures(List<CsvGenerator.Feature> enableFeatures) {
        this.enableFeatures = enableFeatures;
    }

    public List<CsvGenerator.Feature> getDisableFeatures() {
        return disableFeatures;
    }

    public void setDisableFeatures(List<CsvGenerator.Feature> disableFeatures) {
        this.disableFeatures = disableFeatures;
    }

    @Override
    protected List<CsvGenerator.Feature> getCsvGeneratorEnabledFeatureListForCustomizing() {
        return enableFeatures;
    }

    @Override
    protected List<CsvGenerator.Feature> getCsvGeneratorDisabledFeatureListForCustomizing() {
        return disableFeatures;
    }

    public CsvMapper getMapper() {
        return mapper;
    }

    public CsvDefaultObjectWriter<T> mapper(CsvMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public CsvSchema getSchema() {
        return schema;
    }

    public CsvDefaultObjectWriter<T> schema(CsvSchema schema) {
        this.schema = schema;
        return this;
    }

    protected void initMapperAndSchema() {
        if (mapper == null) {
            mapper = new CsvMapper();
            if (writerClass != null) {
                mapper.writerFor(writerClass);
            } else {
                mapper.writerFor(schemaClass);
            }
        }
        mapper = customize(mapper);
        if (schema == null) {
            schema = mapper.schemaFor(schemaClass);
        }
        schema = customize(schema);
    }

    public CsvDefaultObjectWriter<T> build() {
        initMapperAndSchema();
        if (targetWriter == null) {
            targetWriter = mapper.writer(schema);
        }
        targetWriter = customize(targetWriter);

        return this;
    }

    protected ObjectWriter customize(ObjectWriter writer) {
        return writer;
    }

    public SequenceWriter getSequenceWriter(File out) throws IOException {
        return targetWriter.writeValues(out);
    }

    public SequenceWriter getSequenceWriter(Writer out) throws IOException {
        return targetWriter.writeValues(out);
    }
}
