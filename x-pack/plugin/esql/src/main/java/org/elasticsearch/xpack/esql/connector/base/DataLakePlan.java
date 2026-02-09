/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.connector.base;

import org.elasticsearch.xpack.esql.connector.ConnectorPlan;
import org.elasticsearch.xpack.esql.core.expression.Expression;

/**
 * Plan interface for data lake connectors.
 *
 * <p>Data lake connectors typically support:
 * <ul>
 *   <li>Filter pushdown (partition pruning, predicate pushdown to format)</li>
 *   <li>Limit pushdown</li>
 *   <li>Column projection</li>
 * </ul>
 *
 * <p>They typically do NOT support ORDER BY or aggregation pushdown since
 * file formats don't guarantee sorted output or support server-side computation.
 *
 * <p>Implementations must extend {@link org.elasticsearch.xpack.esql.plan.logical.LeafPlan}
 * and implement this interface. All methods that modify state return new instances
 * (immutable pattern).
 *
 * @see DataLakeConnector
 */
public interface DataLakePlan extends ConnectorPlan {

    /**
     * The filter expression to apply, or null if none.
     */
    Expression filter();

    /**
     * The limit to apply, or null if none.
     */
    Integer limit();

    /**
     * Whether a filter is present.
     */
    default boolean hasFilter() {
        return filter() != null;
    }

    /**
     * Whether a limit is present.
     */
    default boolean hasLimit() {
        return limit() != null;
    }

    /**
     * Create a copy with the given filter.
     */
    DataLakePlan withFilter(Expression filter);

    /**
     * Create a copy with the given limit.
     */
    DataLakePlan withLimit(Integer limit);
}
