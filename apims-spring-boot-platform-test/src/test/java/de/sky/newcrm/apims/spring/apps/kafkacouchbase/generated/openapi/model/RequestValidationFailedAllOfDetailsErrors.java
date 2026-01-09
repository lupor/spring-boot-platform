/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;

import java.util.Objects;

/**
 * RequestValidationFailedAllOfDetailsErrors
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonTypeName("RequestValidationFailed_allOf_details_errors")
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2023-03-15T21:42:04.788506414+01:00[Europe/Berlin]")
public class RequestValidationFailedAllOfDetailsErrors {

    @JsonProperty("field")
    private String field;

    @JsonProperty("message")
    private String message;

    public RequestValidationFailedAllOfDetailsErrors field(String field) {
        this.field = field;
        return this;
    }

    /**
     * Get field
     * @return field
     */
    @Schema(name = "field", required = false)
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public RequestValidationFailedAllOfDetailsErrors message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Get message
     * @return message
     */
    @Schema(name = "message", required = false)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestValidationFailedAllOfDetailsErrors requestValidationFailedAllOfDetailsErrors =
                (RequestValidationFailedAllOfDetailsErrors) o;
        return Objects.equals(this.field, requestValidationFailedAllOfDetailsErrors.field)
                && Objects.equals(this.message, requestValidationFailedAllOfDetailsErrors.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, message);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestValidationFailedAllOfDetailsErrors {\n");
        sb.append("    field: ").append(toIndentedString(field)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
