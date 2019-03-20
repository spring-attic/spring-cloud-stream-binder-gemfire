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

import java.lang.reflect.Field;
import java.util.Properties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Gemfire <a href = "https://gemfire.docs.pivotal.io/docs-gemfire/reference/topics/gemfire_properties.html">properties</a>
 * in a JavaBean style object. This allows for the use of
 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html">Spring Boot</a>
 * configuration for Gemfire properties.
 *
 * @author Patrick Peralta
 */
@ConfigurationProperties(prefix = "spring.cloud.stream.gemfire.binder")
public class GemfireProperties {

	/**
	 * Number of seconds the distributed system will wait after the
	 * {@code ack-wait-threshold} for a message to be acknowledged
	 * before it issues an alert at severe level. A value of zero
	 * disables this feature.
	 */
	@Min(0)
	private Integer ackSevereAlertThreshold;

	/**
	 * Number of seconds a distributed message can wait for
	 * acknowledgment before it sends an alert to signal that
	 * something might be wrong with the system member that
	 * is unresponsive.
	 * <p>
	 * The waiter continues to wait. The alerts are logged
	 * in the system member’s log as warnings.
	 * <p></p>
	 * Valid values are in the range 0...2147483647.
	 */
	@Min(0)
	private Integer ackWaitThreshold;

	/**
	 * Maximum size (in megabytes) of all inactive statistic
	 * archive files combined. If this limit is exceeded,
	 * inactive archive files are deleted, oldest first,
	 * until the total size is within the limit. If set to
	 * zero, disk space use is unlimited.
	 */
	@Min(0)
	private Integer archiveDiskSpaceLimit;

	/**
	 * The maximum size (in megabytes) of a single statistic
	 * archive file. Once this limit is exceeded, a new statistic
	 * archive file is created, and the current archive file
	 * becomes inactive. If set to zero, file size is unlimited.
	 */
	@Min(0)
	private Integer archiveFileSizeLimit;

	/**
	 * The number of milliseconds a process that is publishing to
	 * this process should attempt to distribute a cache operation
	 * before switching over to asynchronous messaging for this
	 * process. The switch to asynchronous messaging lasts until
	 * this process catches up, departs, or some specified limit
	 * is reached, such as {@code async-queue-timeout} or
	 * {@code async-max-queue-size}. To enable asynchronous messaging,
	 * the value must be set above zero. Valid values are in the
	 * range 0...60000.
	 * <p></p>
	 * Note: This setting controls only peer-to-peer communication
	 * and does not apply to client/server or multi-site communication.
	 */
	@Min(0)
	@Max(60000)
	private Integer asyncDistributionTimeout;

	/**
	 * Affects non-conflated asynchronous queues for members that
	 * publish to this member. This is the maximum size the queue
	 * can reach (in megabytes) before the publisher asks this
	 * member to leave the distributed system. Valid values are
	 * in the range 0..1024.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication
	 * and does not apply to client/server or multi-site communication.
	 */
	@Min(0)
	@Max(1024)
	private Integer asyncMaxQueueSize;

	/**
	 * Affects asynchronous queues for members that publish to this
	 * member. This is the maximum milliseconds the publisher should
	 * wait with no distribution to this member before it asks this
	 * member to leave the distributed system. Used for handling
	 * slow receivers.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication
	 * and does not apply to client/server or multi-site communication.
	 */
	private Integer asyncQueueTimeout;

	/**
	 * Relevant only for multi-homed hosts - machines with multiple
	 * network interface cards. Specifies the adapter card the cache
	 * binds to for peer-to-peer communication. Also specifies the
	 * default location for GemFire servers to listen on, which is
	 * used unless overridden by the server-bind-address. An empty
	 * string causes the member to listen on the default card for the
	 * machine. This is a machine-wide attribute used for system member
	 * and client/server communication. It has no effect on locator
	 * location, unless the locator is embedded in a member process.
	 * <p>
	 * Specify the IP address, not the hostname, because each network
	 * card may not have a unique hostname. An empty string (the default)
	 * causes the member to listen on the default card for the machine.
	 */
	private String bindAddress;

	// todo: should we include cache-xml-file?

	/**
	 * This property specifies the directory in which the cluster
	 * configuration related disk-store and artifacts are stored.
	 * This property is only applicable to dedicated locators
	 * that have "enable-cluster-configuration" set to true.
	 */
	private String clusterConfigurationDir;

	/**
	 * Used for SSL security. A space-separated list of the valid
	 * SSL ciphers for peer-to-peer connections in the cluster.
	 * A setting of 'any' uses any ciphers that are enabled by
	 * default in the configured JSSE provider. This attribute
	 * must be consistent across all members of the distributed
	 * system. GemFire applies this peer-to-peer connection property
	 * setting to client/server, JMX manager, WAN gateway and HTTP
	 * service connections unless the corresponding SSL property
	 * (server-ssl-ciphers, jmx-manager-ssl-ciphers,
	 * gateway-ssl-ciphers, or http-service-ssl-ciphers) is defined.
	 */
	private String clusterSslCiphers;

	/**
	 * Used for SSL security. Boolean indicating whether to use SSL
	 * for peer-to-peer communications. A true setting requires
	 * the use of locators. This attribute must be consistent across
	 * all members of the distributed system. GemFire applies this
	 * peer-to-peer connection property setting to client/server,
	 * JMX manager, WAN gateway, and HTTP service connections unless
	 * the corresponding SSL property (server-ssl-enabled,
	 * jmx-manager-ssl-enabled, gateway-ssl-enabled, or
	 * http-service-ssl-enabled) is defined.
	 */
	private Boolean clusterSslEnabled;

	/**
	 * Property that identifies the keystore to use for SSL connections.
	 * {@code clusterSslKeystore} defines the keystore for GemFire
	 * peer-to-peer connections. If you specify only {@code clusterSslKeystore},
	 * then the same keystore is also used for client/server, JMX manager,
	 * WAN gateway and HTTP service connections.
	 */
	private String clusterSslKeystore;

	/**
	 * Keystore to use for WAN gateway SSL connections.
	 */
	private String gatewaySslKeystore;

	/**
	 * Keystore to use for HTTP service SSL connections.
	 */
	private String httpServiceSslKeystore;

	/**
	 * Keystore to use for JMX manager SSL connections.
	 */
	private String jmxManagerSslKeystore;

	/**
	 * Keystore to use for client/server SSL connections.
	 */
	private String serverSslKeystore;

	/**
	 * Password for peer to peer SSL keystore.
	 */
	private String clusterSslKeystorePassword;

	/**
	 * Password for WAN gateway SSL keystore.
	 */
	private String gatewaySslKeystorePassword;

	/**
	 * Password for HTTP service SSL keystore.
	 */
	private String httpServiceSslKeystorePassword;

	/**
	 * Password for JMX manager SSL keystore.
	 */
	private String jmxManagerSslKeystorePassword;

	/**
	 * Password for client/server SSL keystore.
	 */
	private String serverSslKeystorePassword;

    private String clusterSslKeystoreType;
    private String gatewaySslKeystoreType;
    private String httpServiceSslKeystoreType;
    private String jmxManagerSslKeystoreType;
    private String serverSslKeystoreType;

	/**
	 * A space-separated list of the valid SSL protocols for
	 * peer-to-peer connections in the cluster. A setting of
	 * 'any' uses any protocol that is enabled by default in
	 * the configured JSSE provider. GemFire applies this
	 * peer-to-peer connection property setting to client/server,
	 * JMX manager, WAN gateway and HTTP service connections
	 * unless the corresponding SSL property (server-ssl-protocols,
	 * jmx-manager-ssl-protocols, gateway-ssl-protocols, or
	 * http-service-ssl-protocols) is defined
	 */
	private String clusterSslProtocols;

	/**
	 * Boolean indicating whether to require authentication for
	 * member communication. GemFire applies this peer-to-peer
	 * connection property setting to client/server, JMX manager,
	 * WAN gateway and HTTP service connections unless the corresponding
	 * SSL property (server-ssl-require-authentication,
	 * jmx-manager-ssl-require-authentication,
	 * gateway-ssl-require-authentication, or
	 * http-service-ssl-require-authentication) is defined.
	 */
	private Boolean clusterSslRequireAuthentication;

	private String clusterSslTruststore;
	private String gatewaySslTruststore;
	private String httpServiceSslTruststore;
	private String jmxManagerSslTruststore;
	private String serverSslTruststore;

	private String clusterSslTruststorePassword;
	private String gatewaySslTruststorePassword;
	private String httpServiceSslTruststorePassword;
	private String jmxManagerSslTruststorePassword;
	private String serverSslTruststorePassword;

	/**
	 * Used only by clients in a client/server installation.
	 * This is a client-side property that is passed to the server.
	 * Affects subscription queue conflation in this client's
	 * servers. Specifies whether to conflate (true setting),
	 * not conflate (false), or to use the server's conflation
	 * setting (server).
	 */
	private String conflateEvents;

