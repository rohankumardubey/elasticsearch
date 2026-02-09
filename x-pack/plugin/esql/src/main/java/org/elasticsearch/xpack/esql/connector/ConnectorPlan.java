/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.connector;

import org.elasticsearch.xpack.esql.core.expression.Attribute;

import java.util.List;

/**
 * Interface for connector-specific logical plan leaf nodes.
 *
 * <p>Each connector defines its own implementation that extends
 * {@link org.elasticsearch.xpack.esql.plan.logical.LeafPlan} and implements this interface.
 * Connector implementations store whatever state they need (filters, SQL fragments,
 * file lists, etc.) as type-safe fields in their own classes.
 *
 * <h2>Lifecycle</h2>
 *
 * <ol>
 *   <li><b>Creation:</b> {@link Connector#resolve} creates the initial node with schema</li>
 *   <li><b>Optimization:</b> Connector-provided {@link Connector#optimizationRules() rules}
 *       fold operations (Filter, Limit, etc.) into this node</li>
 *   <li><b>Physical Planning:</b> {@code Mapper} calls {@link Connector#createPhysicalPlan}</li>
 * </ol>
 *
 * <h2>Implementation Requirements</h2>
 *
 * <p>Implementations must:
 * <ul>
 *   <li>Extend {@link org.elasticsearch.xpack.esql.plan.logical.LeafPlan}</li>
 *   <li>Be immutable</li>
 *   <li>Implement {@link #connector()} to return their owning connector</li>
 * </ul>
 *
 * <p>Operation-specific state (filters, limits, ORDER BY, aggregations, etc.) is managed
 * by the connector implementation and its base classes, not by this interface.
 *
 * @see Connector
 * @see org.elasticsearch.xpack.esql.connector.base.DataLakeConnector
 * @see org.elasticsearch.xpack.esql.connector.base.SqlConnector
 */
public interface ConnectorPlan {

    /**
     * The connector that owns this plan node.
     */
    Connector connector();

    /**
     * The source location (URI/path) for error messages and identification.
     */
    String location();

    /**
     * The output columns (projected schema).
     * This is required by the logical plan infrastructure.
     */
    List<Attribute> output();
}
