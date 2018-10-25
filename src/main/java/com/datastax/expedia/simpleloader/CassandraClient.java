package com.datastax.expedia.simpleloader;


import com.datastax.driver.mapping.Mapper;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.javafaker.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;

public class CassandraClient {

    private static final Logger LOG = LoggerFactory.getLogger(CassandraClient.class);

    public static void main(String args[]) {

        final int COUNT = 1000000;
        final int TIMEDELAY =  2000;

        BasicConfigurator.configure();

        RetryQueue retryQueue;

        CassandraConnector connector = new CassandraConnector();

        connector.connect();
        Mapper<RetryQueue> mapper = connector.getMappingManager().mapper(RetryQueue.class);
        Faker faker = new Faker();
        Random rand = new Random();

        List<String> typeList = new ArrayList<String>();

        for(int m = 0; m < 5000; m++)
        {
            typeList.add(faker.lorem().characters(13, true));
        }


        // long newDate = new Date().getTime();

        try {

            for (int i = 0; i < COUNT; i++) {

                Timestamp randomTimeStamp = getRandomEpoch();
                String dateString = randomTimeStamp.toString();
                DateTime next_retry = new DateTime(randomTimeStamp).plusMinutes(10);
                retryQueue = new RetryQueue();
                retryQueue.setType(typeList.get(rand.nextInt(2500)));
                retryQueue.setDate(dateString);
                retryQueue.setNext_retry(next_retry.getMillis());
                retryQueue.setKey(faker.lorem().fixedString(13));
                retryQueue.setEvent_metadata(faker.lorem().fixedString(15));
                mapper.save(retryQueue);

                LOG.info("*** retryqueue with PK " + retryQueue.getType() + " inserted @ count: " + (i+1) + " ***\n");

                timeDelay(TIMEDELAY);
            }

        }catch (InterruptedException iexp){
            LOG.error(iexp.toString());
        }
        catch (Exception exp){
            LOG.error(exp.getMessage());
        }
        finally {
            connector.close();
        }
    }

    private static Timestamp getRandomEpoch(){

        long offset = Timestamp.valueOf("2016-08-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2018-08-31 00:00:00").getTime();
        long diff = end - offset + 1;
        long time = offset +  (long)(Math.random() * diff);
        return new Timestamp(time);
    }

    private static void timeDelay(int delayMillis) throws InterruptedException{
        Thread.sleep(delayMillis);
    }

}

/*

TIMESTAMP <-> java.util.Date                     : use getTimestamp()
TIME      <-> long                               : use getTime()
DATE      <-> com.datastax.driver.core.LocalDate : use getDate()


faker.lorem().characters(13,true)

    Cassandra Drivers A basic example in Java: Cluster	cluster	=
    Cluster.builder().addContactPoints("52.89.183.67").withPort(9042).build();
    Session	session	= cluster.newSession();
    session.execute("SELECT	*	FROM	foo…");

*/


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