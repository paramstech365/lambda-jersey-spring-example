package com.paramstech.serverless.db.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class DynamoDBClientFactory {
    
	private final AmazonDynamoDBClientBuilder builder =
        AmazonDynamoDBClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAIQM2DHJKXSQD5XBA", "K0DYgB5SDhINUIYC7H+SWdEKLi5cPvSdX0CB15OM")));

    public AmazonDynamoDB createClient() {
        return builder.build();
    }
    
}
