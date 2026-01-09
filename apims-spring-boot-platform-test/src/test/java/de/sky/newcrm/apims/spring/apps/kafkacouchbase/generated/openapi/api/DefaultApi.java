/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.api;

import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model.Customer;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model.ErrorResponse;
import de.sky.newcrm.apims.spring.apps.kafkacouchbase.generated.openapi.model.RequestValidationFailed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Generated(
        value = "org.openapitools.codegen.languages.SpringCodegen",
        date = "2023-03-15T21:42:04.788506414+01:00[Europe/Berlin]")
@Validated
@Tag(name = "Default", description = "the Default API")
public interface DefaultApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /example/customers/{customerId}
     * Saving a customer to the database
     *
     * @param customer  (required)
     * @param customerId  (required)
     * @return OK, customer saved (status code 204)
     *         or The customer object is syntactically not correct. (status code 400)
     *         or A business error occurred e.g. the customer could not be validated against certain business rules. (status code 422)
     */
    @Operation(
            operationId = "createCustomer",
            responses = {
                @ApiResponse(responseCode = "204", description = "OK, customer saved"),
                @ApiResponse(
                        responseCode = "400",
                        description = "The customer object is syntactically not correct.",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RequestValidationFailed.class))
                        }),
                @ApiResponse(
                        responseCode = "422",
                        description =
                                "A business error occurred e.g. the customer could not be validated against certain business rules.",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                        })
            })
    @PostMapping(
            value = "/example/customers/{customerId}",
            produces = {"application/json"},
            consumes = {"application/json"})
    default ResponseEntity<Void> createCustomer(
            @Parameter(name = "Customer", description = "", required = true) @Valid @RequestBody Customer customer,
            @Parameter(name = "customerId", description = "", required = true) @PathVariable String customerId) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * DELETE /example/customers/{customerId}
     * Delete a customer by id.
     *
     * @param customerId  (required)
     * @return Customer deleted successfully (status code 204)
     *         or Customer not found (status code 422)
     */
    @Operation(
            operationId = "deleteCustomerById",
            responses = {
                @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
                @ApiResponse(
                        responseCode = "422",
                        description = "Customer not found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                        })
            })
    @DeleteMapping(
            value = "/example/customers/{customerId}",
            produces = {"application/json"})
    default ResponseEntity<Void> deleteCustomerById(
            @Parameter(name = "customerId", description = "", required = true) @PathVariable String customerId) {

        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * GET /example/customers/{customerId}
     * Get a customer by id.
     *
     * @param customerId  (required)
     * @return Returns the customer by id (status code 200)
     *         or Customer not found (status code 422)
     */
    @Operation(
            operationId = "getCustomerById",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Returns the customer by id",
                        content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class))
                        }),
                @ApiResponse(
                        responseCode = "422",
                        description = "Customer not found",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))
                        })
            })
    @GetMapping(
            value = "/example/customers/{customerId}",
            produces = {"application/json"})
    default ResponseEntity<Customer> getCustomerById(
            @Parameter(name = "customerId", description = "", required = true) @PathVariable String customerId) {

        getRequest().ifPresent(request -> {
            for (MediaType mediaType : MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"name\" : \"name\", \"email\" : \"email\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
