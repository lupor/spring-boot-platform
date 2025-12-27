/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.utils;

import com.fasterxml.jackson.dataformat.csv.CsvParser;

public class CsvDefaultCsvRecordObjectReader extends CsvDefaultObjectReader<CsvDefaultObjectReader.CsvRecord> {

    public static final char CSV_RECORD_DEFAULT_COLUMN_SEPARATOR = ',';
    public static final String CSV_RECORD_DEFAULT_LINE_SEPARATOR = "\n";
    public static final String CSV_RECORD_DEFAULT_QUOTE_CHAR = "\"";
    public static final boolean CSV_RECORD_DEFAULT_STRICT_MODE = false;
    public static final boolean CSV_RECORD_DEFAULT_USE_HEADER = false;

    public CsvDefaultCsvRecordObjectReader() {
        super();
        this.columnSeparator(CSV_RECORD_DEFAULT_COLUMN_SEPARATOR);
        this.lineSeparator(CSV_RECORD_DEFAULT_LINE_SEPARATOR);
        this.strictMode(CSV_RECORD_DEFAULT_STRICT_MODE);
        this.useHeader(CSV_RECORD_DEFAULT_USE_HEADER);
        this.quoteChar(CSV_RECORD_DEFAULT_QUOTE_CHAR);
        this.enableFeature(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE);
        this.enableFeature(CsvParser.Feature.SKIP_EMPTY_LINES);
        this.disableFeature(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS);
        this.schemaClass(CsvRecord.class);
        this.build();
    }
}
