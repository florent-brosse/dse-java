package com.datastax.example.conf;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.datastax.driver.dse.DseCluster;
import com.datastax.driver.dse.DseCluster.Builder;
import com.datastax.driver.dse.DseSession;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.example.dao.accessor.UserAccessor;
import com.datastax.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

@Configuration
public class DseConfiguration {

    @Autowired
    private DseProperties dseProperties;

    @Bean
    protected MappingManager manager() {
        return new MappingManager(session());
    }

    @Bean(name = "defaultSession")
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

    @Bean(name = "defaultSession")
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

        Builder builder = DseCluster.builder()
                .addContactPoints(dseProperties.getConf().getContactPoints().toArray(new String[dseProperties.getConf().getContactPoints().size()]))
                .withPort(dseProperties.getConf().getPort())
                .withCredentials(dseProperties.getConf().getLogin(), dseProperties.getConf().getPassword())
                .withQueryOptions(queryOptions).withSocketOptions(socketOptions).withPoolingOptions(poolingOptions)
                .withLoadBalancingPolicy(loadBalancingPolicy)
                .withSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(
                        dseProperties.getConf().getConstantSpeculativeExecutionPolicyDelay(), // delay before a new
                        // execution is launched
                        dseProperties.getConf().getConstantSpeculativeExecutionPolicyMaxNumber() // maximum number of
                        // executions
                ));
        if (dseProperties.getConf().isEnableSSL()) {
            builder = builder.withSSL(getSSLOptions());
        }
        return builder.build();
    }

    @Bean(name = "solrSession")
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

    @Bean(name = "solrSession")
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

        Builder builder = DseCluster.builder()
                .addContactPoints(dseProperties.getSolrConf().getContactPoints().toArray(new String[dseProperties.getSolrConf().getContactPoints().size()]))
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
                ));
        if (dseProperties.getSolrConf().isEnableSSL()) {
            builder = builder.withSSL(getSolrSSLOptions());
        }
        return builder.build();
    }

    @Bean
    public UserAccessor userAccessor() {
        return manager().createAccessor(UserAccessor.class);

    }

    protected RemoteEndpointAwareJdkSSLOptions getSSLOptions() {
        RemoteEndpointAwareJdkSSLOptions sslOptions;
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");


            InputStream in = new ClassPathResource(dseProperties.getConf().getTruststoreFilePath()).getInputStream();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(in, dseProperties.getConf().getTruststorePassword().toCharArray());

            tmf.init(ks);
            context.init(null, tmf.getTrustManagers(), null);

            sslOptions = (RemoteEndpointAwareJdkSSLOptions)RemoteEndpointAwareJdkSSLOptions.builder().withSSLContext(context).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sslOptions;
    }

    public RemoteEndpointAwareJdkSSLOptions getSolrSSLOptions() {

        RemoteEndpointAwareJdkSSLOptions sslOptions;
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");


            InputStream in = new ClassPathResource(dseProperties.getSolrConf().getTruststoreFilePath()).getInputStream();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(in, dseProperties.getSolrConf().getTruststorePassword().toCharArray());

            tmf.init(ks);
            context.init(null, tmf.getTrustManagers(), null);

            sslOptions = (RemoteEndpointAwareJdkSSLOptions)RemoteEndpointAwareJdkSSLOptions.builder().withSSLContext(context).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sslOptions;
    }
}
