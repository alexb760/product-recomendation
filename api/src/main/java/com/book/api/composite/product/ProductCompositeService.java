package com.book.api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Alexander Bravo */
@Api
public interface ProductCompositeService {

  /**
   * Sample usage: curl $HOST:$PORT/product-composite/1
   *
   * @param productId product identifier
   * @return the composite product info, if found, else null
   */
  @ApiOperation(
      value = "${api.product-composite.get-composite-product.description}",
      notes = "${api.product-composite.get-composite-product.notes}")
  @ApiResponses(value = {
      @ApiResponse(responseCode =  " 400 - Bad Request, invalid format of the request. See response message for more information."),
      @ApiResponse(responseCode =  "404 - Not found, the specified id does not exist."),
      @ApiResponse(responseCode =  "422 - Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
  })
  @GetMapping(value = "/product-composite/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ProductAggregate getProduct(@PathVariable int productId);
}
