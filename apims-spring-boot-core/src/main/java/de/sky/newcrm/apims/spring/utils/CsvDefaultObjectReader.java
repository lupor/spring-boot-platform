/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import de.sky.newcrm.apims.spring.exceptions.ApimsRuntimeException;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.*;
import org.springframework.util.StringUtils;

@SuppressWarnings({"java:S112", "java:S135", "java:S1172", "java:S3776", "java:S6201", "java:S6212"})
public class CsvDefaultObjectReader<T> extends CsvDefaultObjectBuilder {

    protected Class<T> schemaClass;
    protected Class<?> readerClass;
    protected CsvMapper mapper;
    protected CsvSchema schema;
    protected boolean strictMode = true;
    protected List<CsvParser.Feature> enableFeatures = new ArrayList<>();
    protected List<CsvParser.Feature> disableFeatures = new ArrayList<>();

    public CsvDefaultObjectReader() {
        enableFeature(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, CsvParser.Feature.SKIP_EMPTY_LINES);
    }

    public Class<T> getSchemaClass() {
        return schemaClass;
    }

    public CsvDefaultObjectReader<T> schemaClass(Class<T> schemaClass) {
        this.schemaClass = schemaClass;
        return this;
    }

    public Class<?> getReaderClass() {
        return readerClass;
    }

    public CsvDefaultObjectReader<T> readerClass(Class<?> readerClass) {
        this.readerClass = readerClass;
        return this;
    }

