/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.connector;

import java.util.OptionalLong;

/**
 * A partition of work that can be executed independently on a cluster data node.
 *
 * <h2>Lifecycle</h2>
 *
 * <p><b>Created by:</b> {@link Connector#planPartitions}, which is called by the physical
 * planner on the coordinator after physical planning completes.
 *
 * <p><b>Transported:</b> Serialized and sent from coordinator to the target data node.
 *
 * <p><b>Consumed by:</b> {@link Connector#createSourceOperator}, which is called by
 * {@code LocalExecutionPlanner} on the data node to create the actual operator that
 * reads data.
 *
 * <h2>Examples</h2>
 * <ul>
 *   <li>Parquet: Each partition contains a subset of files to read</li>
 *   <li>Iceberg: Each partition contains file scan tasks</li>
 *   <li>Sharded database: Each partition targets a different shard</li>
 * </ul>
 *
 * @param plan The connector plan for this partition (may have partition-specific state)
 * @param nodeAffinity Preferred data node for execution (for data locality), or null
 * @param estimatedRows Estimated rows in this partition (for load balancing)
 * @param estimatedBytes Estimated bytes (for memory planning)
 */
public record SourcePartition(ConnectorPlan plan, String nodeAffinity, OptionalLong estimatedRows, OptionalLong estimatedBytes) {

    /**
     * Create a single partition for the entire plan (coordinator-only execution).
     */
    public static SourcePartition single(ConnectorPlan plan) {
        return new SourcePartition(plan, null, OptionalLong.empty(), OptionalLong.empty());
    }

    /**
     * Create a partition without size estimates.
     */
    public SourcePartition(ConnectorPlan plan, String nodeAffinity) {
        this(plan, nodeAffinity, OptionalLong.empty(), OptionalLong.empty());
    }

    /**
     * Create a partition with size estimates.
     */
    public SourcePartition(ConnectorPlan plan, String nodeAffinity, long estimatedRows, long estimatedBytes) {
        this(plan, nodeAffinity, OptionalLong.of(estimatedRows), OptionalLong.of(estimatedBytes));
    }

    /**
     * Whether this partition has a preferred data node.
     */
    public boolean hasNodeAffinity() {
        return nodeAffinity != null;
    }

    /**
     * Get estimated rows or a default value.
     */
    public long estimatedRowsOr(long defaultValue) {
        return estimatedRows.orElse(defaultValue);
    }

    /**
     * Get estimated bytes or a default value.
     */
    public long estimatedBytesOr(long defaultValue) {
        return estimatedBytes.orElse(defaultValue);
    }
}
