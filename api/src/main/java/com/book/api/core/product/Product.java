package com.book.api.core.product;

import lombok.Getter;

/**
 * @author Alexander Bravo
 */
@Getter
public class Product {
 private final int productId;
 private final String name;
 private final int weight;
 private final String serviceAddress;

 public Product(){
  productId = 0;
  name = null;
  weight = 0;
  serviceAddress = null;
 }

 public Product(int productId, String name, int weight, String serviceAddress) {
  this.productId = productId;
  this.name = name;
  this.weight = weight;
  this.serviceAddress = serviceAddress;
 }
}
