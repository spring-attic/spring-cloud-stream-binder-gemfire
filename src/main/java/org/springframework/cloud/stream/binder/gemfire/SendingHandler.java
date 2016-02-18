/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.gemfire;

import static org.springframework.cloud.stream.binder.gemfire.GemfireMessageChannelBinder.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.stream.binder.DefaultBindingPropertiesAccessor;
import org.springframework.cloud.stream.binder.PartitionHandler;
import org.springframework.cloud.stream.binder.PartitionSelectorStrategy;
import org.springframework.context.Lifecycle;
import org.springframework.expression.EvaluationContext;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;

/**
 * {@link MessageHandler} implementation that publishes messages
 * to GemFire {@link Region}s.
 */
public class SendingHandler extends AbstractMessageHandler implements Lifecycle {

	/**
	 * Binding name.
	 */
	private final String name;

	/**
	 * GemFire peer-to-peer cache.
	 */
	private final Cache cache;

	/**
	 * Type of region to create for sending messages.
	 */
	private final RegionShortcut producerRegionType;

	/**
	 * Sequence number for generating unique message IDs.
	 */
	private final AtomicLong sequence = new AtomicLong();

	/**
	 * Process ID for this process; used for generating unique message IDs.
	 */
	private final int pid;

	/**
	 * Timestamp of when this object was instantiated; used for
	 * generating unique message IDs.
	 */
	private final long timestamp = System.currentTimeMillis();

	/**
	 * Flag that determines if this component is running.
	 */
	private volatile boolean running;

	/**
	 * Replicated region for consumer group registration.
	 * Key is the binding name, value is {@link ConsumerGroupTracker}.
	 */
	private final Region<String, ConsumerGroupTracker> consumerGroupsRegion;

	/**
	 * Map of message regions used for producing messages.
	 */
	private final Map<String, Region<MessageKey, Message<?>>> regionMap = new ConcurrentHashMap<>();

	/**
	 * Lock to guard critical section where regions are created.
	 */
	private final Lock regionCreationLock = new ReentrantLock();

	/**
	 * Bean factory.
	 */
	private final ConfigurableListableBeanFactory beanFactory;

	/**
	 * Evaluation context.
	 */
	private final EvaluationContext evaluationContext;

	/**
	 * Binder properties.
	 */
	private final DefaultBindingPropertiesAccessor properties;

	/**
	 * Strategy for selecting partitions for messages. May be {@code null}.
	 */
	private final PartitionSelectorStrategy partitionSelector;

	/**
	 * Utility for selecting partitions for messages.
	 */
	private volatile PartitionHandler partitionHandler;


	/**
	 * Construct a {@link SendingHandler} for a binding.
	 *
	 * @param cache GemFire peer-to-peer cache; used to generate factories for message regions
	 * @param consumerGroupsRegion replicated region used to hold consumer group registrations
	 * @param name binding name
	 * @param producerRegionType type of region to create for sending messages
	 * @param beanFactory bean factory
	 * @param evaluationContext evaluation context
	 * @param partitionSelector strategy for selecting partitions for messages; may be {@code null}.
	 * @param properties binder properties
	 */
	public SendingHandler(Cache cache,
			Region<String, ConsumerGroupTracker> consumerGroupsRegion,
			String name,
			RegionShortcut producerRegionType,
			ConfigurableListableBeanFactory beanFactory,
			EvaluationContext evaluationContext,
			PartitionSelectorStrategy partitionSelector,
			DefaultBindingPropertiesAccessor properties) {
		Assert.notNull(consumerGroupsRegion);
		Assert.notNull(name);
		Assert.notNull(producerRegionType);
		Assert.notNull(beanFactory);
		Assert.notNull(evaluationContext);
		Assert.notNull(properties);

		this.cache = cache;
		this.name = name;
		this.producerRegionType = producerRegionType;
		this.consumerGroupsRegion = consumerGroupsRegion;
		this.pid = cache.getDistributedSystem().getDistributedMember().getProcessId();
		this.beanFactory = beanFactory;
		this.evaluationContext = evaluationContext;
		this.partitionSelector = partitionSelector;
		this.properties = properties;
	}

