/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.runtime.instance;

import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.jobmanager.slots.TaskManagerGateway;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.util.Preconditions;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Simple testing logical slot for testing purposes.
 */
public class TestingLogicalSlot implements LogicalSlot {

	private final TaskManagerLocation taskManagerLocation;

	private final TaskManagerGateway taskManagerGateway;

	private final CompletableFuture<?> releaseFuture;

	private final AtomicReference<Execution> executionReference;

	private final int slotNumber;

	private final AllocationID allocationId;

	public TestingLogicalSlot() {
		this(
			new LocalTaskManagerLocation(),
			new SimpleAckingTaskManagerGateway(),
			0,
			new AllocationID());
	}

	public TestingLogicalSlot(
			TaskManagerLocation taskManagerLocation,
			TaskManagerGateway taskManagerGateway,
			int slotNumber,
			AllocationID allocationId) {
		this.taskManagerLocation = Preconditions.checkNotNull(taskManagerLocation);
		this.taskManagerGateway = Preconditions.checkNotNull(taskManagerGateway);
		this.releaseFuture = new CompletableFuture<>();
		this.executionReference = new AtomicReference<>();
		this.slotNumber = slotNumber;
		this.allocationId = Preconditions.checkNotNull(allocationId);
	}

	@Override
	public TaskManagerLocation getTaskManagerLocation() {
		return taskManagerLocation;
	}

	@Override
	public TaskManagerGateway getTaskManagerGateway() {
		return taskManagerGateway;
	}

	@Override
	public boolean isAlive() {
		return !releaseFuture.isDone();
	}

	@Override
	public boolean isCanceled() {
		return releaseFuture.isDone();
	}

	@Override
	public boolean isReleased() {
		return releaseFuture.isDone();
	}

	@Override
	public boolean setExecution(Execution execution) {
		return executionReference.compareAndSet(null, execution);
	}

	@Override
	public void releaseSlot() {
		releaseFuture.complete(null);
	}

	@Override
	public int getPhysicalSlotNumber() {
		return slotNumber;
	}

	@Override
	public AllocationID getAllocationId() {
		return allocationId;
	}
}