	/**
	 * Specifies whether sockets are shared by the system
	 * member’s threads. If true, threads share, and a minimum
	 * number of sockets are used to connect to the distributed
	 * system. If false, every application thread has its own
	 * sockets for distribution purposes. You can override this
	 * setting for individual threads inside your application.
	 * Where possible, it is better to set conserve-sockets to
	 * true and enable the use of specific extra sockets in the
	 * application code if needed.
	 * <p>
	 * Note: WAN deployments increase the messaging demands on
	 * a GemFire system. To avoid hangs related to WAN messaging,
	 * always set conserve-sockets=false for GemFire members
	 * that participate in a WAN deployment.
	 */
	private Boolean conserveSockets;

	/**
	 * Specifies whether to distribute the deltas for entry updates,
	 * instead of the full values, between clients and servers
	 * and between peers.
	 */
	private Boolean deltaPropagation;

	/**
	 * Working directory used when deploying JAR application
	 * files to distributed system members. This directory
	 * can be local and unique to the member or a shared resource.
	 */
	private String deployWorkingDir;

	/**
	 * By default, a GemFire member (both locators and servers)
	 * will attempt to reconnect and reinitialize the cache after
	 * it has been forced out of the distributed system by a
	 * network partition event or has otherwise been shunned
	 * by other members. Use this property to turn off the
	 * autoreconnect behavior.
	 */
	private Boolean disableAutoReconnect;

	/**
	 * Boolean indicating whether to disable the use of TCP/IP
	 * sockets for inter-cache point-to-point messaging. If
	 * disabled, the cache uses datagram (UDP) sockets.
	 */
	private Boolean disableTcp;

	/**
	 * Identifier used to distinguish messages from different
	 * distributed systems. Set this to different values for
	 * different systems in a multi-site (WAN) configuration.
	 * This is required for Portable Data eXchange (PDX) data
	 * serialization. This setting must be the same for every
	 * member in the same distributed system and unique to the
	 * distributed system within the WAN installation.
	 * -1 means no setting. Valid values are integers in
	 * the range -1...255.
	 */
	@Min(-1)
	@Max(255)
	private Integer distributedSystemId;

	/**
	 * Used only for clients in a client/server installation. If set,
	 * this indicates that the client is durable and identifies the
	 * client. The ID is used by servers to reestablish any messaging
	 * that was interrupted by client downtime.
	 */
	private String durableClientId;

	/**
	 * Used only for clients in a client/server installation. Number of
	 * seconds this client can remain disconnected from its server and
	 * have the server continue to accumulate durable events for it.
	 */
	private Integer durableClientTimeout;

	/**
	 * Boolean instructing the system to detect and handle splits
	 * in the distributed system, typically caused by a partitioning
	 * of the network (split brain) where the distributed system is
	 * running. We recommend setting this property to true. You must
	 * set this property to the same value across all your distributed
	 * system members. In addition, you must set this property to true
	 * if you are using persistent regions and configure your regions
	 * to use DISTRIBUTED_ACK or GLOBAL scope to avoid potential
	 * data conflicts.
	 */
	private Boolean enableNetworkPartitionDetection;

	/**
	 * A value of "true" causes the creation of cluster configuration
	 * on dedicated locators. The cluster configuration service on
	 * dedicated locator(s) with this property set to "true" would
	 * serve the configuration to new members joining the distributed
	 * system and also save the configuration changes caused by the
	 * gfsh commands. This property is only applicable to dedicated locators.
	 */
	private Boolean enableClusterConfiguration;

	/**
	 * Boolean instructing the system to track time-based statistics
	 * for the distributed system and caching. Disabled by default
	 * for performance reasons and not recommended for production
	 * environments. You must also configure
	 * statistics-sampling-enabled to true and
	 * specify a statistics-archive-file.
	 */
	private Boolean enableTimeStatistics;

	/**
	 * Whether partitioned regions will put redundant copies of the
	 * same data in different members running on the same physical
	 * machine. By default, GemFire tries to put redundant copies on
	 * different machines, but it will put them on the same machine
	 * if no other machines are available. Setting this property to
	 * true prevents this and requires different machines for redundant copies.
	 */
	private Boolean enforceUniqueHost;

	/**
	 * A space-separated list of the valid SSL ciphers for WAN gateway
	 * connections. A setting of 'any' uses any ciphers that are enabled
	 * by default in the configured JSSE provider. If this property
	 * is not set, then GemFire uses the value of cluster-ssl-ciphers
	 * to determine which SSL ciphers are used for WAN connections.
	 */
	private String gatewaySslCiphers;

	/**
	 * Enables or disables SSL for WAN gateway connections. If this property
	 * is not set, then GemFire uses the value of cluster-ssl-enabled to
	 * determine whether JMX connections use SSL.
	 */
	private Boolean gatewaySslEnabled;

	/**
	 * A space-separated list of the valid SSL protocols for WAN gateway
	 * connections. A setting of 'any' uses any protocol that is enabled
	 * by default in the configured JSSE provider. If this property is
	 * not set, then GemFire uses the value of cluster-ssl-protocols to
	 * determine which SSL protocols are used by WAN connections.
	 */
	private String gatewaySslProtocols;

	/**
	 * Boolean indicating whether to require authentication for WAN
	 * gateway connections. If this property is not set, then GemFire
	 * uses the value of cluster-ssl-require-authentication to
	 * determine whether WAN connections require authentication.
	 */
	private Boolean gatewaySslRequireAuthentication;

	/**
	 * Defines the list of groups that this member belongs to. Use
	 * commas to separate group names. Note that anything defined
	 * by the roles gemfire property will also be considered a group.
	 */
	private String groups;

	/**
	 * If set, then the GemFire member binds the embedded HTTP
	 * service to the specified address. If this property is not
	 * set but the HTTP service is enabled using http-service-port,
	 * then GemFire binds the HTTP service to the member's local
	 * address. Used by the GemFire Pulse Web application and
	 * the developer REST API service.
	 */
	private String httpServiceBindAddress;

	/**
	 * If non-zero, then GemFire starts an embedded HTTP service
	 * that listens on this port. The HTTP service is used to
	 * host the GemFire Pulse Web application and the development
	 * REST API service. If you are hosting the Pulse web app on
	 * your own Web server and are not using the development REST
	 * API service, then disable this embedded HTTP service by
	 * setting this property to zero. Ignored if jmx-manager and
	 * start-dev-rest-api are both set to false.
	 */
	private Integer httpServicePort;

	/**
	 * A space separated list of the SSL cipher suites to enable.
	 * Those listed must be supported by the available providers.
	 */
	private String httpServiceSslCiphers;

	/**
	 * Specifies if the HTTP service is started with separate ssl
	 * configuration. If not specified, then the global property
	 * cluster-ssl-enabled (and its other related properties)
	 * are used to create server socket.
 	 */
	private Boolean httpServiceSslEnabled;

	/**
	 * A space separated list of the SSL protocols to enable. Those
	 * listed must be supported by the available providers.
	 */
	private String httpServiceSslProtocols;

	/**
	 * Boolean indicating whether to require authentication for HTTP
	 * service connections. If this property is not set, then GemFire
	 * uses the value of cluster-ssl-require-authentication to
	 * determine whether HTTP service connections require authentication.
	 */
	private Boolean httpServiceSslRequireAuthentication;

	/**
	 * If true then this member is willing to be a JMX Manager. All the
	 * other JMX Manager properties will be used when it does become a
	 * manager. If this property is false then all other
	 * jmx-manager-* properties are ignored.
	 */
	private Boolean jmxManager;

	/**
	 * By default the JMX Manager will allow full access to all mbeans
	 * by any client. If this property is set to the name of a file
	 * then it can restrict clients to only being able to read MBeans;
	 * they will not be able to modify MBeans. The access level can be
	 * configured differently in this file for each user name defined
	 * in the password file. For more information about the format of
	 * this file see Oracle's documentation of the
	 * com.sun.management.jmxremote.access.file system property.
	 * Ignored if jmx-manager is false or if jmx-manager-port is zero.
	 */
	private String jmxManagerAccessFile;

	/**
	 * By default the jmx-manager (when configured with a port) will
	 * listen on all the local host's addresses. You can use this
	 * property to configure what IP address or host name the JMX Manager
	 * will listen on for non-HTTP connections. Ignored if JMX Manager
	 * is false or jmx-manager-port is zero.
	 */
	private String jmxManagerBindAddress;

	/**
	 * Lets you control what hostname will be given to clients that
	 * ask the locator for the location of a JMX Manager. By default
	 * the IP address that the jmx-manager reports is used. But for
	 * clients on a different network this property allows you to
	 * configure a different hostname that will be given to clients.
	 * Ignored if jmx-manager is false or jmx-manager-port is zero.
	 */
	private String jmxManagerHostnameForClients;

