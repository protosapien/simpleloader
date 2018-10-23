package com.datastax.expedia.simpleloader;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.*;
import com.datastax.driver.mapping.MappingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraConnector {

    private Cluster cluster;
    private Session session;
    private MappingManager mappingManager;

    private final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    public void connect() {
        cluster =
                Cluster.builder()
                        // .addContactPoints("127.0.0.1")
                        .addContactPoints("172.31.33.179", "172.31.34.129", "172.31.46.107", "172.31.33.29",
                                "172.31.42.56", "172.31.45.70", "172.31.41.9", "172.31.32.12", "172.31.42.72")
                        .withPort(9042)
                        .withClusterName("txbv2-cluster")
                        .withLoadBalancingPolicy(
                                DCAwareRoundRobinPolicy
                                        .builder()
                                        .withLocalDc("DC1")
                                        .withUsedHostsPerRemoteDc(2)
                                        .allowRemoteDCsForLocalConsistencyLevel()
                                        .build())
                        .build();
        session = cluster.connect("txbv2");
        mappingManager = new MappingManager(session);
        LOG.info("Connected to " + cluster.getClusterName() );

    }


    public MappingManager getMappingManager() {
        return this.mappingManager;
    }

    public void close() {
        session.close();
        cluster.close();
    }

}



