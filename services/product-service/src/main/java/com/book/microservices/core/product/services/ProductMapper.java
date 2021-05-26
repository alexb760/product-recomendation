package com.book.microservices.core.product.services;

import com.book.api.core.product.Product;
import com.book.microservices.core.product.persintence.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/** @author Alexander Bravo */
@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mappings({@Mapping(target = "serviceAddress", ignore = true)})
  Product entityToApi(ProductEntity entity);

  @Mappings({@Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)})
  ProductEntity apiToEntity(Product api);
}
