/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.StringUtils;

abstract class CsvDefaultObjectBuilder {

    protected boolean useHeader = true;
    protected char columnSeparator = ',';
    protected String escapeChar = null;
    protected String quoteChar = "\"";
    protected String lineSeparator = "\n";

    public boolean isUseHeader() {
        return useHeader;
    }

    public void setUseHeader(boolean useHeader) {
        this.useHeader = useHeader;
    }

    public char getColumnSeparator() {
        return columnSeparator;
    }

    protected void setColumnSeparator(char columnSeparator) {
        this.columnSeparator = columnSeparator;
    }

    public String getEscapeChar() {
        return escapeChar;
    }

    public void setEscapeChar(String escapeChar) {
        this.escapeChar = escapeChar;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    protected void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    protected List<CsvParser.Feature> getCsvParserEnabledFeatureListForCustomizing() {
        return new ArrayList<>();
    }

    protected List<CsvParser.Feature> getCsvParserDisabledFeatureListForCustomizing() {
        return new ArrayList<>();
    }

    protected List<CsvGenerator.Feature> getCsvGeneratorEnabledFeatureListForCustomizing() {
        return new ArrayList<>();
    }

    protected List<CsvGenerator.Feature> getCsvGeneratorDisabledFeatureListForCustomizing() {
        return new ArrayList<>();
    }

    protected CsvMapper customize(CsvMapper mapper) {
        List<CsvParser.Feature> pfList = getCsvParserEnabledFeatureListForCustomizing();
        if (pfList != null) {
            for (CsvParser.Feature feature : pfList) {
                mapper.enable(feature);
            }
        }
        List<CsvGenerator.Feature> pgList = getCsvGeneratorEnabledFeatureListForCustomizing();
        if (pgList != null) {
            for (CsvGenerator.Feature feature : pgList) {
                mapper.enable(feature);
            }
        }
        pfList = getCsvParserDisabledFeatureListForCustomizing();
        if (pfList != null) {
            for (CsvParser.Feature feature : pfList) {
                mapper.disable(feature);
            }
        }
        pgList = getCsvGeneratorDisabledFeatureListForCustomizing();
        if (pgList != null) {
            for (CsvGenerator.Feature feature : pgList) {
                mapper.disable(feature);
            }
        }
        return mapper;
    }

    protected CsvSchema customize(CsvSchema schema) {
        schema = schema.withUseHeader(useHeader)
                .withColumnSeparator(columnSeparator)
                .withLineSeparator(lineSeparator);
        if (StringUtils.hasLength(quoteChar)) {
            schema = schema.withQuoteChar(quoteChar.toCharArray()[0]);
        } else {
            schema = schema.withoutQuoteChar();
        }
        if (StringUtils.hasLength(escapeChar)) {
            schema = schema.withEscapeChar(escapeChar.toCharArray()[0]);
        } else {
            schema = schema.withoutEscapeChar();
        }
        return schema;
    }
}
