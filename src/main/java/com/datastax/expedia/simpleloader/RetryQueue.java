package com.datastax.expedia.simpleloader;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.sql.Time;
import java.sql.Timestamp;

@Table(keyspace = "txbv2", name = "retryqueue", readConsistency = "QUORUM", writeConsistency = "QUORUM")
public class RetryQueue {

    @PartitionKey(0)
    @Column(name = "type")
    private String type;

    @PartitionKey(1)
    @Column(name = "date")
    private String date;

    @ClusteringColumn(0)
    @Column(name = "next_retry")
    private long next_retry;

    @ClusteringColumn(1)
    @Column(name = "key")
    private String key;

    @Column(name = "event_metadata")
    private String event_metadata;

    public RetryQueue(){

    }

    public RetryQueue(String type, String date, long next_retry, String key, String event_metadata) {
        this.type = type;
        this.date = date;
        this.next_retry = next_retry;
        this.event_metadata = event_metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getNext_retry() {
        return next_retry;
    }

    public void setNext_retry(long next_retry) {
        this.next_retry = next_retry;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEvent_metadata() {
        return event_metadata;
    }

    public void setEvent_metadata(String event_metadata) {
        this.event_metadata = event_metadata;
    }
}


/*
CREATE TABLE txbv2.retryqueue (
	type text,
	date text,
	next_retry bigint,
	key text,
	event_metadata text,
	PRIMARY KEY ((type, date), next_retry, key)
	WITH CLUSTERING ORDER BY (next_retry DESC, key ASC);
*/
