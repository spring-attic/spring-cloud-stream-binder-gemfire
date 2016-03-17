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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.PartitionAttributes;
import com.gemstone.gemfire.cache.PartitionAttributesFactory;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionFactory;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.Scope;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventListener;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventQueue;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventQueueFactory;
import com.gemstone.gemfire.cache.partition.PartitionListenerAdapter;

import org.springframework.cloud.stream.binder.AbstractBinder;
import org.springframework.cloud.stream.binder.Binding;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.DefaultBinding;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.endpoint.EventDrivenConsumer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


/**
 * A binder that uses <a href="http://gemfire.docs.pivotal.io/">GemFire</a>
 * for message delivery. Spring Cloud Stream modules that are of type
 * processor or sink will host buckets for a partitioned region used
 * to store {@link Message messages}. This allows for message delivery
 * directly to the modules that will process them (as opposed to fetching
 * messages from a stand-alone messaging system).
 * <p>
 * Messages for each consumer group will be stored in their own partitioned
 * region. Consumer group metadata is stored in a replicated region.
 *
 * @author Patrick Peralta
 */
public class GemfireMessageChannelBinder
		extends AbstractBinder<MessageChannel, ConsumerProperties, ProducerProperties> {

	/**
	 * SPeL parser.
	 */
	private static final SpelExpressionParser parser = new SpelExpressionParser();

	/**
	 * Postfix for message regions.
	 */
	public static final String MESSAGES_POSTFIX = "_messages";

	/**
	 * Postfix for region event queues.
	 */
	public static final String QUEUE_POSTFIX = "_queue";

	/**
	 * Name of replicated region used to register consumer groups.
	 */
	public static final String CONSUMER_GROUPS_REGION = "consumer_groups_region";

	/**
	 * Name of default consumer group.
	 */
	public static final String DEFAULT_CONSUMER_GROUP = "default";

	/**
	 * GemFire peer-to-peer cache.
	 */
	private final Cache cache;

	/**
	 * Maximum number of messages to be fetched from the region
	 * for processing at a time.
	 */
	private volatile int batchSize;

	/**
	 * Type of region to use for consuming messages.
	 */
	private volatile RegionShortcut consumerRegionType = RegionShortcut.PARTITION;

	/**
	 * Type of region to use for producing messages.
	 */
	private volatile RegionShortcut producerRegionType = RegionShortcut.PARTITION_PROXY;

	/**
	 * If {@code true}, the event queue is persistent.
	 */
	private volatile boolean persistentQueue = false;

	/**
	 * Map of message regions used for consuming messages.
	 */
	private final Map<String, Region<MessageKey, Message<?>>> regionMap = new ConcurrentHashMap<>();

	/**
	 * Map of registered {@link SendingHandler}s for producers.
	 */
	private final Map<String, SendingHandler> sendingHandlerMap = new ConcurrentHashMap<>();

	/**
	 * Replicated region for consumer group registration.
	 * Key is the binding name, value is {@link ConsumerGroupTracker}.
	 */
	private volatile Region<String, ConsumerGroupTracker> consumerGroupsRegion;


	/**
	 * Construct a GemfireMessageChannelBinder.
	 *
	 * @param cache a configured GemFire {@link Cache}.
	 */
	public GemfireMessageChannelBinder(Cache cache) {
		this.cache = cache;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public RegionShortcut getConsumerRegionType() {
		return consumerRegionType;
	}

	public void setConsumerRegionType(RegionShortcut consumerRegionType) {
		this.consumerRegionType = consumerRegionType;
	}

	public RegionShortcut getProducerRegionType() {
		return producerRegionType;
	}

	public void setProducerRegionType(RegionShortcut producerRegionType) {
		this.producerRegionType = producerRegionType;
	}

	public boolean isPersistentQueue() {
		return persistentQueue;
	}

	public void setPersistentQueue(boolean persistentQueue) {
		this.persistentQueue = persistentQueue;
	}

	@Override
	public void onInit() throws Exception {
		RegionFactory<String, ConsumerGroupTracker> regionFactory = this.cache.createRegionFactory(RegionShortcut.REPLICATE);
		this.consumerGroupsRegion = regionFactory.setScope(Scope.GLOBAL).create(CONSUMER_GROUPS_REGION);
	}

	/**
	 * For a binding name and consumer group name, return a string
	 * used for naming the region that will hold messages for this binding
	 * and consumer group.
	 *
	 * @param name binding name
	 * @param group consumer group name
	 * @return name of region for messages for this binding and consumer group
	 */
	public static String createMessageRegionName(String name, String group) {
		return String.format("%s_%s%s", name, group, MESSAGES_POSTFIX);
	}

	/**
	 * Create a {@link Region} instance used for consuming {@link Message} objects.
	 * This region is created with an async event queue ID that will associate
	 * it with an {@link AsyncEventListeningMessageProducer} as a cache
	 * listener which triggers message consumption when a message is added to the region.
	 *
	 * @param regionName prefix of the message region name
	 * @param queueId queue id to associate with region
	 *
	 * @return region for consuming messages
	 */
	protected Region<MessageKey, Message<?>> createConsumerMessageRegion(String regionName, String queueId)  {
		RegionFactory<MessageKey, Message<?>> regionFactory = this.cache.createRegionFactory(getConsumerRegionType());
		return regionFactory.setPartitionAttributes(createPartitionAttributes())
				.addAsyncEventQueueId(queueId).create(regionName);
	}

	/**
	 * Create {@link PartitionAttributes} for partitioned regions used for
	 * storing messages.
	 *
	 * @return partition attributes for message regions
	 */
	protected PartitionAttributes createPartitionAttributes() {
		return new PartitionAttributesFactory().addPartitionListener(new PartitionListenerAdapter() {
			@Override
			public void afterBucketRemoved(int i, Iterable<?> iterable) {
				logger.debug("Bucket {} removed", i);
			}

			@Override
			public void afterBucketCreated(int i, Iterable<?> iterable) {
				logger.debug("Bucket {} created", i);
			}
		}).create();
	}

	/**
	 * Create a {@link AsyncEventQueue} for passing messages to the provided
	 * {@link AsyncEventListener}.
	 *
	 * @param name prefix of the event queue name
	 * @param eventListener message listener invoked when an event is added to the queue
	 * @return queue for processing region events
	 */
	protected AsyncEventQueue createAsyncEventQueue(String name, AsyncEventListener eventListener) {
		AsyncEventQueueFactory queueFactory = this.cache.createAsyncEventQueueFactory();
		queueFactory.setPersistent(this.persistentQueue);
		queueFactory.setParallel(true);
		queueFactory.setBatchSize(this.batchSize);
		String queueId = name + QUEUE_POSTFIX;
		return queueFactory.create(queueId, eventListener);
	}

	/**
	 * Register a consumer group for a binding.
	 *
	 * @param name  binding name
	 * @param group consumer group name
	 */
	private void addConsumerGroup(String name, String group) {
		Lock lock = this.consumerGroupsRegion.getDistributedLock(name);
		try {
			lock.lockInterruptibly();
			ConsumerGroupTracker tracker = this.consumerGroupsRegion.get(name);
			if (tracker == null) {
				tracker = new ConsumerGroupTracker();
			}
			tracker.addGroup(group);
			this.consumerGroupsRegion.put(name, tracker);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for lock '" + name + "'", e);
		}
		finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	/**
	 * Remove registration for a consumer group for a binding.
	 *
	 * @param name  binding name
	 * @param group consumer group name
	 */
	private void removeConsumerGroup(String name, String group) {
		Lock lock = this.consumerGroupsRegion.getDistributedLock(name);
		try {
			lock.lockInterruptibly();
			ConsumerGroupTracker tracker = this.consumerGroupsRegion.get(name);
			if (tracker == null) {
				return;
			}
			tracker.removeGroup(group);
			this.consumerGroupsRegion.put(name, tracker);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for lock '" + name + "'", e);
		}
		finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	protected Binding<MessageChannel> doBindConsumer(String name, String group,
			MessageChannel inputChannel, ConsumerProperties properties) {
		if (StringUtils.isEmpty(group)) {
			group = DEFAULT_CONSUMER_GROUP;
		}
		String messageRegionName = createMessageRegionName(name, group);

		AsyncEventListeningMessageProducer messageProducer = new AsyncEventListeningMessageProducer();
		messageProducer.setOutputChannel(inputChannel);
		messageProducer.setExpressionPayload(parser.parseExpression("deserializedValue"));
		messageProducer.setBeanFactory(this.getBeanFactory());
		messageProducer.afterPropertiesSet();

		AsyncEventQueue queue = createAsyncEventQueue(messageRegionName, messageProducer);
		Region<MessageKey, Message<?>> messageRegion = createConsumerMessageRegion(messageRegionName, queue.getId());

		this.regionMap.put(name, messageRegion);
		addConsumerGroup(name, group);
		messageProducer.start();

		return bindingForConsumer(name, group, inputChannel, messageProducer);
	}

	@Override
	protected Binding<MessageChannel> doBindProducer(String name,
			MessageChannel outboundBindTarget, ProducerProperties properties) {
		Assert.isInstanceOf(SubscribableChannel.class, outboundBindTarget);

		SendingHandler handler = new SendingHandler(this.cache, this.consumerGroupsRegion,
				name, this.producerRegionType, createPartitionAttributes(), getBeanFactory(),
				this.evaluationContext, this.partitionSelector, properties);
		handler.start();

		SubscribableChannel subscribableChannel = (SubscribableChannel) outboundBindTarget;
		subscribableChannel.subscribe(handler);

		this.sendingHandlerMap.put(name, handler);

		return bindingForProducer(name, outboundBindTarget,
				new EventDrivenConsumer(subscribableChannel, handler));
	}

	private DefaultBinding<MessageChannel> bindingForProducer(String name, MessageChannel target,
			AbstractEndpoint endpoint) {

		return new DefaultBinding<MessageChannel>(name, /*group*/null, target, endpoint) {

			@Override
			protected void afterUnbind() {
				SendingHandler handler = sendingHandlerMap.remove(getName());
				if (handler != null) {
					handler.stop();
				}
			}
		};
	}

	private DefaultBinding<MessageChannel> bindingForConsumer(String name, String group,
			MessageChannel target, AbstractEndpoint endpoint) {

		return new DefaultBinding<MessageChannel>(name, group, target, endpoint) {

			@Override
			protected void afterUnbind() {
				Region<MessageKey, Message<?>> region = regionMap.remove(
						createMessageRegionName(getName(), getGroup()));
				if (region != null) {
					region.close();
				}

				removeConsumerGroup(getName(), getGroup());
			}
		};
	}

}
