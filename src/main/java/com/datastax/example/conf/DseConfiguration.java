package com.datastax.example.conf;

import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
import com.datastax.driver.mapping.MappingManager;
import com.datastax.example.accessor.UserAccessor;

@Configuration
public class DseConfiguration {

	@Autowired
	private DseProperties dseProperties;
	
	@Bean
	public MappingManager manager() {
		return new MappingManager(session());
	}

	@Bean
	public DseSession session() {
		return cluster().connect(dseProperties.getKeyspace());
	}

	@Bean
	protected DseCluster cluster() {

		QueryOptions queryOptions = new QueryOptions().setConsistencyLevel(dseProperties.getConsistencyLevel())
				.setDefaultIdempotence(true);
		SocketOptions socketOptions = new SocketOptions().setReadTimeoutMillis(dseProperties.getReadTimeout())
				.setConnectTimeoutMillis(dseProperties.getConnectionTimeout());

		PoolingOptions poolingOptions = new PoolingOptions().setConnectionsPerHost(HostDistance.LOCAL, 2, 3);

		LoadBalancingPolicy loadBalancingPolicy = new TokenAwarePolicy(
				DCAwareRoundRobinPolicy.builder().withLocalDc(dseProperties.getLocalDC()).build());

		DseCluster cluster = DseCluster.builder()
				.addContactPoint(dseProperties.getContactPoints().stream().collect(Collectors.joining(",")))
				.withPort(dseProperties.getPort())
				.withCredentials(dseProperties.getLogin(), dseProperties.getPassword()).withQueryOptions(queryOptions)
				.withSocketOptions(socketOptions).withPoolingOptions(poolingOptions)
				.withLoadBalancingPolicy(loadBalancingPolicy)
				.withSpeculativeExecutionPolicy(
				        new ConstantSpeculativeExecutionPolicy(
				        		dseProperties.getConstantSpeculativeExecutionPolicyDelay(), // delay before a new execution is launched
				        		dseProperties.getConstantSpeculativeExecutionPolicyMaxNumber()    // maximum number of executions
				        )).build();
		return cluster;
	}

	@Bean
	public UserAccessor userAccessor() {
		return manager().createAccessor(UserAccessor.class);
		
	}
}
