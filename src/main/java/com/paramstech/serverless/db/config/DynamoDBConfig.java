package com.paramstech.serverless.db.config;

import org.apache.commons.lang3.StringUtils;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

@Configuration
@EnableDynamoDBRepositories(basePackages = "com.paramstech.serverless.db.repositories")
public class DynamoDBConfig {

	@Value("dynamodb.us-east-1.amazonaws.com")
	private String amazonDynamoDBEndpoint;

	//@Value("${amazon.aws.accesskey}")
	@Value("AKIAJUIDZHWNWZX5NDDA")
	private String amazonAWSAccessKey;

	@Value("7FJtLX2s4EE0I8ADH0t6DK1cWj1Kq0eRZycv+Cg3")
	private String amazonAWSSecretKey;

	@Bean
	public AmazonDynamoDB amazonDynamoDB(AWSCredentials amazonAWSCredentials) {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(amazonAWSCredentials);

		if (StringUtils.isNotEmpty(amazonDynamoDBEndpoint)) {
			amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
		}
		return amazonDynamoDB;
	}

	@Bean
	public AWSCredentials amazonAWSCredentials() {
	    // Or use an AWSCredentialsProvider/AWSCredentialsProviderChain
		return new BasicAWSCredentials("AKIAJUIDZHWNWZX5NDDA", "7FJtLX2s4EE0I8ADH0t6DK1cWj1Kq0eRZycv");
	}

}