	/**
	 * By default the JMX Manager will allow clients without credentials
	 * to connect. If this property is set to the name of a file then
	 * only clients that connect with credentials that match an entry
	 * in this file will be allowed. Most JVMs require that the file is
	 * only readable by the owner. For more information about the format
	 * of this file see Oracle's documentation of the
	 * com.sun.management.jmxremote.password.file system property.
	 * Ignored if jmx-manager is false or if jmx-manager-port is zero.
	 */
	private String jmxManagerPasswordFile;

	/**
	 * 	The port this JMX Manager will listen to for client connections.
	 * 	If this property is set to zero then GemFire will not allow
	 * 	remote client connections but you can alternatively use the
	 * 	standard system properties supported by the JVM for configuring
	 * 	access from remote JMX clients. Ignored if jmx-manager is false.
	 */
	private Integer jmxManagerPort;

	/**
	 * Enables or disables SSL for connections to the JMX Manager. If true
	 * and jmx-manager-port is not zero, then the JMX Manager will only
	 * accept SSL connections. If this property is not set, then GemFire
	 * uses the value of cluster-ssl-enabled to determine whether JMX
	 * connections should use SSL.
	 */
	private Boolean jmxManagerSslEnabled;

	/**
	 * A space-separated list of the valid SSL ciphers for JMX manager
	 * connections. A setting of 'any' uses any ciphers that are enabled
	 * by default in the configured JSSE provider. If this property is not
	 * set, then GemFire uses the value of cluster-ssl-ciphers to determine
	 * which SSL ciphers are used for JMX connections.
	 */
	private String jmxManagerSslCiphers;

	/**
	 * A space-separated list of the valid SSL protocols for JMX manager
	 * connections. A setting of 'any' uses any protocol that is enabled
	 * by default in the configured JSSE provider. If this property is not
	 * set, then GemFire uses the value of cluster-ssl-protocols to
	 * determine which SSL protocols are used for JMX connections.
	 */
	private String jmxManagerSslProtocols;

	/**
	 * Boolean indicating whether to require authentication for JMX Manager
	 * connections. If this property is not set, then GemFire uses the value
	 * of cluster-ssl-require-authentication to determine whether JMX
	 * connections require authentication.
	 */
	private Boolean jmxManagerSslRequireAuthentication;

	/**
	 * If true then this member will start a jmx manager when it creates a
	 * cache. Management tools like gfsh can be configured to connect to the
	 * jmx-manager. In most cases you should not set this because a jmx manager
	 * will automatically be started when needed on a member that sets
	 * "jmx-manager" to true. Ignored if jmx-manager is false.
	 */
	private Boolean jmxManagerStart;

	/**
	 * The rate, in milliseconds, at which this member will push updates to
	 * any JMX Managers. Currently this value should be greater than or
	 * equal to the statistic-sample-rate. Setting this value too high
	 * will cause stale values to be seen by gfsh and GemFire Pulse.
	 */
	@Min(0)
	private Integer jmxManagerUpdateRate;

	/**
	 * Setting this property to "true" causes loading of cluster configuration
	 * from "cluster_config" directory in the locator. This property is only
	 * applicable to dedicated locators that have "enable-cluster-configuration"
	 * set to true.
	 */
	private Boolean loadClusterConfigurationFromDir;

	/**
	 * The number of seconds that a member should wait for a locator to start
	 * if a locator is not available when attempting to join the distributed
	 * system. Use this setting when you are starting locators and peers all at
	 * once. This timeout allows peers to wait for the locators to finish
	 * starting up before attempting to join the distributed system.
	 */
	private Integer locatorWaitTime;

	/**
	 * The list of locators used by system members. The list must be configured
	 * consistently for every member of the distributed system. If the list
	 * is empty, locators are not used.
	 * <p>
	 * For each locator, provide a host name and/or address (separated by ‘@’,
	 * if you use both), followed by a port number in brackets. Examples:
	 * <pre>
	 * locators=address1[port1],address2[port2]
	 * locators=hostName1@address1[port1],hostName2@address2[port2]
	 * locators=hostName1[port1],hostName2[port2]
	 * </pre>
	 * Note: On multi-homed hosts, this last notation will use the default
	 * address. If you use bind addresses for your locators, explicitly
	 * specify the addresses in the locators list—do not use just the hostname.
	 * <p>
	 * If you have values specified for the locators property, the
	 * mcast-port property defaults to 0.
	 * <p>
	 * Note: If you specify invalid DNS hostnames in this property, any locators
	 * or servers started with gfsh will not produce log files. Make sure you
	 * provide valid DNS hostnames before starting the locator or server with gfsh.
	 */
	private String locators;

	/**
	 * Maximum size in megabytes of all inactive log files combined. If this
	 * limit is exceeded, inactive log files are deleted, oldest first, until
	 * the total size is within the limit. If set to zero, disk space use is
	 * unlimited.
	 */
	private Integer logDiskSpaceLimit;

	/**
	 * File to which a running system member writes log messages. If set to
	 * null, the default is used.
	 * Each member type has its own default output:
	 * <ul>
	 *     <li>application: standard out</li>
	 *     <li>locator: <locator_name>.log</li>
	 *     <li>server: <server_name>.log</li>
	 * </ul>
	 */
	private String logFile;

	/**
	 * Maximum size in megabytes of a log file before it is closed and
	 * logging rolls on to a new (child) log file. If set to 0, log
	 * rolling is disabled.
	 */
	private Integer logFileSizeLimit;

	/**
	 * Level of detail of the messages written to the system member’s log.
	 * Setting log-level to one of the ordered levels causes all messages
	 * of that level and greater severity to be printed.
	 * <p>
	 * Valid values from lowest to highest are:
	 * fine, config, info, warning, error, severe, and none.
	 */
	private String logLevel;

	/**
	 * Maximum number of milliseconds to wait for the distributed system
	 * to reconnect on each reconnect attempt.
	 */
	private Integer maxWaitTimeReconnect;

	/**
	 * Address used to discover other members of the distributed system.
	 * Only used if mcast-port is non-zero. This attribute must be consistent
	 * across the distributed system.
	 * <p>
	 * Note: Select different multicast addresses and different ports for
	 * different distributed systems. Do not just use different addresses.
	 * Some operating systems may not keep communication separate between
	 * systems that use unique addresses but the same port number.
	 * <p>
	 * This default multicast address was assigned by IANA
	 * (https://www.iana.org/assignments/multicast-addresses). Consult the
	 * IANA chart when selecting another multicast address to use with GemFire.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication and does
	 * not apply to client/server or multi-site communication. If multicast
	 * is enabled, distributed regions use it for most communication.
	 * Partitioned regions only use multicast for a few purposes, and mainly
	 * use either TCP or UDP unicast.
	 */
	private String mcastAddress;

	/**
	 * Tuning property for flow-of-control protocol for unicast and multicast
	 * no-ack UDP messaging. Compound property made up of three settings
	 * separated by commas: byteAllowance, rechargeThreshold, and rechargeBlockMs.
	 * Valid values range from these minimums: 10000,0.1,500 to these maximums:
	 * no_maximum ,0.5,60000.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication, generally
	 * between distributed regions.
	 */
	private String mcastFlowControl;

	/**
	 * Port used, along with the mcast-address, for multicast communication
	 * with other members of the distributed system. If zero, multicast
	 * is disabled for member discovery and distribution.
	 * <p>
	 * Note: Select different multicast addresses and ports for different
	 * distributed systems. Do not just use different addresses. Some
	 * operating systems may not keep communication separate between
	 * systems that use unique addresses but the same port number.
	 * <p>
	 * Valid values are in the range 0..65535.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication and does
	 * not apply to client/server or multi-site communication.
	 * If you have values specified for the locators property, the
	 * mcast-port property defaults to 0.
	 */
	@Min(0)
	@Max(65535)
	private Integer mcastPort;

	/**
	 * Size of the socket buffer used for incoming multicast transmissions.
	 * You should set this high if there will be high volumes of messages.
	 * Valid values are in the range 2048.. OS_maximum.
	 * <p>
	 * Note: The default setting is higher than the default OS maximum buffer
	 * size on Unix, which should be increased to at least 1 megabyte to
	 * provide high-volume messaging on Unix systems.
	 * <p>
	 * This setting controls only peer-to-peer communication and does not apply
	 * to client/server or multi-site communication.
	 */
	private Integer mcastRecvBufferSize;

	/**
	 * The size of the socket buffer used for outgoing multicast transmissions.
	 * Valid values are in the range 2048.. OS_maximum.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication and does not
	 * apply to client/server or multi-site communication.
	 */
	private Integer mcastSendBufferSize;

	/**
	 * How far multicast messaging goes in your network. Lower settings may
	 * improve system performance. A setting of 0 constrains multicast
	 * messaging to the machine.
	 * <p>
	 * Note: This setting controls only peer-to-peer communication and does
	 * not apply to client/server or multi-site communication.
	 */
	private Integer mcastTtl;

