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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RequestValidationFailedAllOfDetails
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@JsonTypeName("RequestValidationFailed_allOf_details")
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2023-03-15T21:42:04.788506414+01:00[Europe/Berlin]")
public class RequestValidationFailedAllOfDetails {

    @JsonProperty("errors")
    @Valid
    private List<RequestValidationFailedAllOfDetailsErrors> errors = null;

    public RequestValidationFailedAllOfDetails errors(List<RequestValidationFailedAllOfDetailsErrors> errors) {
        this.errors = errors;
        return this;
    }

    public RequestValidationFailedAllOfDetails addErrorsItem(RequestValidationFailedAllOfDetailsErrors errorsItem) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(errorsItem);
        return this;
    }

    /**
     * Get errors
     * @return errors
     */
    @Valid
    @Schema(name = "errors", required = false)
    public List<RequestValidationFailedAllOfDetailsErrors> getErrors() {
        return errors;
    }

    public void setErrors(List<RequestValidationFailedAllOfDetailsErrors> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestValidationFailedAllOfDetails requestValidationFailedAllOfDetails =
                (RequestValidationFailedAllOfDetails) o;
        return Objects.equals(this.errors, requestValidationFailedAllOfDetails.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RequestValidationFailedAllOfDetails {\n");
        sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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
