/*
 * Copyright (C) 2023 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * The response if a error occured
 */
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
@Schema(name = "ErrorResponse", description = "The response if a error occured")
@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2023-03-15T21:42:04.788506414+01:00[Europe/Berlin]")
public class ErrorResponse {

    @JsonProperty("timestamp")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private OffsetDateTime timestamp;

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("path")
    private String path;

    @JsonProperty("code")
    private String code;

    @JsonProperty("details")
    private Object details;

    public ErrorResponse timestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Get timestamp
     * @return timestamp
     */
    @Valid
    @Pattern(regexp = "yyyy-MM-dd'T'hh:mm:ssXXX")
    @Schema(name = "timestamp", required = false)
    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ErrorResponse status(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * The HTTP status code
     * @return status
     */
    @NotNull
    @Schema(name = "status", description = "The HTTP status code", required = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ErrorResponse error(String error) {
        this.error = error;
        return this;
    }

    /**
     * Describes the HTTP status phrase
     * @return error
     */
    @NotNull
    @Schema(name = "error", description = "Describes the HTTP status phrase", required = true)
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ErrorResponse path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Describes the path where the error occured
     * @return path
     */
    @Schema(name = "path", description = "Describes the path where the error occured", required = false)
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ErrorResponse code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Describes the error that occured. e.g. MPP_WRONG or SKYPIN_BLOCKED
     * @return code
     */
    @NotNull
    @Schema(
            name = "code",
            description = "Describes the error that occured. e.g. MPP_WRONG or SKYPIN_BLOCKED",
            required = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ErrorResponse details(Object details) {
        this.details = details;
        return this;
    }

    /**
     * Optional field to have the possibility to return additional information about the error. The structure is error specific.
     * @return details
     */
    @Schema(
            name = "details",
            description =
                    "Optional field to have the possibility to return additional information about the error. The structure is error specific.",
            required = false)
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
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
        ErrorResponse errorResponse = (ErrorResponse) o;
        return Objects.equals(this.timestamp, errorResponse.timestamp)
                && Objects.equals(this.status, errorResponse.status)
                && Objects.equals(this.error, errorResponse.error)
                && Objects.equals(this.path, errorResponse.path)
                && Objects.equals(this.code, errorResponse.code)
                && Objects.equals(this.details, errorResponse.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, status, error, path, code, details);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorResponse {\n");
        sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    error: ").append(toIndentedString(error)).append("\n");
        sb.append("    path: ").append(toIndentedString(path)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
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