	/**
	 * GemFire uses the member-timeout server configuration, specified in
	 * milliseconds, to detect the abnormal termination of members. The
	 * configuration setting is used in two ways:
	 * <ol>
	 *     <li>First it is used during the UDP heartbeat detection process.
	 *     When a member detects that a heartbeat datagram is missing from
	 *     the member that it is monitoring after the time interval of 2 * the
	 *     value of member-timeout, the detecting member attempts to form a
	 *     TCP/IP stream-socket connection with the monitored member as
	 *     described in the next case.</li>
	 *     <li>The property is then used again during the TCP/IP stream-socket
	 *     connection. If the suspected process does not respond to the are
	 *     you alive datagram within the time period specified in member-timeout,
	 *     the membership coordinator sends out a new membership view that
	 *     notes the member's failure.</li>
	 * </ol>
	 * Valid values are in the range 1000..600000.
	 */
	private Integer memberTimeout;

	/**
	 * The range of ports available for unicast UDP messaging and for TCP
	 * failure detection. This is specified as two integers separated by
	 * a minus sign. Different members can use different ranges.
	 * GemFire randomly chooses at least two unique integers from this
	 * range for the member, one for UDP unicast messaging and the other
	 * for TCP failure detection messaging. If tcp-port is configured to 0,
	 * it will also randomly select a port from this range for TCP sockets
	 * used for peer-to-peer communication only.
	 * <p>
	 * Therefore, the specified range must include at least three available
	 * port numbers (UDP, FD_SOCK, and TCP DirectChannel).
	 * <p>
	 * The system uniquely identifies the member using the combined host
	 * IP address and UDP port number.
	 * <p>
	 * You may want to restrict the range of ports that GemFire uses so
	 * the product can run in an environment where routers only allow
	 * traffic on certain ports.
	 */
	private String membershipPortRange;

	/**
	 * If specified and is non-zero, sets the port number for an embedded
	 * Gemcached server and starts the Gemcached server.
	 */
	private Integer memcachedPort;

	/**
	 * Sets the protocol used by an embedded Gemcached server. Valid values
	 * are BINARY and ASCII. If you omit this property, the ASCII protocol is used.
	 */
	private String memcachedProtocol;

	/**
	 * Symbolic name used to identify this system member.
	 */
	private String name;

	/**
	 * Defines this member's redundancy zone. Used to separate member's into
	 * different groups for satisfying partitioned region redundancy. If
	 * this property is set, GemFire will not put redundant copies of data
	 * in members with the same redundancy zone setting.
	 */
	private String redundancyZone;

	/**
	 * Used to configure the locators that a cluster will use in order to
	 * connect to a remote site in a multi-site (WAN) configuration.
	 * To use locators in a WAN configuration, you must specify a unique
	 * distributed system ID (distributed-system-id) for the local cluster
	 * and remote locator(s) for the remote clusters to which you will connect.
	 * <p>
	 * For each remote locator, provide a host name and/or address
	 * (separated by ‘@’, if you use both), followed by a port number in brackets. Examples:
	 * <pre>
	 *     remote-locators=address1[port1],address2[port2]
	 *     remote-locators=hostName1@address1[port1],hostName2@address2[port2]
	 *     remote-locators=hostName1[port1],hostName2[port2]
	 * </pre>
	 */
	private String remoteLocators;

	/**
	 * When this property is set to true, the primary server drops unresponsive
	 * clients from all secondaries and itself. Clients are deemed unresponsive
	 * when their messaging queues become full on the server. While a client's
	 * queue is full, puts that would add to the queue block on the server.
	 */
	private Boolean removeUnresponsiveClient;

	/**
	 * Used for authorization. Static creation method returning an AccessControl
	 * object, which determines authorization of client-server cache operations.
	 * This specifies the callback that should be invoked in the pre-operation
	 * phase, which is when the request for the operation is received from the client.
	 */
	private String securityClientAccessor;

	/**
	 * Used for authorization. The callback that should be invoked in the
	 * post-operation phase, which is when the operation has completed on
	 * the server but before the result is sent to the client. The
	 * post-operation callback is also invoked for the updates that are
	 * sent from server to client through the notification channel.
	 */
	private String securityClientAccessorPp;

	/**
	 * Used for authentication. Static creation method returning an
	 * AuthInitialize object, which obtains credentials for peers in
	 * a distributed system. The obtained credentials should be
	 * acceptable to the Authenticator specified through the
	 * security-peer-authenticator property on the peers.
	 */
	private String securityClientAuthInit;

	/**
	 * Used for authentication. Static creation method returning an
	 * Authenticator object, which is used by a peer to verify the
	 * credentials of the connecting peer.
	 */
	private String securityClientAuthenticator;

	/**
	 * Used for authentication. For secure transmission of sensitive
	 * credentials like passwords, you can encrypt the credentials
	 * using the Diffie-Hellman key exchange algorithm. Do this by
	 * setting the security-client-dhalgo system property on the clients
	 * to the name of a valid symmetric key cipher supported by the JDK.
	 */
	private String securityClientDhalgo;

	/**
	 * Used with authentication. The log file for security log messages.
	 * If not specified, the member's regular log file is used.
	 */
	private String securityLogFile;

	/**
	 * Used with authentication. Logging level detail for security log messages.
	 * <p>
	 * Valid values from lowest to highest are:
	 * fine, config, info, warning, error, severe, and none.
	 */
	private String securityLogLevel;

	/**
	 * Used with authentication. Static creation method returning an
	 * AuthInitialize object, which obtains credentials for peers in a
	 * distributed system. The obtained credentials should be acceptable
	 * to the Authenticator specified through the security-peer-authenticator
	 * property on the peers.
	 */
	private String securityPeerAuthInit;

	/**
	 * Used with authentication. Static creation method returning an
	 * Authenticator object, which is used by a peer to verify the
	 * credentials of the connecting peer.
	 */
	private String securityPeerAuthenticator;

	/**
	 * Used with authentication. Timeout in milliseconds used by a peer
	 * to verify membership of an unknown authenticated peer requesting
	 * a secure connection.
	 */
	private Integer securityPeerVerifymemberTimeout;

	/**
	 * Relevant only for multi-homed hosts - machines with multiple
	 * network interface cards. Network adapter card a GemFire
	 * server binds to for client/server communication. You can
	 * use this to separate the server’s client/server communication
	 * from its peer-to-peer communication, spreading the traffic load.
	 * <p>
	 * This is a machine-wide attribute used for communication with
	 * clients in client/server and multi-site installations. This
	 * setting has no effect on locator configuration.
	 * <p>
	 * Specify the IP address, not the hostname, because each network
	 * card may not have a unique hostname.
	 * <p>
	 * An empty string causes the servers to listen on the same card
	 * used for peer-to-peer communication. This is either the
	 * bind-address or, if that is not set, the machine’s default card.
	 */
	private String serverBindAddress;

	/**
	 * A space-separated list of the valid SSL ciphers for client/server
	 * connections. A setting of 'any' uses any ciphers that are
	 * enabled by default in the configured JSSE provider. If this
	 * property is not set, then GemFire uses the value of cluster-ssl-ciphers
	 * to determine which ciphers are used for client connections.
	 */
	private String serverSslCiphers;

	/**
	 * Enables or disables SSL for client/server connections. If this property
	 * is not set, then GemFire uses the value of cluster-ssl-enabled to
	 * determine whether client connections use SSL.
	 */
	private Boolean serverSslEnabled;

	/**
	 * A space-separated list of the valid SSL protocols for client/server
	 * connections. A setting of 'any' uses any protocol that is enabled
	 * by default in the configured JSSE provider. If this property is not set,
	 * then GemFire uses the value of cluster-ssl-protocols to determine which
	 * SSL protocols are used for client connections.
	 */
	private String serverSslProtocols;

	/**
	 * Boolean indicating whether to require authentication for client/server
	 * connections. If this property is not set, then GemFire uses the value
	 * of cluster-ssl-require-authentication to determine whether client
	 * connections require authentication.
	 */
	private Boolean serverSslRequireAuthentication;

	/**
	 * Receive buffer sizes in bytes of the TCP/IP connections used for data
	 * transmission. To minimize the buffer size allocation needed for
	 * distributing large, serializable messages, the messages are sent
	 * in chunks. This setting determines the size of the chunks. Larger
	 * buffers can handle large messages more quickly, but take up more memory.
	 */
	private Integer socketBufferSize;

	/**
	 * Time, in milliseconds, a thread can have exclusive access to a socket
	 * it is not actively using. A value of zero causes socket leases to
	 * never expire. This property is ignored if conserve-sockets is true.
	 * <p>
	 * Valid values are in the range 0..600000.
	 */
	@Min(0)
	@Max(600000)
	private Integer socketLeaseTime;

	/**
	 * If set to true, then the developer REST API service will be started
	 * when cache is created. REST service can be configured using
	 * http-service-port and http-service-bind-address properties.
	 */
	private Boolean startDevRestApi;

