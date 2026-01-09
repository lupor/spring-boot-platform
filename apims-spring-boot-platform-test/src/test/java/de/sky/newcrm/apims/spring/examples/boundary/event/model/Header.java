/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.examples.boundary.event.model;

import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.SpecificData;

import java.io.Serial;

@org.apache.avro.specific.AvroGenerated
public class Header extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    @Serial
    private static final long serialVersionUID = -1042969592353530349L;

    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse(
                    "{\"type\":\"record\",\"name\":\"Header\",\"namespace\":\"de.sky.newcrm.apims.spring.examples.boundary.event.model\",\"fields\":[{\"name\":\"correlationId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"originator\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<Header> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<Header> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<Header> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<Header> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<Header> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this Header to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a Header from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a Header instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static Header fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private String correlationId;
    private String originator;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public Header() {}

    /**
     * All-args constructor.
     * @param correlationId The new value for correlationId
     * @param originator The new value for originator
     */
    public Header(String correlationId, String originator) {
        this.correlationId = correlationId;
        this.originator = originator;
    }

    @Override
    public SpecificData getSpecificData() {
        return MODEL$;
    }

    @Override
    public org.apache.avro.Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return correlationId;
            case 1:
                return originator;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    // Used by DatumReader.  Applications should not call.
    @Override
    @SuppressWarnings(value = "unchecked")
    public void put(int field$, Object value$) {
        switch (field$) {
            case 0:
                correlationId = value$ != null ? value$.toString() : null;
                break;
            case 1:
                originator = value$ != null ? value$.toString() : null;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'correlationId' field.
     * @return The value of the 'correlationId' field.
     */
    public String getCorrelationId() {
        return correlationId;
    }

    /**
     * Sets the value of the 'correlationId' field.
     * @param value the value to set.
     */
    public void setCorrelationId(String value) {
        this.correlationId = value;
    }

    /**
     * Gets the value of the 'originator' field.
     * @return The value of the 'originator' field.
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * Sets the value of the 'originator' field.
     * @param value the value to set.
     */
    public void setOriginator(String value) {
        this.originator = value;
    }

    /**
     * Creates a new Header RecordBuilder.
     * @return A new Header RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder newBuilder() {
        return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder();
    }

    /**
     * Creates a new Header RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new Header RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder newBuilder(
            de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder other) {
        if (other == null) {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder();
        } else {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder(other);
        }
    }

    /**
     * Creates a new Header RecordBuilder by copying an existing Header instance.
     * @param other The existing instance to copy.
     * @return A new Header RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder newBuilder(
            de.sky.newcrm.apims.spring.examples.boundary.event.model.Header other) {
        if (other == null) {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder();
        } else {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder(other);
        }
    }

    /**
     * RecordBuilder for Header instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Header>
            implements org.apache.avro.data.RecordBuilder<Header> {

        private String correlationId;
        private String originator;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.correlationId)) {
                this.correlationId = data().deepCopy(fields()[0].schema(), other.correlationId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.originator)) {
                this.originator = data().deepCopy(fields()[1].schema(), other.originator);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
        }

        /**
         * Creates a Builder by copying an existing Header instance
         * @param other The existing instance to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.Header other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.correlationId)) {
                this.correlationId = data().deepCopy(fields()[0].schema(), other.correlationId);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.originator)) {
                this.originator = data().deepCopy(fields()[1].schema(), other.originator);
                fieldSetFlags()[1] = true;
            }
        }

        /**
         * Gets the value of the 'correlationId' field.
         * @return The value.
         */
        public String getCorrelationId() {
            return correlationId;
        }

        /**
         * Sets the value of the 'correlationId' field.
         * @param value The value of 'correlationId'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder setCorrelationId(String value) {
            validate(fields()[0], value);
            this.correlationId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'correlationId' field has been set.
         * @return True if the 'correlationId' field has been set, false otherwise.
         */
        public boolean hasCorrelationId() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'correlationId' field.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder clearCorrelationId() {
            correlationId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'originator' field.
         * @return The value.
         */
        public String getOriginator() {
            return originator;
        }

        /**
         * Sets the value of the 'originator' field.
         * @param value The value of 'originator'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder setOriginator(String value) {
            validate(fields()[1], value);
            this.originator = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'originator' field has been set.
         * @return True if the 'originator' field has been set, false otherwise.
         */
        public boolean hasOriginator() {
            return fieldSetFlags()[1];
        }

        /**
         * Clears the value of the 'originator' field.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Header.Builder clearOriginator() {
            originator = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Header build() {
            try {
                Header record = new Header();
                record.correlationId =
                        fieldSetFlags()[0] ? this.correlationId : (String) defaultValue(fields()[0]);
                record.originator = fieldSetFlags()[1] ? this.originator : (String) defaultValue(fields()[1]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<Header> WRITER$ =
            (org.apache.avro.io.DatumWriter<Header>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<Header> READER$ =
            (org.apache.avro.io.DatumReader<Header>) MODEL$.createDatumReader(SCHEMA$);

    @Override
    public void readExternal(java.io.ObjectInput in) throws java.io.IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    @Override
    protected boolean hasCustomCoders() {
        return true;
    }

    @Override
    public void customEncode(org.apache.avro.io.Encoder out) throws java.io.IOException {
        if (this.correlationId == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeString(this.correlationId);
        }

        if (this.originator == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeString(this.originator);
        }
    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            if (in.readIndex() != 1) {
                in.readNull();
                this.correlationId = null;
            } else {
                this.correlationId = in.readString();
            }

            if (in.readIndex() != 1) {
                in.readNull();
                this.originator = null;
            } else {
                this.originator = in.readString();
            }

        } else {
            for (int i = 0; i < 2; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.correlationId = null;
                        } else {
                            this.correlationId = in.readString();
                        }
                        break;

                    case 1:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.originator = null;
                        } else {
                            this.originator = in.readString();
                        }
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