    public CsvDefaultObjectReader<T> strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return this;
    }

    public CsvDefaultObjectReader<T> useHeader(boolean useHeader) {
        super.setUseHeader(useHeader);
        return this;
    }

    public CsvDefaultObjectReader<T> columnSeparator(char columnSeparator) {
        super.setColumnSeparator(columnSeparator);
        return this;
    }

    public CsvDefaultObjectReader<T> lineSeparator(String lineSeparator) {
        super.setLineSeparator(lineSeparator);
        return this;
    }

    public CsvDefaultObjectReader<T> escapeChar(String escapeChar) {
        super.setEscapeChar(escapeChar);
        return this;
    }

    public CsvDefaultObjectReader<T> quoteChar(String quoteChar) {
        super.setQuoteChar(quoteChar);
        return this;
    }

    public CsvDefaultObjectReader<T> enableFeature(CsvParser.Feature... features) {
        getEnableFeatures().addAll(Arrays.asList(features));
        return this;
    }

    public CsvDefaultObjectReader<T> disableFeature(CsvParser.Feature... features) {
        getDisableFeatures().addAll(Arrays.asList(features));
        return this;
    }

    public List<CsvParser.Feature> getEnableFeatures() {
        return enableFeatures;
    }

    public void setEnableFeatures(List<CsvParser.Feature> enableFeatures) {
        this.enableFeatures = enableFeatures;
    }

    public List<CsvParser.Feature> getDisableFeatures() {
        return disableFeatures;
    }

    public void setDisableFeatures(List<CsvParser.Feature> disableFeatures) {
        this.disableFeatures = disableFeatures;
    }

    @Override
    protected List<CsvParser.Feature> getCsvParserEnabledFeatureListForCustomizing() {
        return enableFeatures;
    }

    @Override
    protected List<CsvParser.Feature> getCsvParserDisabledFeatureListForCustomizing() {
        return disableFeatures;
    }

    public CsvMapper getMapper() {
        return mapper;
    }

    public CsvDefaultObjectReader<T> mapper(CsvMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    public CsvSchema getSchema() {
        return schema;
    }

    public CsvDefaultObjectReader<T> schema(CsvSchema schema) {
        this.schema = schema;
        return this;
    }

    protected void initMapperAndSchema() {
        if (!useHeader && !strictMode) {
            strictMode = true;
        }
        if (mapper == null) {
            mapper = new CsvMapper();
            if (readerClass != null) {
                mapper.readerFor(readerClass);
            } else if (strictMode) {
                mapper.readerFor(schemaClass);
            }
        }
        mapper = customize(mapper);
        if (schema == null) {
            schema = strictMode ? mapper.schemaFor(schemaClass) : CsvSchema.emptySchema();
        }
        schema = customize(schema);
    }

    public long readValues(File source, IteratorCallback<T> callback) throws IOException {
        try {
            return doReadValues(source, callback);
        } catch (CharConversionException _) {
            // no UTF-8 file: second try with ISO_8859_1 (CP1257)
            return readValues(source, StandardCharsets.ISO_8859_1, callback);
        }
    }

    public long readValues(File source, String charsetName, IteratorCallback<T> callback) throws IOException {
        return readValues(
                source,
                StringUtils.hasLength(charsetName) ? Charset.forName(charsetName) : StandardCharsets.UTF_8,
                callback);
    }

    public long readValues(File source, Charset charset, IteratorCallback<T> callback) throws IOException {
        try (InputStream inputStream = new FileInputStream(source)) {
            return doReadValues(
                    new InputStreamReader(inputStream, charset == null ? StandardCharsets.UTF_8 : charset), callback);
        }
    }

    public long readValues(byte[] source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(String source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(InputStream source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(Reader source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(URL source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(DataInput source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    public long readValues(JsonParser source, IteratorCallback<T> callback) throws IOException {
        return doReadValues(source, callback);
    }

    protected long doReadValues(Object source, IteratorCallback<T> callback) throws IOException {
        AssertUtils.notNullCheck("source", source);
        AssertUtils.notNullCheck("callback", callback);
        initMapperAndSchema();
        ObjectReader objectReader = mapper.readerWithTypedSchemaFor(strictMode ? schemaClass : Map.class)
                .with(schema);
        MappingIterator<?> iterator = null;
        try {
            if (source instanceof File file) {
                iterator = objectReader.readValues(file);
            } else if (source instanceof byte[]) {
                iterator = objectReader.readValues((byte[]) source);
            } else if (source instanceof String) {
                iterator = objectReader.readValues((String) source);
            } else if (source instanceof InputStream) {
                iterator = objectReader.readValues((InputStream) source);
            } else if (source instanceof Reader) {
                iterator = objectReader.readValues((Reader) source);
            } else if (source instanceof DataInput) {
                iterator = objectReader.readValues((DataInput) source);
            } else if (source instanceof JsonParser) {
                iterator = objectReader.readValues((JsonParser) source);
            } else {
                throw new IllegalArgumentException(
                        "[Assertion failed] - source type '" + source.getClass()
                                + "' is not valid; expected: File, byte[], String, InputStream, Reader, URL, DataInput, JsonParser.");
            }
            return doReadValues(iterator, callback);
        } finally {
            if (iterator != null) {
                try {
                    iterator.close();
                } catch (IOException _) {
                    // ignore
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected long doReadValues(MappingIterator<?> iterator, IteratorCallback<T> callback) throws IOException {
        long count = 0;
        int maxSize = callback.getMaxSize();
        if (maxSize < 1) {
            maxSize = IteratorCallback.DEFAULT_MAX_SIZE;
        } else if (maxSize > IteratorCallback.MAXIMUM_MAX_SIZE) {
            maxSize = IteratorCallback.MAXIMUM_MAX_SIZE;
        }

        List<T> values = new ArrayList<>(maxSize);
        while (iterator.hasNext()) {
            count++;
            T value;
            try {
                if (strictMode) {
                    value = (T) iterator.nextValue();
                } else {
                    Map<String, Object> map = (Map<String, Object>) iterator.nextValue();
                    value = mapper.convertValue(map, schemaClass);
                }
            } catch (JsonMappingException e) {
                if (!callback.handleMappingException(e)) {
                    return count;
                }
                continue;
            } catch (IOException e) {
                if (e instanceof CharConversionException charConversionException) {
                    throw charConversionException;
                }
                if (!callback.handleIOException(e)) {
                    return count;
                }
                continue;
            }
            value = callback.customize(value);
            if (value != null) {
                values.add(value);
                if (values.size() == maxSize) {
                    if (!callback.apply(values)) {
                        return count;
                    }
                    values = new ArrayList<>(maxSize);
                }
            }
        }
        if (!values.isEmpty()) {
            callback.apply(values);
        }
        callback.onFileReaded();
        return count;
    }

    public CsvDefaultObjectReader<T> build() {
        initMapperAndSchema();
        return this;
    }

    @Getter
    public static class IteratorCallback<T> {

        public static final int MAXIMUM_MAX_SIZE = 5000;
        public static final int DEFAULT_MAX_SIZE = 64;
        private final int maxSize;

        protected IteratorCallback() {
            this(DEFAULT_MAX_SIZE);
        }

        protected IteratorCallback(int maxSize) {
            this.maxSize = maxSize;
        }

        public boolean apply(List<T> values) {
            return true;
        }

        @SuppressWarnings("java:S1186")
        public void onFileReaded() {}

        public T customize(T value) {
            return value;
        }

        public boolean handleMappingException(JsonMappingException e) {
            throw new RuntimeJsonMappingException(e.getMessage(), e);
        }

        public boolean handleIOException(IOException e) {
            throw new ApimsRuntimeException(e.getMessage(), e);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @JsonPropertyOrder(alphabetic = true)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CsvRecord {

        private String col01;
        private String col02;
        private String col03;
        private String col04;
        private String col05;
        private String col06;
        private String col07;
        private String col08;
        private String col09;
        private String col10;
        private String col11;
        private String col12;
        private String col13;
        private String col14;
        private String col15;
        private String col16;
        private String col17;
        private String col18;
        private String col19;
        private String col20;
        private String col21;
        private String col22;
        private String col23;
        private String col24;
        private String col25;
        private String col26;
        private String col27;
        private String col28;
        private String col29;
        private String col30;
        private String col31;
        private String col32;
        private String col33;
        private String col34;
        private String col35;
        private String col36;
        private String col37;
        private String col38;
        private String col39;
        private String col40;
        private String col41;
        private String col42;
        private String col43;
        private String col44;
        private String col45;
        private String col46;
        private String col47;
        private String col48;
        private String col49;
        private String col50;

        public String getColumn(int index) {
            return switch (index) {
                case 1 -> col01;
                case 2 -> col02;
                case 3 -> col03;
                case 4 -> col04;
                case 5 -> col05;
                case 6 -> col06;
                case 7 -> col07;
                case 8 -> col08;
                case 9 -> col09;
                case 10 -> col10;
                case 11 -> col11;
                case 12 -> col12;
                case 13 -> col13;
                case 14 -> col14;
                case 15 -> col15;
                case 16 -> col16;
                case 17 -> col17;
                case 18 -> col18;
                case 19 -> col19;
                case 20 -> col20;
                case 21 -> col21;
                case 22 -> col22;
                case 23 -> col23;
                case 24 -> col24;
                case 25 -> col25;
                case 26 -> col26;
                case 27 -> col27;
                case 28 -> col28;
                case 29 -> col29;
                case 30 -> col30;
                case 31 -> col31;
                case 32 -> col32;
                case 33 -> col33;
                case 34 -> col34;
                case 35 -> col35;
                case 36 -> col36;
                case 37 -> col37;
                case 38 -> col38;
                case 39 -> col39;
                case 40 -> col40;
                case 41 -> col41;
                case 42 -> col42;
                case 43 -> col43;
                case 44 -> col44;
                case 45 -> col45;
                case 46 -> col46;
                case 47 -> col47;
                case 48 -> col48;
                case 49 -> col49;
                case 50 -> col50;
                default -> null;
            };
        }
    }
}