	/**
	 * If set, automatically starts a locator in the current process when
	 * the member connects to the distributed system and stops the
	 * locator when the member disconnects.
	 * <p>
	 * To use, specify the locator with an optional address or host
	 * specification and a required port number, in one of these formats:
	 * <pre>
	 *     start-locator=address[port1]
	 *     start-locator=port1
	 * </pre>
	 * If you only specify the port, the address assigned to the member
	 * is used for the locator. If not already there, this locator is
	 * automatically added to the list of locators in this set of
	 * gemfire properties.
	 */
	private String startLocator;

	/**
	 * The file to which the running system member writes statistic
	 * samples. For example: "StatisticsArchiveFile.gfs". An empty string
	 * disables archiving. Adding .gz suffix to the file name causes
	 * it to be compressed.
	 */
	private String statisticArchiveFile;

	/**
	 * How often to sample statistics, in milliseconds.
	 * Valid values are in the range 100..60000.
	 */
	private Integer statisticSampleRate;

	/**
	 * Whether to collect and archive statistics on the member.
	 * <p>
	 * Statistics sampling provides valuable information for ongoing
	 * system tuning and troubleshooting purposes. Sampling statistics
	 * at the default sample rate does not impact system performance.
	 * We recommend enabling statistics sampling in production environments.
	 * <p>
	 * Note: This setting does not apply to partitioned regions, where
	 * statistics are always enabled.
	 */
	private Boolean statisticSamplingEnabled;

	/**
	 * The TCP port to listen on for cache communications. If set to zero,
	 * the operating system selects an available port. Each process on a
	 * machine must have its own TCP port. Note that some operating systems
	 * restrict the range of ports usable by non-privileged users, and using
	 * restricted port numbers can cause runtime errors in GemFire startup.
	 * <p>
	 * Valid values are in the range 0..65535.
	 */
	@Min(0)
	@Max(65535)
	private Integer tcpPort;

	/**
	 * The number of tombstones that can accumulate before the GemFire
	 * member triggers garbage collection for tombstones.
	 */
	private Integer tombstoneGcThreshold;

	/**
	 * Maximum fragment size, in bytes, for transmission over UDP unicast
	 * or multicast sockets. Smaller messages are combined, if possible,
	 * for transmission up to the fragment size setting.
	 * <p>
	 * Valid values are in the range 1000..60000.
	 */
	private Integer udpFragmentSize;

	/**
	 * The size of the socket buffer used for incoming UDP point-to-point
	 * transmissions. If disable-tcp is false, a reduced buffer size of
	 * 65535 is used by default.
	 * <p>
	 * The default setting of 1048576 is higher than the default OS
	 * maximum buffer size on Unix, which should be increased to at
	 * least 1 megabyte to provide high-volume messaging on Unix systems.
	 * <p>
	 * Valid values are in the range 2048.. OS_maximum.
	 */
	private Integer udpRecvBufferSize;

	/**
	 * The size of the socket buffer used for outgoing UDP point-to-point
	 * transmissions.
	 * <p>
	 * Valid values are in the range 2048..OS_maximum.
	 */
	private Integer udpSendBufferSize;

	/**
	 * This property is only applicable for data members (non-client and
	 * non -ocator). A value of "true" causes a member to request and use
	 * the configuration from cluster configuration services running on
	 * dedicated locators. Setting this property to "false" causes a
	 * member to not request the configuration from the configuration
	 * services running on the locator(s).
	 */
	private Boolean useClusterConfiguration;

	/**
	 * A comma separated list of Java packages that contain classes
	 * implementing the CommandMarker interface. Matching classes will
	 * be loaded when the VM starts and will be available in the
	 * GFSH command-line utility.
	 */
	private String userCommandPackages;

	public Integer getAckSevereAlertThreshold() {
		return ackSevereAlertThreshold;
	}

	public GemfireProperties setAckSevereAlertThreshold(Integer ackSevereAlertThreshold) {
		this.ackSevereAlertThreshold = ackSevereAlertThreshold;
		return this;
	}

	public Integer getAckWaitThreshold() {
		return ackWaitThreshold;
	}

	public GemfireProperties setAckWaitThreshold(Integer ackWaitThreshold) {
		this.ackWaitThreshold = ackWaitThreshold;
		return this;
	}

	public Integer getArchiveDiskSpaceLimit() {
		return archiveDiskSpaceLimit;
	}

	public GemfireProperties setArchiveDiskSpaceLimit(Integer archiveDiskSpaceLimit) {
		this.archiveDiskSpaceLimit = archiveDiskSpaceLimit;
		return this;
	}

	public Integer getArchiveFileSizeLimit() {
		return archiveFileSizeLimit;
	}

	public GemfireProperties setArchiveFileSizeLimit(Integer archiveFileSizeLimit) {
		this.archiveFileSizeLimit = archiveFileSizeLimit;
		return this;
	}

	public Integer getAsyncDistributionTimeout() {
		return asyncDistributionTimeout;
	}

	public GemfireProperties setAsyncDistributionTimeout(Integer asyncDistributionTimeout) {
		this.asyncDistributionTimeout = asyncDistributionTimeout;
		return this;
	}

	public Integer getAsyncMaxQueueSize() {
		return asyncMaxQueueSize;
	}

	public GemfireProperties setAsyncMaxQueueSize(Integer asyncMaxQueueSize) {
		this.asyncMaxQueueSize = asyncMaxQueueSize;
		return this;
	}

	public Integer getAsyncQueueTimeout() {
		return asyncQueueTimeout;
	}

	public GemfireProperties setAsyncQueueTimeout(Integer asyncQueueTimeout) {
		this.asyncQueueTimeout = asyncQueueTimeout;
		return this;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public GemfireProperties setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
		return this;
	}

	public String getClusterConfigurationDir() {
		return clusterConfigurationDir;
	}

	public GemfireProperties setClusterConfigurationDir(String clusterConfigurationDir) {
		this.clusterConfigurationDir = clusterConfigurationDir;
		return this;
	}

	public String getClusterSslCiphers() {
		return clusterSslCiphers;
	}

	public GemfireProperties setClusterSslCiphers(String clusterSslCiphers) {
		this.clusterSslCiphers = clusterSslCiphers;
		return this;
	}

	public Boolean getClusterSslEnabled() {
		return clusterSslEnabled;
	}

	public GemfireProperties setClusterSslEnabled(Boolean clusterSslEnabled) {
		this.clusterSslEnabled = clusterSslEnabled;
		return this;
	}

	public String getClusterSslKeystore() {
		return clusterSslKeystore;
	}

	public GemfireProperties setClusterSslKeystore(String clusterSslKeystore) {
		this.clusterSslKeystore = clusterSslKeystore;
		return this;
	}

	public String getGatewaySslKeystore() {
		return gatewaySslKeystore;
	}

	public GemfireProperties setGatewaySslKeystore(String gatewaySslKeystore) {
		this.gatewaySslKeystore = gatewaySslKeystore;
		return this;
	}

	public String getHttpServiceSslKeystore() {
		return httpServiceSslKeystore;
	}

	public GemfireProperties setHttpServiceSslKeystore(String httpServiceSslKeystore) {
		this.httpServiceSslKeystore = httpServiceSslKeystore;
		return this;
	}

	public String getJmxManagerSslKeystore() {
		return jmxManagerSslKeystore;
	}

	public GemfireProperties setJmxManagerSslKeystore(String jmxManagerSslKeystore) {
		this.jmxManagerSslKeystore = jmxManagerSslKeystore;
		return this;
	}

	public String getServerSslKeystore() {
		return serverSslKeystore;
	}

	public GemfireProperties setServerSslKeystore(String serverSslKeystore) {
		this.serverSslKeystore = serverSslKeystore;
		return this;
	}

	public String getClusterSslKeystorePassword() {
		return clusterSslKeystorePassword;
	}

	public GemfireProperties setClusterSslKeystorePassword(String clusterSslKeystorePassword) {
		this.clusterSslKeystorePassword = clusterSslKeystorePassword;
		return this;
	}

	public String getGatewaySslKeystorePassword() {
		return gatewaySslKeystorePassword;
	}

	public GemfireProperties setGatewaySslKeystorePassword(String gatewaySslKeystorePassword) {
		this.gatewaySslKeystorePassword = gatewaySslKeystorePassword;
		return this;
	}

	public String getHttpServiceSslKeystorePassword() {
		return httpServiceSslKeystorePassword;
	}

	public GemfireProperties setHttpServiceSslKeystorePassword(String httpServiceSslKeystorePassword) {
		this.httpServiceSslKeystorePassword = httpServiceSslKeystorePassword;
		return this;
	}

	public String getJmxManagerSslKeystorePassword() {
		return jmxManagerSslKeystorePassword;
	}

	public GemfireProperties setJmxManagerSslKeystorePassword(String jmxManagerSslKeystorePassword) {
		this.jmxManagerSslKeystorePassword = jmxManagerSslKeystorePassword;
		return this;
	}

	public String getServerSslKeystorePassword() {
		return serverSslKeystorePassword;
	}

	public GemfireProperties setServerSslKeystorePassword(String serverSslKeystorePassword) {
		this.serverSslKeystorePassword = serverSslKeystorePassword;
		return this;
	}

