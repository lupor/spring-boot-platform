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
public class General extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    @Serial
    private static final long serialVersionUID = -286271079004149739L;

    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse(
                    "{\"type\":\"record\",\"name\":\"General\",\"namespace\":\"de.sky.newcrm.spring.examples.boundary.event.model\",\"fields\":[{\"name\":\"customerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<General> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<General> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<General> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<General> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<General> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this General to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a General from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a General instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static General fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private String customerId;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public General() {}

    /**
     * All-args constructor.
     * @param customerId The new value for customerId
     */
    public General(String customerId) {
        this.customerId = customerId;
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
                return customerId;
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
                customerId = value$ != null ? value$.toString() : null;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'customerId' field.
     * @return The value of the 'customerId' field.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the 'customerId' field.
     * @param value the value to set.
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Creates a new General RecordBuilder.
     * @return A new General RecordBuilder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Creates a new General RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new General RecordBuilder
     */
    public static Builder newBuilder(
            Builder other) {
        if (other == null) {
            return new Builder();
        } else {
            return new Builder(other);
        }
    }

    /**
     * Creates a new General RecordBuilder by copying an existing General instance.
     * @param other The existing instance to copy.
     * @return A new General RecordBuilder
     */
    public static Builder newBuilder(
            General other) {
        if (other == null) {
            return new Builder();
        } else {
            return new Builder(other);
        }
    }

    /**
     * RecordBuilder for General instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<General>
            implements org.apache.avro.data.RecordBuilder<General> {

        private String customerId;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.customerId)) {
                this.customerId = data().deepCopy(fields()[0].schema(), other.customerId);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
        }

        /**
         * Creates a Builder by copying an existing General instance
         * @param other The existing instance to copy.
         */
        private Builder(General other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.customerId)) {
                this.customerId = data().deepCopy(fields()[0].schema(), other.customerId);
                fieldSetFlags()[0] = true;
            }
        }

        /**
         * Gets the value of the 'customerId' field.
         * @return The value.
         */
        public String getCustomerId() {
            return customerId;
        }

        /**
         * Sets the value of the 'customerId' field.
         * @param value The value of 'customerId'.
         * @return This builder.
         */
        public Builder setCustomerId(String value) {
            validate(fields()[0], value);
            this.customerId = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'customerId' field has been set.
         * @return True if the 'customerId' field has been set, false otherwise.
         */
        public boolean hasCustomerId() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'customerId' field.
         * @return This builder.
         */
        public Builder clearCustomerId() {
            customerId = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public General build() {
            try {
                General record = new General();
                record.customerId = fieldSetFlags()[0] ? this.customerId : (String) defaultValue(fields()[0]);
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<General> WRITER$ =
            (org.apache.avro.io.DatumWriter<General>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<General> READER$ =
            (org.apache.avro.io.DatumReader<General>) MODEL$.createDatumReader(SCHEMA$);

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
        out.writeString(this.customerId);
    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.customerId = in.readString();

        } else {
            for (int i = 0; i < 1; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        this.customerId = in.readString();
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
