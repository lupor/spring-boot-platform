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
public class Payload extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    @Serial
    private static final long serialVersionUID = -6979127381324680396L;

    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse(
                    "{\"type\":\"record\",\"name\":\"Payload\",\"namespace\":\"de.sky.newcrm.apims.spring.examples.boundary.event.model\",\"fields\":[{\"name\":\"message\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"createCustomer\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"CreateCustomer\",\"fields\":[{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"email\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"general\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"General\",\"fields\":[{\"name\":\"customerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}],\"default\":null}]}],\"default\":null},{\"name\":\"deleteCustomer\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"DeleteCustomer\",\"fields\":[{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"email\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"general\",\"type\":[\"null\",\"General\"],\"default\":null}]}],\"default\":null}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<Payload> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<Payload> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<Payload> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<Payload> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<Payload> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this Payload to a ByteBuffer.
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a Payload from a ByteBuffer.
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a Payload instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static Payload fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private String message;
    private de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer createCustomer;
    private de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer deleteCustomer;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public Payload() {}

    /**
     * All-args constructor.
     * @param message The new value for message
     * @param createCustomer The new value for createCustomer
     * @param deleteCustomer The new value for deleteCustomer
     */
    public Payload(
            String message,
            de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer createCustomer,
            de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer deleteCustomer) {
        this.message = message;
        this.createCustomer = createCustomer;
        this.deleteCustomer = deleteCustomer;
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
                return message;
            case 1:
                return createCustomer;
            case 2:
                return deleteCustomer;
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
                message = value$ != null ? value$.toString() : null;
                break;
            case 1:
                createCustomer = (de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer) value$;
                break;
            case 2:
                deleteCustomer = (de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer) value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'message' field.
     * @return The value of the 'message' field.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the 'message' field.
     * @param value the value to set.
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the 'createCustomer' field.
     * @return The value of the 'createCustomer' field.
     */
    public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer getCreateCustomer() {
        return createCustomer;
    }

    /**
     * Sets the value of the 'createCustomer' field.
     * @param value the value to set.
     */
    public void setCreateCustomer(de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer value) {
        this.createCustomer = value;
    }

    /**
     * Gets the value of the 'deleteCustomer' field.
     * @return The value of the 'deleteCustomer' field.
     */
    public de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer getDeleteCustomer() {
        return deleteCustomer;
    }

    /**
     * Sets the value of the 'deleteCustomer' field.
     * @param value the value to set.
     */
    public void setDeleteCustomer(de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer value) {
        this.deleteCustomer = value;
    }

    /**
     * Creates a new Payload RecordBuilder.
     * @return A new Payload RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder newBuilder() {
        return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder();
    }

    /**
     * Creates a new Payload RecordBuilder by copying an existing Builder.
     * @param other The existing builder to copy.
     * @return A new Payload RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder newBuilder(
            de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder other) {
        if (other == null) {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder();
        } else {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder(other);
        }
    }

    /**
     * Creates a new Payload RecordBuilder by copying an existing Payload instance.
     * @param other The existing instance to copy.
     * @return A new Payload RecordBuilder
     */
    public static de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder newBuilder(
            de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload other) {
        if (other == null) {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder();
        } else {
            return new de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder(other);
        }
    }

    /**
     * RecordBuilder for Payload instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Payload>
            implements org.apache.avro.data.RecordBuilder<Payload> {

        private String message;
        private de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer createCustomer;
        private de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder createCustomerBuilder;
        private de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer deleteCustomer;
        private de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.Builder deleteCustomerBuilder;

        /** Creates a new Builder */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         * @param other The existing Builder to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.message)) {
                this.message = data().deepCopy(fields()[0].schema(), other.message);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.createCustomer)) {
                this.createCustomer = data().deepCopy(fields()[1].schema(), other.createCustomer);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (other.hasCreateCustomerBuilder()) {
                this.createCustomerBuilder = de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.newBuilder(
                        other.getCreateCustomerBuilder());
            }
            if (isValidValue(fields()[2], other.deleteCustomer)) {
                this.deleteCustomer = data().deepCopy(fields()[2].schema(), other.deleteCustomer);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (other.hasDeleteCustomerBuilder()) {
                this.deleteCustomerBuilder = de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.newBuilder(
                        other.getDeleteCustomerBuilder());
            }
        }

        /**
         * Creates a Builder by copying an existing Payload instance
         * @param other The existing instance to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.message)) {
                this.message = data().deepCopy(fields()[0].schema(), other.message);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.createCustomer)) {
                this.createCustomer = data().deepCopy(fields()[1].schema(), other.createCustomer);
                fieldSetFlags()[1] = true;
            }
            this.createCustomerBuilder = null;
            if (isValidValue(fields()[2], other.deleteCustomer)) {
                this.deleteCustomer = data().deepCopy(fields()[2].schema(), other.deleteCustomer);
                fieldSetFlags()[2] = true;
            }
            this.deleteCustomerBuilder = null;
        }

        /**
         * Gets the value of the 'message' field.
         * @return The value.
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the value of the 'message' field.
         * @param value The value of 'message'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder setMessage(String value) {
            validate(fields()[0], value);
            this.message = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'message' field has been set.
         * @return True if the 'message' field has been set, false otherwise.
         */
        public boolean hasMessage() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'message' field.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder clearMessage() {
            message = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'createCustomer' field.
         * @return The value.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer getCreateCustomer() {
            return createCustomer;
        }

        /**
         * Sets the value of the 'createCustomer' field.
         * @param value The value of 'createCustomer'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder setCreateCustomer(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer value) {
            validate(fields()[1], value);
            this.createCustomerBuilder = null;
            this.createCustomer = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'createCustomer' field has been set.
         * @return True if the 'createCustomer' field has been set, false otherwise.
         */
        public boolean hasCreateCustomer() {
            return fieldSetFlags()[1];
        }

        /**
         * Gets the Builder instance for the 'createCustomer' field and creates one if it doesn't exist yet.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder getCreateCustomerBuilder() {
            if (createCustomerBuilder == null) {
                if (hasCreateCustomer()) {
                    setCreateCustomerBuilder(
                            de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.newBuilder(createCustomer));
                } else {
                    setCreateCustomerBuilder(de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.newBuilder());
                }
            }
            return createCustomerBuilder;
        }

        /**
         * Sets the Builder instance for the 'createCustomer' field
         * @param value The builder instance that must be set.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder setCreateCustomerBuilder(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder value) {
            clearCreateCustomer();
            createCustomerBuilder = value;
            return this;
        }

        /**
         * Checks whether the 'createCustomer' field has an active Builder instance
         * @return True if the 'createCustomer' field has an active Builder instance
         */
        public boolean hasCreateCustomerBuilder() {
            return createCustomerBuilder != null;
        }

        /**
         * Clears the value of the 'createCustomer' field.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder clearCreateCustomer() {
            createCustomer = null;
            createCustomerBuilder = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'deleteCustomer' field.
         * @return The value.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer getDeleteCustomer() {
            return deleteCustomer;
        }

        /**
         * Sets the value of the 'deleteCustomer' field.
         * @param value The value of 'deleteCustomer'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder setDeleteCustomer(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer value) {
            validate(fields()[2], value);
            this.deleteCustomerBuilder = null;
            this.deleteCustomer = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'deleteCustomer' field has been set.
         * @return True if the 'deleteCustomer' field has been set, false otherwise.
         */
        public boolean hasDeleteCustomer() {
            return fieldSetFlags()[2];
        }

        /**
         * Gets the Builder instance for the 'deleteCustomer' field and creates one if it doesn't exist yet.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.Builder getDeleteCustomerBuilder() {
            if (deleteCustomerBuilder == null) {
                if (hasDeleteCustomer()) {
                    setDeleteCustomerBuilder(
                            de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.newBuilder(deleteCustomer));
                } else {
                    setDeleteCustomerBuilder(de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.newBuilder());
                }
            }
            return deleteCustomerBuilder;
        }

        /**
         * Sets the Builder instance for the 'deleteCustomer' field
         * @param value The builder instance that must be set.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder setDeleteCustomerBuilder(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer.Builder value) {
            clearDeleteCustomer();
            deleteCustomerBuilder = value;
            return this;
        }

        /**
         * Checks whether the 'deleteCustomer' field has an active Builder instance
         * @return True if the 'deleteCustomer' field has an active Builder instance
         */
        public boolean hasDeleteCustomerBuilder() {
            return deleteCustomerBuilder != null;
        }

        /**
         * Clears the value of the 'deleteCustomer' field.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.Payload.Builder clearDeleteCustomer() {
            deleteCustomer = null;
            deleteCustomerBuilder = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Payload build() {
            try {
                Payload record = new Payload();
                record.message = fieldSetFlags()[0] ? this.message : (String) defaultValue(fields()[0]);
                if (createCustomerBuilder != null) {
                    try {
                        record.createCustomer = this.createCustomerBuilder.build();
                    } catch (org.apache.avro.AvroMissingFieldException e) {
                        e.addParentField(record.getSchema().getField("createCustomer"));
                        throw e;
                    }
                } else {
                    record.createCustomer = fieldSetFlags()[1]
                            ? this.createCustomer
                            : (de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer) defaultValue(fields()[1]);
                }
                if (deleteCustomerBuilder != null) {
                    try {
                        record.deleteCustomer = this.deleteCustomerBuilder.build();
                    } catch (org.apache.avro.AvroMissingFieldException e) {
                        e.addParentField(record.getSchema().getField("deleteCustomer"));
                        throw e;
                    }
                } else {
                    record.deleteCustomer = fieldSetFlags()[2]
                            ? this.deleteCustomer
                            : (de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer) defaultValue(fields()[2]);
                }
                return record;
            } catch (org.apache.avro.AvroMissingFieldException e) {
                throw e;
            } catch (Exception e) {
                throw new org.apache.avro.AvroRuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumWriter<Payload> WRITER$ =
            (org.apache.avro.io.DatumWriter<Payload>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<Payload> READER$ =
            (org.apache.avro.io.DatumReader<Payload>) MODEL$.createDatumReader(SCHEMA$);

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
        out.writeString(this.message);

        if (this.createCustomer == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            this.createCustomer.customEncode(out);
        }

        if (this.deleteCustomer == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            this.deleteCustomer.customEncode(out);
        }
    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            this.message = in.readString();

            if (in.readIndex() != 1) {
                in.readNull();
                this.createCustomer = null;
            } else {
                if (this.createCustomer == null) {
                    this.createCustomer = new de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer();
                }
                this.createCustomer.customDecode(in);
            }

            if (in.readIndex() != 1) {
                in.readNull();
                this.deleteCustomer = null;
            } else {
                if (this.deleteCustomer == null) {
                    this.deleteCustomer = new de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer();
                }
                this.deleteCustomer.customDecode(in);
            }

        } else {
            for (int i = 0; i < 3; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        this.message = in.readString();
                        break;

                    case 1:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.createCustomer = null;
                        } else {
                            if (this.createCustomer == null) {
                                this.createCustomer = new de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer();
                            }
                            this.createCustomer.customDecode(in);
                        }
                        break;

                    case 2:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.deleteCustomer = null;
                        } else {
                            if (this.deleteCustomer == null) {
                                this.deleteCustomer = new de.sky.newcrm.apims.spring.examples.boundary.event.model.DeleteCustomer();
                            }
                            this.deleteCustomer.customDecode(in);
                        }
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
