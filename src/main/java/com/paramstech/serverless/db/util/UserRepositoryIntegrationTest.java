package com.paramstech.serverless.db.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.paramstech.serverless.db.model.User;
import com.paramstech.serverless.db.repositories.UserRepository;



@Configuration  

public class UserRepositoryIntegrationTest {
     
    private static final String KEY_NAME = "id";
    private static final Long READ_CAPACITY_UNITS = 5L;
    private static final Long WRITE_CAPACITY_UNITS = 5L;
    
    @Autowired
    UserRepository repository;
    
    @Autowired
    private AmazonDynamoDB amazonDynamoDB;
    
    public static void main(String args[]) {
    	UserRepositoryIntegrationTest test = new UserRepositoryIntegrationTest();
    	test.init();
    }
    
    public void init() {
    	
    	amazonDynamoDB =new DynamoDBClientFactory().createClient();
        ListTablesResult listTablesResult = amazonDynamoDB.listTables();
        listTablesResult.getTableNames().stream().forEach(tbName->{
        	System.out.println(tbName);
        });
        
        listTablesResult.getTableNames().stream().
                filter(tableName -> tableName.equals("User")).forEach(tableName -> {
            amazonDynamoDB.deleteTable(tableName);
        });
    
        List<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName(KEY_NAME).withAttributeType("S"));
    
        List<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>();
        keySchemaElements.add(new KeySchemaElement().withAttributeName(KEY_NAME).withKeyType(KeyType.HASH));
    
        CreateTableRequest request = new CreateTableRequest()
                .withTableName("User")
                .withKeySchema(keySchemaElements)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(READ_CAPACITY_UNITS)
                        .withWriteCapacityUnits(WRITE_CAPACITY_UNITS));
    
        amazonDynamoDB.createTable(request);
        
        User dave = new User("Dave", "Matthews");
       // RepositoryFactorySupport factory = RepositoryFactorySupport.
        repository.save(dave);
    
        User carter = new User("Carter", "Beauford");
        repository.save(carter);
    
        List<User> result = repository.findByLastName("Matthews");
    }
    
    
}