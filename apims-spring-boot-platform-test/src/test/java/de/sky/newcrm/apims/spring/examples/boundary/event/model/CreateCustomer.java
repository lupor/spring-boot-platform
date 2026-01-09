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
public class CreateCustomer extends org.apache.avro.specific.SpecificRecordBase
        implements org.apache.avro.specific.SpecificRecord {
    @Serial
    private static final long serialVersionUID = 7407097709821887793L;

    public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser()
            .parse(
                    "{\"type\":\"record\",\"name\":\"CreateCustomer\",\"namespace\":\"de.sky.newcrm.apims.spring.examples.boundary.event.model\",\"fields\":[{\"name\":\"name\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"email\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"general\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"General\",\"fields\":[{\"name\":\"customerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}],\"default\":null}]}");

    public static org.apache.avro.Schema getClassSchema() {
        return SCHEMA$;
    }

    private static final SpecificData MODEL$ = new SpecificData();

    private static final BinaryMessageEncoder<CreateCustomer> ENCODER = new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

    private static final BinaryMessageDecoder<CreateCustomer> DECODER = new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

    /**
     * Return the BinaryMessageEncoder instance used by this class.
     *
     * @return the message encoder used by this class
     */
    public static BinaryMessageEncoder<CreateCustomer> getEncoder() {
        return ENCODER;
    }

    /**
     * Return the BinaryMessageDecoder instance used by this class.
     *
     * @return the message decoder used by this class
     */
    public static BinaryMessageDecoder<CreateCustomer> getDecoder() {
        return DECODER;
    }

    /**
     * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
     *
     * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
     * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
     */
    public static BinaryMessageDecoder<CreateCustomer> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
    }

    /**
     * Serializes this CreateCustomer to a ByteBuffer.
     *
     * @return a buffer holding the serialized data for this instance
     * @throws java.io.IOException if this instance could not be serialized
     */
    public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
        return ENCODER.encode(this);
    }

    /**
     * Deserializes a CreateCustomer from a ByteBuffer.
     *
     * @param b a byte buffer holding serialized data for an instance of this class
     * @return a CreateCustomer instance decoded from the given buffer
     * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
     */
    public static CreateCustomer fromByteBuffer(java.nio.ByteBuffer b) throws java.io.IOException {
        return DECODER.decode(b);
    }

    private String name;
    private String email;
    private General general;

    /**
     * Default constructor.  Note that this does not initialize fields
     * to their default values from the schema.  If that is desired then
     * one should use <code>newBuilder()</code>.
     */
    public CreateCustomer() {}

    /**
     * All-args constructor.
     *
     * @param name    The new value for name
     * @param email   The new value for email
     * @param general The new value for general
     */
    public CreateCustomer(
            String name,
            String email,
            General general) {
        this.name = name;
        this.email = email;
        this.general = general;
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
                return name;
            case 1:
                return email;
            case 2:
                return general;
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
                name = value$ != null ? value$.toString() : null;
                break;
            case 1:
                email = value$ != null ? value$.toString() : null;
                break;
            case 2:
                general = (General) value$;
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid index: " + field$);
        }
    }

    /**
     * Gets the value of the 'name' field.
     *
     * @return The value of the 'name' field.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the 'name' field.
     *
     * @param value the value to set.
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the 'email' field.
     *
     * @return The value of the 'email' field.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the 'email' field.
     *
     * @param value the value to set.
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the 'general' field.
     *
     * @return The value of the 'general' field.
     */
    public General getGeneral() {
        return general;
    }

    /**
     * Sets the value of the 'general' field.
     *
     * @param value the value to set.
     */
    public void setGeneral(General value) {
        this.general = value;
    }

    /**
     * Creates a new CreateCustomer RecordBuilder.
     *
     * @return A new CreateCustomer RecordBuilder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Creates a new CreateCustomer RecordBuilder by copying an existing Builder.
     *
     * @param other The existing builder to copy.
     * @return A new CreateCustomer RecordBuilder
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
     * Creates a new CreateCustomer RecordBuilder by copying an existing CreateCustomer instance.
     *
     * @param other The existing instance to copy.
     * @return A new CreateCustomer RecordBuilder
     */
    public static Builder newBuilder(
            CreateCustomer other) {
        if (other == null) {
            return new Builder();
        } else {
            return new Builder(other);
        }
    }

    /**
     * RecordBuilder for CreateCustomer instances.
     */
    @org.apache.avro.specific.AvroGenerated
    public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<CreateCustomer>
            implements org.apache.avro.data.RecordBuilder<CreateCustomer> {

        private String name;
        private String email;
        private General general;
        private General.Builder generalBuilder;

        /**
         * Creates a new Builder
         */
        private Builder() {
            super(SCHEMA$, MODEL$);
        }

        /**
         * Creates a Builder by copying an existing Builder.
         *
         * @param other The existing Builder to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder other) {
            super(other);
            if (isValidValue(fields()[0], other.name)) {
                this.name = data().deepCopy(fields()[0].schema(), other.name);
                fieldSetFlags()[0] = other.fieldSetFlags()[0];
            }
            if (isValidValue(fields()[1], other.email)) {
                this.email = data().deepCopy(fields()[1].schema(), other.email);
                fieldSetFlags()[1] = other.fieldSetFlags()[1];
            }
            if (isValidValue(fields()[2], other.general)) {
                this.general = data().deepCopy(fields()[2].schema(), other.general);
                fieldSetFlags()[2] = other.fieldSetFlags()[2];
            }
            if (other.hasGeneralBuilder()) {
                this.generalBuilder =
                        de.sky.newcrm.apims.spring.examples.boundary.event.model.General.newBuilder(other.getGeneralBuilder());
            }
        }

        /**
         * Creates a Builder by copying an existing CreateCustomer instance
         *
         * @param other The existing instance to copy.
         */
        private Builder(de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer other) {
            super(SCHEMA$, MODEL$);
            if (isValidValue(fields()[0], other.name)) {
                this.name = data().deepCopy(fields()[0].schema(), other.name);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.email)) {
                this.email = data().deepCopy(fields()[1].schema(), other.email);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.general)) {
                this.general = data().deepCopy(fields()[2].schema(), other.general);
                fieldSetFlags()[2] = true;
            }
            this.generalBuilder = null;
        }

        /**
         * Gets the value of the 'name' field.
         *
         * @return The value.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the value of the 'name' field.
         *
         * @param value The value of 'name'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder setName(String value) {
            validate(fields()[0], value);
            this.name = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'name' field has been set.
         *
         * @return True if the 'name' field has been set, false otherwise.
         */
        public boolean hasName() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'name' field.
         *
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder clearName() {
            name = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'email' field.
         *
         * @return The value.
         */
        public String getEmail() {
            return email;
        }

        /**
         * Sets the value of the 'email' field.
         *
         * @param value The value of 'email'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder setEmail(String value) {
            validate(fields()[1], value);
            this.email = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'email' field has been set.
         *
         * @return True if the 'email' field has been set, false otherwise.
         */
        public boolean hasEmail() {
            return fieldSetFlags()[1];
        }

        /**
         * Clears the value of the 'email' field.
         *
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder clearEmail() {
            email = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'general' field.
         *
         * @return The value.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.General getGeneral() {
            return general;
        }

        /**
         * Sets the value of the 'general' field.
         *
         * @param value The value of 'general'.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder setGeneral(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.General value) {
            validate(fields()[2], value);
            this.generalBuilder = null;
            this.general = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'general' field has been set.
         *
         * @return True if the 'general' field has been set, false otherwise.
         */
        public boolean hasGeneral() {
            return fieldSetFlags()[2];
        }

        /**
         * Gets the Builder instance for the 'general' field and creates one if it doesn't exist yet.
         *
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.General.Builder getGeneralBuilder() {
            if (generalBuilder == null) {
                if (hasGeneral()) {
                    setGeneralBuilder(de.sky.newcrm.apims.spring.examples.boundary.event.model.General.newBuilder(general));
                } else {
                    setGeneralBuilder(de.sky.newcrm.apims.spring.examples.boundary.event.model.General.newBuilder());
                }
            }
            return generalBuilder;
        }

        /**
         * Sets the Builder instance for the 'general' field
         *
         * @param value The builder instance that must be set.
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder setGeneralBuilder(
                de.sky.newcrm.apims.spring.examples.boundary.event.model.General.Builder value) {
            clearGeneral();
            generalBuilder = value;
            return this;
        }

        /**
         * Checks whether the 'general' field has an active Builder instance
         *
         * @return True if the 'general' field has an active Builder instance
         */
        public boolean hasGeneralBuilder() {
            return generalBuilder != null;
        }

        /**
         * Clears the value of the 'general' field.
         *
         * @return This builder.
         */
        public de.sky.newcrm.apims.spring.examples.boundary.event.model.CreateCustomer.Builder clearGeneral() {
            general = null;
            generalBuilder = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        public CreateCustomer build() {
            try {
                CreateCustomer record = new CreateCustomer();
                record.name = fieldSetFlags()[0] ? this.name : (String) defaultValue(fields()[0]);
                record.email = fieldSetFlags()[1] ? this.email : (String) defaultValue(fields()[1]);
                if (generalBuilder != null) {
                    try {
                        record.general = this.generalBuilder.build();
                    } catch (org.apache.avro.AvroMissingFieldException e) {
                        e.addParentField(record.getSchema().getField("general"));
                        throw e;
                    }
                } else {
                    record.general = fieldSetFlags()[2]
                            ? this.general
                            : (de.sky.newcrm.apims.spring.examples.boundary.event.model.General) defaultValue(fields()[2]);
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
    private static final org.apache.avro.io.DatumWriter<CreateCustomer> WRITER$ =
            (org.apache.avro.io.DatumWriter<CreateCustomer>) MODEL$.createDatumWriter(SCHEMA$);

    @Override
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    @SuppressWarnings("unchecked")
    private static final org.apache.avro.io.DatumReader<CreateCustomer> READER$ =
            (org.apache.avro.io.DatumReader<CreateCustomer>) MODEL$.createDatumReader(SCHEMA$);

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
        if (this.name == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            out.writeString(this.name);
        }

        out.writeString(this.email);

        if (this.general == null) {
            out.writeIndex(0);
            out.writeNull();
        } else {
            out.writeIndex(1);
            this.general.customEncode(out);
        }
    }

    @Override
    public void customDecode(org.apache.avro.io.ResolvingDecoder in) throws java.io.IOException {
        org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
        if (fieldOrder == null) {
            if (in.readIndex() != 1) {
                in.readNull();
                this.name = null;
            } else {
                this.name = in.readString();
            }

            this.email = in.readString();

            if (in.readIndex() != 1) {
                in.readNull();
                this.general = null;
            } else {
                if (this.general == null) {
                    this.general = new de.sky.newcrm.apims.spring.examples.boundary.event.model.General();
                }
                this.general.customDecode(in);
            }

        } else {
            for (int i = 0; i < 3; i++) {
                switch (fieldOrder[i].pos()) {
                    case 0:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.name = null;
                        } else {
                            this.name = in.readString();
                        }
                        break;

                    case 1:
                        this.email = in.readString();
                        break;

                    case 2:
                        if (in.readIndex() != 1) {
                            in.readNull();
                            this.general = null;
                        } else {
                            if (this.general == null) {
                                this.general = new de.sky.newcrm.apims.spring.examples.boundary.event.model.General();
                            }
                            this.general.customDecode(in);
                        }
                        break;

                    default:
                        throw new java.io.IOException("Corrupt ResolvingDecoder.");
                }
            }
        }
    }
}