	public String getClusterSslKeystoreType() {
		return clusterSslKeystoreType;
	}

	public GemfireProperties setClusterSslKeystoreType(String clusterSslKeystoreType) {
		this.clusterSslKeystoreType = clusterSslKeystoreType;
		return this;
	}

	public String getGatewaySslKeystoreType() {
		return gatewaySslKeystoreType;
	}

	public GemfireProperties setGatewaySslKeystoreType(String gatewaySslKeystoreType) {
		this.gatewaySslKeystoreType = gatewaySslKeystoreType;
		return this;
	}

	public String getHttpServiceSslKeystoreType() {
		return httpServiceSslKeystoreType;
	}

	public GemfireProperties setHttpServiceSslKeystoreType(String httpServiceSslKeystoreType) {
		this.httpServiceSslKeystoreType = httpServiceSslKeystoreType;
		return this;
	}

	public String getJmxManagerSslKeystoreType() {
		return jmxManagerSslKeystoreType;
	}

	public GemfireProperties setJmxManagerSslKeystoreType(String jmxManagerSslKeystoreType) {
		this.jmxManagerSslKeystoreType = jmxManagerSslKeystoreType;
		return this;
	}

	public String getServerSslKeystoreType() {
		return serverSslKeystoreType;
	}

	public GemfireProperties setServerSslKeystoreType(String serverSslKeystoreType) {
		this.serverSslKeystoreType = serverSslKeystoreType;
		return this;
	}

	public String getClusterSslProtocols() {
		return clusterSslProtocols;
	}

	public GemfireProperties setClusterSslProtocols(String clusterSslProtocols) {
		this.clusterSslProtocols = clusterSslProtocols;
		return this;
	}

	public Boolean getClusterSslRequireAuthentication() {
		return clusterSslRequireAuthentication;
	}

	public GemfireProperties setClusterSslRequireAuthentication(Boolean clusterSslRequireAuthentication) {
		this.clusterSslRequireAuthentication = clusterSslRequireAuthentication;
		return this;
	}

	public String getClusterSslTruststore() {
		return clusterSslTruststore;
	}

	public GemfireProperties setClusterSslTruststore(String clusterSslTruststore) {
		this.clusterSslTruststore = clusterSslTruststore;
		return this;
	}

	public String getGatewaySslTruststore() {
		return gatewaySslTruststore;
	}

	public GemfireProperties setGatewaySslTruststore(String gatewaySslTruststore) {
		this.gatewaySslTruststore = gatewaySslTruststore;
		return this;
	}

	public String getHttpServiceSslTruststore() {
		return httpServiceSslTruststore;
	}

	public GemfireProperties setHttpServiceSslTruststore(String httpServiceSslTruststore) {
		this.httpServiceSslTruststore = httpServiceSslTruststore;
		return this;
	}

	public String getJmxManagerSslTruststore() {
		return jmxManagerSslTruststore;
	}

	public GemfireProperties setJmxManagerSslTruststore(String jmxManagerSslTruststore) {
		this.jmxManagerSslTruststore = jmxManagerSslTruststore;
		return this;
	}

	public String getServerSslTruststore() {
		return serverSslTruststore;
	}

	public GemfireProperties setServerSslTruststore(String serverSslTruststore) {
		this.serverSslTruststore = serverSslTruststore;
		return this;
	}

	public String getClusterSslTruststorePassword() {
		return clusterSslTruststorePassword;
	}

	public GemfireProperties setClusterSslTruststorePassword(String clusterSslTruststorePassword) {
		this.clusterSslTruststorePassword = clusterSslTruststorePassword;
		return this;
	}

	public String getGatewaySslTruststorePassword() {
		return gatewaySslTruststorePassword;
	}

	public GemfireProperties setGatewaySslTruststorePassword(String gatewaySslTruststorePassword) {
		this.gatewaySslTruststorePassword = gatewaySslTruststorePassword;
		return this;
	}

	public String getHttpServiceSslTruststorePassword() {
		return httpServiceSslTruststorePassword;
	}

	public GemfireProperties setHttpServiceSslTruststorePassword(String httpServiceSslTruststorePassword) {
		this.httpServiceSslTruststorePassword = httpServiceSslTruststorePassword;
		return this;
	}

	public String getJmxManagerSslTruststorePassword() {
		return jmxManagerSslTruststorePassword;
	}

	public GemfireProperties setJmxManagerSslTruststorePassword(String jmxManagerSslTruststorePassword) {
		this.jmxManagerSslTruststorePassword = jmxManagerSslTruststorePassword;
		return this;
	}

	public String getServerSslTruststorePassword() {
		return serverSslTruststorePassword;
	}

	public GemfireProperties setServerSslTruststorePassword(String serverSslTruststorePassword) {
		this.serverSslTruststorePassword = serverSslTruststorePassword;
		return this;
	}

	public String getConflateEvents() {
		return conflateEvents;
	}

	public GemfireProperties setConflateEvents(String conflateEvents) {
		this.conflateEvents = conflateEvents;
		return this;
	}

	public Boolean getConserveSockets() {
		return conserveSockets;
	}

	public GemfireProperties setConserveSockets(Boolean conserveSockets) {
		this.conserveSockets = conserveSockets;
		return this;
	}

	public Boolean getDeltaPropagation() {
		return deltaPropagation;
	}

	public GemfireProperties setDeltaPropagation(Boolean deltaPropagation) {
		this.deltaPropagation = deltaPropagation;
		return this;
	}

	public String getDeployWorkingDir() {
		return deployWorkingDir;
	}

	public GemfireProperties setDeployWorkingDir(String deployWorkingDir) {
		this.deployWorkingDir = deployWorkingDir;
		return this;
	}

	public Boolean getDisableAutoReconnect() {
		return disableAutoReconnect;
	}

	public GemfireProperties setDisableAutoReconnect(Boolean disableAutoReconnect) {
		this.disableAutoReconnect = disableAutoReconnect;
		return this;
	}

	public Boolean getDisableTcp() {
		return disableTcp;
	}

	public GemfireProperties setDisableTcp(Boolean disableTcp) {
		this.disableTcp = disableTcp;
		return this;
	}

	public Integer getDistributedSystemId() {
		return distributedSystemId;
	}

	public GemfireProperties setDistributedSystemId(Integer distributedSystemId) {
		this.distributedSystemId = distributedSystemId;
		return this;
	}

	public String getDurableClientId() {
		return durableClientId;
	}

	public GemfireProperties setDurableClientId(String durableClientId) {
		this.durableClientId = durableClientId;
		return this;
	}

	public Integer getDurableClientTimeout() {
		return durableClientTimeout;
	}

	public GemfireProperties setDurableClientTimeout(Integer durableClientTimeout) {
		this.durableClientTimeout = durableClientTimeout;
		return this;
	}

	public Boolean getEnableNetworkPartitionDetection() {
		return enableNetworkPartitionDetection;
	}

	public GemfireProperties setEnableNetworkPartitionDetection(Boolean enableNetworkPartitionDetection) {
		this.enableNetworkPartitionDetection = enableNetworkPartitionDetection;
		return this;
	}

	public Boolean getEnableClusterConfiguration() {
		return enableClusterConfiguration;
	}

	public GemfireProperties setEnableClusterConfiguration(Boolean enableClusterConfiguration) {
		this.enableClusterConfiguration = enableClusterConfiguration;
		return this;
	}

	public Boolean getEnableTimeStatistics() {
		return enableTimeStatistics;
	}

	public GemfireProperties setEnableTimeStatistics(Boolean enableTimeStatistics) {
		this.enableTimeStatistics = enableTimeStatistics;
		return this;
	}

	public Boolean getEnforceUniqueHost() {
		return enforceUniqueHost;
	}

	public GemfireProperties setEnforceUniqueHost(Boolean enforceUniqueHost) {
		this.enforceUniqueHost = enforceUniqueHost;
		return this;
	}

	public String getGatewaySslCiphers() {
		return gatewaySslCiphers;
	}

	public GemfireProperties setGatewaySslCiphers(String gatewaySslCiphers) {
		this.gatewaySslCiphers = gatewaySslCiphers;
		return this;
	}

	public Boolean getGatewaySslEnabled() {
		return gatewaySslEnabled;
	}

	public GemfireProperties setGatewaySslEnabled(Boolean gatewaySslEnabled) {
		this.gatewaySslEnabled = gatewaySslEnabled;
		return this;
	}

	public String getGatewaySslProtocols() {
		return gatewaySslProtocols;
	}

	public GemfireProperties setGatewaySslProtocols(String gatewaySslProtocols) {
		this.gatewaySslProtocols = gatewaySslProtocols;
		return this;
	}

	public Boolean getGatewaySslRequireAuthentication() {
		return gatewaySslRequireAuthentication;
	}

	public GemfireProperties setGatewaySslRequireAuthentication(Boolean gatewaySslRequireAuthentication) {
		this.gatewaySslRequireAuthentication = gatewaySslRequireAuthentication;
		return this;
	}

