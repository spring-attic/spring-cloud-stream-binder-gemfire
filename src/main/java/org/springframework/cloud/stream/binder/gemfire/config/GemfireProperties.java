/*
 * Copyright 2016 the original author or authors.
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

package org.springframework.cloud.stream.binder.gemfire.config;

/**
 * @author Patrick Peralta
 */
public class GemfireProperties {

	private String locators = "localhost[7777]";

	private int mcastPort = 0;

	private String logLevel = "warn";

	public String getLocators() {
		return locators;
	}

	public void setLocators(String locators) {
		this.locators = locators;
	}

	public int getMcastPort() {
		return mcastPort;
	}

	public void setMcastPort(int mcastPort) {
		this.mcastPort = mcastPort;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}
}
