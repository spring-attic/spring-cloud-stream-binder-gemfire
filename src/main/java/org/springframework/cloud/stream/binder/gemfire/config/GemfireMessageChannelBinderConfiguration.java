/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.gemfire.config;

import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.RegionShortcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.binder.gemfire.GemfireMessageChannelBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.CacheFactoryBean;

/**
 * @author Patrick Peralta
 */
@Configuration
@EnableConfigurationProperties({GemfireBinderConfigurationProperties.class, GemfireProperties.class})
public class GemfireMessageChannelBinderConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(GemfireMessageChannelBinderConfiguration.class);

	@Autowired
	public GemfireBinderConfigurationProperties properties;


	@Bean
	CacheFactoryBean gemfireCacheBean() {
		CacheFactoryBean gemfireCache = new CacheFactoryBean();

		gemfireCache.setClose(true);
		gemfireCache.setLazyInitialize(true);
		gemfireCache.setProperties(this.properties.toProperties());
		gemfireCache.setUseBeanFactoryLocator(false);

		return gemfireCache;
	}

	@Bean
	public GemfireMessageChannelBinder messageChannelBinder(Cache gemfireCache) {
		GemfireMessageChannelBinder binder = new GemfireMessageChannelBinder(gemfireCache);
		binder.setBatchSize(this.properties.getBatchSize());
		try {
			binder.setConsumerRegionType(RegionShortcut.valueOf(this.properties.getConsumerRegionType()));
		}
		catch (IllegalArgumentException e) {
			logger.warn("Unsupported region type: {}", this.properties.getConsumerRegionType());
		}
		try {
			binder.setProducerRegionType(RegionShortcut.valueOf(this.properties.getProducerRegionType()));
		}
		catch (IllegalArgumentException e) {
			logger.warn("Unsupported region type: {}", this.properties.getProducerRegionType());
		}
		binder.setPersistentQueue(this.properties.isPersistentQueue());

		return binder;
	}

}