	public String getGroups() {
		return groups;
	}

	public GemfireProperties setGroups(String groups) {
		this.groups = groups;
		return this;
	}

	public String getHttpServiceBindAddress() {
		return httpServiceBindAddress;
	}

	public GemfireProperties setHttpServiceBindAddress(String httpServiceBindAddress) {
		this.httpServiceBindAddress = httpServiceBindAddress;
		return this;
	}

	public Integer getHttpServicePort() {
		return httpServicePort;
	}

	public GemfireProperties setHttpServicePort(Integer httpServicePort) {
		this.httpServicePort = httpServicePort;
		return this;
	}

	public String getHttpServiceSslCiphers() {
		return httpServiceSslCiphers;
	}

	public GemfireProperties setHttpServiceSslCiphers(String httpServiceSslCiphers) {
		this.httpServiceSslCiphers = httpServiceSslCiphers;
		return this;
	}

	public Boolean getHttpServiceSslEnabled() {
		return httpServiceSslEnabled;
	}

	public GemfireProperties setHttpServiceSslEnabled(Boolean httpServiceSslEnabled) {
		this.httpServiceSslEnabled = httpServiceSslEnabled;
		return this;
	}

	public String getHttpServiceSslProtocols() {
		return httpServiceSslProtocols;
	}

	public GemfireProperties setHttpServiceSslProtocols(String httpServiceSslProtocols) {
		this.httpServiceSslProtocols = httpServiceSslProtocols;
		return this;
	}

	public Boolean getHttpServiceSslRequireAuthentication() {
		return httpServiceSslRequireAuthentication;
	}

	public GemfireProperties setHttpServiceSslRequireAuthentication(Boolean httpServiceSslRequireAuthentication) {
		this.httpServiceSslRequireAuthentication = httpServiceSslRequireAuthentication;
		return this;
	}

	public Boolean getJmxManager() {
		return jmxManager;
	}

	public GemfireProperties setJmxManager(Boolean jmxManager) {
		this.jmxManager = jmxManager;
		return this;
	}

	public String getJmxManagerAccessFile() {
		return jmxManagerAccessFile;
	}

	public GemfireProperties setJmxManagerAccessFile(String jmxManagerAccessFile) {
		this.jmxManagerAccessFile = jmxManagerAccessFile;
		return this;
	}

	public String getJmxManagerBindAddress() {
		return jmxManagerBindAddress;
	}

	public GemfireProperties setJmxManagerBindAddress(String jmxManagerBindAddress) {
		this.jmxManagerBindAddress = jmxManagerBindAddress;
		return this;
	}

	public String getJmxManagerHostnameForClients() {
		return jmxManagerHostnameForClients;
	}

	public GemfireProperties setJmxManagerHostnameForClients(String jmxManagerHostnameForClients) {
		this.jmxManagerHostnameForClients = jmxManagerHostnameForClients;
		return this;
	}

	public String getJmxManagerPasswordFile() {
		return jmxManagerPasswordFile;
	}

	public GemfireProperties setJmxManagerPasswordFile(String jmxManagerPasswordFile) {
		this.jmxManagerPasswordFile = jmxManagerPasswordFile;
		return this;
	}

	public Integer getJmxManagerPort() {
		return jmxManagerPort;
	}

	public GemfireProperties setJmxManagerPort(Integer jmxManagerPort) {
		this.jmxManagerPort = jmxManagerPort;
		return this;
	}

	public Boolean getJmxManagerSslEnabled() {
		return jmxManagerSslEnabled;
	}

	public GemfireProperties setJmxManagerSslEnabled(Boolean jmxManagerSslEnabled) {
		this.jmxManagerSslEnabled = jmxManagerSslEnabled;
		return this;
	}

	public String getJmxManagerSslCiphers() {
		return jmxManagerSslCiphers;
	}

	public GemfireProperties setJmxManagerSslCiphers(String jmxManagerSslCiphers) {
		this.jmxManagerSslCiphers = jmxManagerSslCiphers;
		return this;
	}

	public String getJmxManagerSslProtocols() {
		return jmxManagerSslProtocols;
	}

	public GemfireProperties setJmxManagerSslProtocols(String jmxManagerSslProtocols) {
		this.jmxManagerSslProtocols = jmxManagerSslProtocols;
		return this;
	}

	public Boolean getJmxManagerSslRequireAuthentication() {
		return jmxManagerSslRequireAuthentication;
	}

	public GemfireProperties setJmxManagerSslRequireAuthentication(Boolean jmxManagerSslRequireAuthentication) {
		this.jmxManagerSslRequireAuthentication = jmxManagerSslRequireAuthentication;
		return this;
	}

	public Boolean getJmxManagerStart() {
		return jmxManagerStart;
	}

	public GemfireProperties setJmxManagerStart(Boolean jmxManagerStart) {
		this.jmxManagerStart = jmxManagerStart;
		return this;
	}

	public Integer getJmxManagerUpdateRate() {
		return jmxManagerUpdateRate;
	}

	public GemfireProperties setJmxManagerUpdateRate(Integer jmxManagerUpdateRate) {
		this.jmxManagerUpdateRate = jmxManagerUpdateRate;
		return this;
	}

	public Boolean getLoadClusterConfigurationFromDir() {
		return loadClusterConfigurationFromDir;
	}

	public GemfireProperties setLoadClusterConfigurationFromDir(Boolean loadClusterConfigurationFromDir) {
		this.loadClusterConfigurationFromDir = loadClusterConfigurationFromDir;
		return this;
	}

	public Integer getLocatorWaitTime() {
		return locatorWaitTime;
	}

	public GemfireProperties setLocatorWaitTime(Integer locatorWaitTime) {
		this.locatorWaitTime = locatorWaitTime;
		return this;
	}

	public String getLocators() {
		return locators;
	}

	public GemfireProperties setLocators(String locators) {
		this.locators = locators;
		return this;
	}

	public Integer getLogDiskSpaceLimit() {
		return logDiskSpaceLimit;
	}

	public GemfireProperties setLogDiskSpaceLimit(Integer logDiskSpaceLimit) {
		this.logDiskSpaceLimit = logDiskSpaceLimit;
		return this;
	}

	public String getLogFile() {
		return logFile;
	}

	public GemfireProperties setLogFile(String logFile) {
		this.logFile = logFile;
		return this;
	}

	public Integer getLogFileSizeLimit() {
		return logFileSizeLimit;
	}

	public GemfireProperties setLogFileSizeLimit(Integer logFileSizeLimit) {
		this.logFileSizeLimit = logFileSizeLimit;
		return this;
	}

	public String getLogLevel() {
		return logLevel;
	}

	public GemfireProperties setLogLevel(String logLevel) {
		this.logLevel = logLevel;
		return this;
	}

	public Integer getMaxWaitTimeReconnect() {
		return maxWaitTimeReconnect;
	}

	public GemfireProperties setMaxWaitTimeReconnect(Integer maxWaitTimeReconnect) {
		this.maxWaitTimeReconnect = maxWaitTimeReconnect;
		return this;
	}

	public String getMcastAddress() {
		return mcastAddress;
	}

	public GemfireProperties setMcastAddress(String mcastAddress) {
		this.mcastAddress = mcastAddress;
		return this;
	}

	public String getMcastFlowControl() {
		return mcastFlowControl;
	}

	public GemfireProperties setMcastFlowControl(String mcastFlowControl) {
		this.mcastFlowControl = mcastFlowControl;
		return this;
	}

	public Integer getMcastPort() {
		return mcastPort;
	}

	public GemfireProperties setMcastPort(Integer mcastPort) {
		this.mcastPort = mcastPort;
		return this;
	}

	public Integer getMcastRecvBufferSize() {
		return mcastRecvBufferSize;
	}

	public GemfireProperties setMcastRecvBufferSize(Integer mcastRecvBufferSize) {
		this.mcastRecvBufferSize = mcastRecvBufferSize;
		return this;
	}

	public Integer getMcastSendBufferSize() {
		return mcastSendBufferSize;
	}

	public GemfireProperties setMcastSendBufferSize(Integer mcastSendBufferSize) {
		this.mcastSendBufferSize = mcastSendBufferSize;
		return this;
	}

	public Integer getMcastTtl() {
		return mcastTtl;
	}

	public GemfireProperties setMcastTtl(Integer mcastTtl) {
		this.mcastTtl = mcastTtl;
		return this;
	}

	public Integer getMemberTimeout() {
		return memberTimeout;
	}

	public GemfireProperties setMemberTimeout(Integer memberTimeout) {
		this.memberTimeout = memberTimeout;
		return this;
	}

	public String getMembershipPortRange() {
		return membershipPortRange;
	}

	public GemfireProperties setMembershipPortRange(String membershipPortRange) {
		this.membershipPortRange = membershipPortRange;
		return this;
	}

	public Integer getMemcachedPort() {
		return memcachedPort;
	}

	public GemfireProperties setMemcachedPort(Integer memcachedPort) {
		this.memcachedPort = memcachedPort;
		return this;
	}

