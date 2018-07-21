package com.datastax.example.conf;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.example.dao.accessor.UserAccessor;
import com.datastax.example.model.User;

@Configuration
public class DseConfiguration {

	@Autowired
	private DseProperties dseProperties;

	@Bean
	protected MappingManager manager() {
		return new MappingManager(session());
	}

	@Bean(name="defaultSession")
	@Profile("IT")
	public DseSession sessionIT() {
		DseSession session = cluster().connect();
		session.execute(
				"CREATE KEYSPACE IF NOT EXISTS ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }");
		session.execute(
				"CREATE TABLE IF NOT EXISTS ks.user (id text PRIMARY KEY, firstname text, lastname text, login text)");
		session.execute("CREATE SEARCH INDEX IF NOT EXISTS ON ks.user");
		session.execute("USE " + dseProperties.getSolrConf().getKeyspace());
		return session;
	}

	@Bean(name="defaultSession")
	@Profile("default")
	public DseSession session() {
		return cluster().connect(dseProperties.getConf().getKeyspace());
	}

	@Bean
	public Mapper<User> userManager() {
		return manager().mapper(User.class);
	}

	@Bean
	protected DseCluster cluster() {

		QueryOptions queryOptions = new QueryOptions()
				.setConsistencyLevel(dseProperties.getConf().getConsistencyLevel()).setDefaultIdempotence(true);
		SocketOptions socketOptions = new SocketOptions().setReadTimeoutMillis(dseProperties.getConf().getReadTimeout())
				.setConnectTimeoutMillis(dseProperties.getConf().getConnectionTimeout());

		PoolingOptions poolingOptions = new PoolingOptions().setConnectionsPerHost(HostDistance.LOCAL, 2, 3);

		LoadBalancingPolicy loadBalancingPolicy = new TokenAwarePolicy(
				DCAwareRoundRobinPolicy.builder().withLocalDc(dseProperties.getConf().getLocalDC()).build());

		DseCluster cluster = DseCluster.builder()
				.addContactPoint(dseProperties.getConf().getContactPoints().stream().collect(Collectors.joining(",")))
				.withPort(dseProperties.getConf().getPort())
				.withCredentials(dseProperties.getConf().getLogin(), dseProperties.getConf().getPassword())
				.withQueryOptions(queryOptions).withSocketOptions(socketOptions).withPoolingOptions(poolingOptions)
				.withLoadBalancingPolicy(loadBalancingPolicy)
				.withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(
						dseProperties.getConf().getConstantSpeculativeExecutionPolicyDelay(), // delay before a new
																								// execution is launched
						dseProperties.getConf().getConstantSpeculativeExecutionPolicyMaxNumber() // maximum number of
																									// executions
				)).build();
		return cluster;
	}

	@Bean(name="solrSession")
	@Profile("IT")
	public DseSession solrSessionIT() {
		DseSession session = solrCluster().connect();
		session.execute(
				"CREATE KEYSPACE IF NOT EXISTS ks WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1 }");
		session.execute(
				"CREATE TABLE IF NOT EXISTS ks.user (id text PRIMARY KEY, firstname text, lastname text, login text)");
		session.execute("CREATE SEARCH INDEX IF NOT EXISTS ON ks.user");
		session.execute("USE " + dseProperties.getSolrConf().getKeyspace());
		return session;
	}

	@Bean(name="solrSession")
	@Profile("default")
	public DseSession solrSession() {
		return solrCluster().connect(dseProperties.getSolrConf().getKeyspace());
	}

	@Bean
	protected DseCluster solrCluster() {

		QueryOptions queryOptions = new QueryOptions()
				.setConsistencyLevel(dseProperties.getSolrConf().getConsistencyLevel()).setDefaultIdempotence(true);
		SocketOptions socketOptions = new SocketOptions()
				.setReadTimeoutMillis(dseProperties.getSolrConf().getReadTimeout())
				.setConnectTimeoutMillis(dseProperties.getSolrConf().getConnectionTimeout());

		PoolingOptions poolingOptions = new PoolingOptions().setConnectionsPerHost(HostDistance.LOCAL, 2, 3);

		LoadBalancingPolicy loadBalancingPolicy = new TokenAwarePolicy(
				DCAwareRoundRobinPolicy.builder().withLocalDc(dseProperties.getSolrConf().getLocalDC()).build());

		DseCluster cluster = DseCluster.builder()
				.addContactPoint(
						dseProperties.getSolrConf().getContactPoints().stream().collect(Collectors.joining(",")))
				.withPort(dseProperties.getSolrConf().getPort())
				.withCredentials(dseProperties.getSolrConf().getLogin(), dseProperties.getSolrConf().getPassword())
				.withQueryOptions(queryOptions).withSocketOptions(socketOptions).withPoolingOptions(poolingOptions)
				.withLoadBalancingPolicy(loadBalancingPolicy)
				.withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(
						dseProperties.getSolrConf().getConstantSpeculativeExecutionPolicyDelay(), // delay before a new
																									// execution is
																									// launched
						dseProperties.getSolrConf().getConstantSpeculativeExecutionPolicyMaxNumber() // maximum number
																										// of executions
				)).build();
		return cluster;
	}

	@Bean
	public UserAccessor userAccessor() {
		return manager().createAccessor(UserAccessor.class);

	}
}
