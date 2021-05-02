#!/usr/bin/env bash

export SPRING_HOME=/mnt/c/Laboral/tools/spring-2.4.5
mkdir services
cd services

$SPRING_HOME/bin/spring init \
	--boot-version=2.4.5 \
	--build=gradle \
	--java-version=14 \
	--packaging=jar \
	--name=product-service \
	--package-name=com.book.microservices.core.product \
	--groupId=com.book.microservices.core.product \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	product-service

$SPRING_HOME/bin/spring init \
	--boot-version=2.4.5 \
	--build=gradle \
	--java-version=14 \
	--packaging=jar \
	--name=review-service \
	--package-name=com.book.microservices.core.review \
	--groupId=com.book.microservices.core.review \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	review-service

$SPRING_HOME/bin/spring init \
	--boot-version=2.4.5 \
	--build=gradle \
	--java-version=14 \
	--packaging=jar \
	--name=recommendation-service \
	--package-name=com.book.microservices.core.recommendation \
	--groupId=com.book.microservices.core.recommendation \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	recommendation-service


$SPRING_HOME/bin/spring init \
        --boot-version=2.4.5 \
	--build=gradle \
	--java-version=14 \
	--packaging=jar \
	--name=product-composite-service \
	--package-name=com.book.microservices.composite.product \
	--groupId=com.book.microservices.composite.product \
	--dependencies=actuator,webflux \
	--version=1.0.0-SNAPSHOT \
	product-composite-service