	public String getMemcachedProtocol() {
		return memcachedProtocol;
	}

	public GemfireProperties setMemcachedProtocol(String memcachedProtocol) {
		this.memcachedProtocol = memcachedProtocol;
		return this;
	}

	public String getName() {
		return name;
	}

	public GemfireProperties setName(String name) {
		this.name = name;
		return this;
	}

	public String getRedundancyZone() {
		return redundancyZone;
	}

	public GemfireProperties setRedundancyZone(String redundancyZone) {
		this.redundancyZone = redundancyZone;
		return this;
	}

	public String getRemoteLocators() {
		return remoteLocators;
	}

	public GemfireProperties setRemoteLocators(String remoteLocators) {
		this.remoteLocators = remoteLocators;
		return this;
	}

	public Boolean getRemoveUnresponsiveClient() {
		return removeUnresponsiveClient;
	}

	public GemfireProperties setRemoveUnresponsiveClient(Boolean removeUnresponsiveClient) {
		this.removeUnresponsiveClient = removeUnresponsiveClient;
		return this;
	}

	public String getSecurityClientAccessor() {
		return securityClientAccessor;
	}

	public GemfireProperties setSecurityClientAccessor(String securityClientAccessor) {
		this.securityClientAccessor = securityClientAccessor;
		return this;
	}

	public String getSecurityClientAccessorPp() {
		return securityClientAccessorPp;
	}

	public GemfireProperties setSecurityClientAccessorPp(String securityClientAccessorPp) {
		this.securityClientAccessorPp = securityClientAccessorPp;
		return this;
	}

	public String getSecurityClientAuthInit() {
		return securityClientAuthInit;
	}

	public GemfireProperties setSecurityClientAuthInit(String securityClientAuthInit) {
		this.securityClientAuthInit = securityClientAuthInit;
		return this;
	}

	public String getSecurityClientAuthenticator() {
		return securityClientAuthenticator;
	}

	public GemfireProperties setSecurityClientAuthenticator(String securityClientAuthenticator) {
		this.securityClientAuthenticator = securityClientAuthenticator;
		return this;
	}

	public String getSecurityClientDhalgo() {
		return securityClientDhalgo;
	}

	public GemfireProperties setSecurityClientDhalgo(String securityClientDhalgo) {
		this.securityClientDhalgo = securityClientDhalgo;
		return this;
	}

	public String getSecurityLogFile() {
		return securityLogFile;
	}

	public GemfireProperties setSecurityLogFile(String securityLogFile) {
		this.securityLogFile = securityLogFile;
		return this;
	}

	public String getSecurityLogLevel() {
		return securityLogLevel;
	}

	public GemfireProperties setSecurityLogLevel(String securityLogLevel) {
		this.securityLogLevel = securityLogLevel;
		return this;
	}

	public String getSecurityPeerAuthInit() {
		return securityPeerAuthInit;
	}

	public GemfireProperties setSecurityPeerAuthInit(String securityPeerAuthInit) {
		this.securityPeerAuthInit = securityPeerAuthInit;
		return this;
	}

	public String getSecurityPeerAuthenticator() {
		return securityPeerAuthenticator;
	}

	public GemfireProperties setSecurityPeerAuthenticator(String securityPeerAuthenticator) {
		this.securityPeerAuthenticator = securityPeerAuthenticator;
		return this;
	}

	public Integer getSecurityPeerVerifymemberTimeout() {
		return securityPeerVerifymemberTimeout;
	}

	public GemfireProperties setSecurityPeerVerifymemberTimeout(Integer securityPeerVerifymemberTimeout) {
		this.securityPeerVerifymemberTimeout = securityPeerVerifymemberTimeout;
		return this;
	}

	public String getServerBindAddress() {
		return serverBindAddress;
	}

	public GemfireProperties setServerBindAddress(String serverBindAddress) {
		this.serverBindAddress = serverBindAddress;
		return this;
	}

	public String getServerSslCiphers() {
		return serverSslCiphers;
	}

	public GemfireProperties setServerSslCiphers(String serverSslCiphers) {
		this.serverSslCiphers = serverSslCiphers;
		return this;
	}

	public Boolean getServerSslEnabled() {
		return serverSslEnabled;
	}

	public GemfireProperties setServerSslEnabled(Boolean serverSslEnabled) {
		this.serverSslEnabled = serverSslEnabled;
		return this;
	}

	public String getServerSslProtocols() {
		return serverSslProtocols;
	}

	public GemfireProperties setServerSslProtocols(String serverSslProtocols) {
		this.serverSslProtocols = serverSslProtocols;
		return this;
	}

	public Boolean getServerSslRequireAuthentication() {
		return serverSslRequireAuthentication;
	}

	public GemfireProperties setServerSslRequireAuthentication(Boolean serverSslRequireAuthentication) {
		this.serverSslRequireAuthentication = serverSslRequireAuthentication;
		return this;
	}

	public Integer getSocketBufferSize() {
		return socketBufferSize;
	}

	public GemfireProperties setSocketBufferSize(Integer socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
		return this;
	}

	public Integer getSocketLeaseTime() {
		return socketLeaseTime;
	}

	public GemfireProperties setSocketLeaseTime(Integer socketLeaseTime) {
		this.socketLeaseTime = socketLeaseTime;
		return this;
	}

	public Boolean getStartDevRestApi() {
		return startDevRestApi;
	}

	public GemfireProperties setStartDevRestApi(Boolean startDevRestApi) {
		this.startDevRestApi = startDevRestApi;
		return this;
	}

	public String getStartLocator() {
		return startLocator;
	}

	public GemfireProperties setStartLocator(String startLocator) {
		this.startLocator = startLocator;
		return this;
	}

	public String getStatisticArchiveFile() {
		return statisticArchiveFile;
	}

	public GemfireProperties setStatisticArchiveFile(String statisticArchiveFile) {
		this.statisticArchiveFile = statisticArchiveFile;
		return this;
	}

	public Integer getStatisticSampleRate() {
		return statisticSampleRate;
	}

	public GemfireProperties setStatisticSampleRate(Integer statisticSampleRate) {
		this.statisticSampleRate = statisticSampleRate;
		return this;
	}

	public Boolean getStatisticSamplingEnabled() {
		return statisticSamplingEnabled;
	}

	public GemfireProperties setStatisticSamplingEnabled(Boolean statisticSamplingEnabled) {
		this.statisticSamplingEnabled = statisticSamplingEnabled;
		return this;
	}

	public Integer getTcpPort() {
		return tcpPort;
	}

	public GemfireProperties setTcpPort(Integer tcpPort) {
		this.tcpPort = tcpPort;
		return this;
	}

	public Integer getTombstoneGcThreshold() {
		return tombstoneGcThreshold;
	}

	public GemfireProperties setTombstoneGcThreshold(Integer tombstoneGcThreshold) {
		this.tombstoneGcThreshold = tombstoneGcThreshold;
		return this;
	}

	public Integer getUdpFragmentSize() {
		return udpFragmentSize;
	}

	public GemfireProperties setUdpFragmentSize(Integer udpFragmentSize) {
		this.udpFragmentSize = udpFragmentSize;
		return this;
	}

	public Integer getUdpRecvBufferSize() {
		return udpRecvBufferSize;
	}

	public GemfireProperties setUdpRecvBufferSize(Integer udpRecvBufferSize) {
		this.udpRecvBufferSize = udpRecvBufferSize;
		return this;
	}

	public Integer getUdpSendBufferSize() {
		return udpSendBufferSize;
	}

	public GemfireProperties setUdpSendBufferSize(Integer udpSendBufferSize) {
		this.udpSendBufferSize = udpSendBufferSize;
		return this;
	}

	public Boolean getUseClusterConfiguration() {
		return useClusterConfiguration;
	}

	public GemfireProperties setUseClusterConfiguration(Boolean useClusterConfiguration) {
		this.useClusterConfiguration = useClusterConfiguration;
		return this;
	}

	public String getUserCommandPackages() {
		return userCommandPackages;
	}

	public GemfireProperties setUserCommandPackages(String userCommandPackages) {
		this.userCommandPackages = userCommandPackages;
		return this;
	}

	/**
	 * Return a {@link Properties} object containing all of the set
	 * (not null) fields in this object.
	 *
	 * @return properties object containing properties from this object
	 */
	public Properties toProperties() {
		Properties properties = new Properties();
		for (Field field : GemfireProperties.class.getDeclaredFields()) {
			Object o = null;
			try {
				o = field.get(this);
			}
			catch (IllegalAccessException e) {
				// todo: maybe this should be logged
			}
			if (o != null) {
				StringBuilder builder = new StringBuilder();
				for (char c : field.getName().toCharArray()) {
					if (Character.isUpperCase(c)) {
						builder.append("-").append(Character.toLowerCase(c));
					}
					else {
						builder.append(c);
					}
				}
				properties.setProperty(builder.toString(), o.toString());
			}
		}
		return properties;
	}

}