	/**
	 * Create a {@link Region} instance used for publishing {@link Message} objects.
	 * This region instance will not store buckets; it is assumed that the regions
	 * created by consumers will host buckets.
	 *
	 * @param regionName name of the message region
	 * @return region for producing messages
	 */
	private Region<MessageKey, Message<?>> createProducerMessageRegion(String regionName) {
		RegionFactory<MessageKey, Message<?>> factory = this.cache.createRegionFactory(this.producerRegionType);
		return factory.addAsyncEventQueueId(regionName + GemfireMessageChannelBinder.QUEUE_POSTFIX)
				.create(regionName);
	}

	@Override
	protected void handleMessageInternal(Message<?> message) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace("Publishing message" + message);
		}

		for (String group : getGroups()) {
			Region<MessageKey, Message<?>> region = getRegionForGroup(group);

			Assert.notNull(this.partitionHandler, "PartitionHandler not initialized");
			MessageKey key = this.partitionHandler.isPartitionedModule()
					? nextMessageKey(this.partitionHandler.determinePartition(message))
					: nextMessageKey();

			region.putAll(Collections.singletonMap(key, message));
		}
	}

	/**
	 * Return a set of consumer group names for the binder for this sender.
	 *
	 * @return set of consumer group names
	 */
	private Set<String> getGroups() {
		Set<String> groups;
		ConsumerGroupTracker tracker = this.consumerGroupsRegion.get(this.name);
		if (tracker == null) {
			groups = Collections.singleton(DEFAULT_CONSUMER_GROUP);
		}
		else {
			groups = tracker.groups();
			if (groups.isEmpty()) {
				groups = Collections.singleton(DEFAULT_CONSUMER_GROUP);
			}
		}

		return groups;
	}

	/**
	 * Return a region for publishing messages for a specified consumer group.
	 *
	 * @param group consumer group to publish messages to
	 * @return region to publish messages to
	 * @throws InterruptedException
	 */
	private Region<MessageKey, Message<?>> getRegionForGroup(String group) throws InterruptedException {
		String regionName = createMessageRegionName(this.name, group);
		Region<MessageKey, Message<?>> region = this.regionMap.get(regionName);
		if (region == null) {
			try {
				this.regionCreationLock.lockInterruptibly();
				region = this.regionMap.get(regionName);
				if (region == null) {
					region = createProducerMessageRegion(regionName);
					this.regionMap.put(regionName, region);
				}
				if (this.partitionHandler == null) {
					// this assumes that all regions for all groups have the same number of buckets
					this.partitionHandler = new PartitionHandler(this.beanFactory, this.evaluationContext,
							this.partitionSelector, this.properties,
							PartitionRegionHelper.getPartitionRegionInfo(region).getConfiguredBucketCount());
				}
			}
			finally {
				this.regionCreationLock.unlock();
			}
		}

		return region;
	}

	/**
	 * Generate and return a new message key for a message.
	 *
	 * @return new message key
	 */
	private MessageKey nextMessageKey() {
		return new MessageKey(sequence.getAndIncrement(), timestamp, pid);
	}

	/**
	 * Generate and return a new message key for a message.
	 *
	 * @param partition the partition that the message should be assigned to
	 * @return new message key
	 */
	private MessageKey nextMessageKey(int partition) {
		return new MessageKey(sequence.getAndIncrement(), timestamp, pid, partition);
	}

	@Override
	public void start() {
		this.running = true;
	}

	@Override
	public void stop() {
		this.running = false;
		for (Region<MessageKey, Message<?>> region : this.regionMap.values()) {
			region.close();
		}
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}
}
