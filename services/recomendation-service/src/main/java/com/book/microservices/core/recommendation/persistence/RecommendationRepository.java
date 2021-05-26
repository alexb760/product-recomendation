/*
 * Copyright (c) 2021 by PROS, Inc.  All Rights Reserved.
 * This software is the confidential and proprietary information of
 * PROS, Inc. ("Confidential Information").
 * You may not disclose such Confidential Information, and may only
 * use such Confidential Information in accordance with the terms of
 * the license agreement you entered into with PROS.
 */
package com.book.microservices.core.recommendation.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Alexander Bravo
 */
public interface RecommendationRepository extends CrudRepository<RecommendationEntity, String> {
 List<RecommendationEntity> findByProductId(int recommendationId);
}
