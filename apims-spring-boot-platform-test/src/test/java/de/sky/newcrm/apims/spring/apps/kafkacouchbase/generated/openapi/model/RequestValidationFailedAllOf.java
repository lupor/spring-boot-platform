/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;

import java.util.Objects;

/**
 * RequestValidationFailedAllOf
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonTypeName("RequestValidationFailed_allOf")
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2023-03-15T21:42:04.788506414+01:00[Europe/Berlin]")
public class RequestValidationFailedAllOf {

    @JsonProperty("details")
    private RequestValidationFailedAllOfDetails details;

    public RequestValidationFailedAllOf details(RequestValidationFailedAllOfDetails details) {
        this.details = details;
        return this;
    }

    /**
     * Get details
     * @return details
     */
    @Valid
    @Schema(name = "details", required = false)
    public RequestValidationFailedAllOfDetails getDetails() {
        return details;
    }

    public void setDetails(RequestValidationFailedAllOfDetails details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestValidationFailedAllOf requestValidationFailedAllOf = (RequestValidationFailedAllOf) o;
        return Objects.equals(this.details, requestValidationFailedAllOf.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestValidationFailedAllOf {\n");
        sb.append("    details: ").append(toIndentedString(details)).append("\n");
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
