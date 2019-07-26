package com.joeyvmason.serverless.spring.application;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.internal.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder;
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class LambdaHandler implements RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(LambdaHandler.class);

    private static SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    private boolean initialized = false;

    @Override
    public AwsProxyResponse handleRequest(AwsProxyRequest awsProxyRequest, Context context) {
        if (!initialized) {
        		
            try {
                handler = SpringLambdaContainerHandler.getAwsProxyHandler(MvcConfig.class);
                initialized = true;
            } catch (ContainerInitializationException e) {
                LOG.warn("Unable to create handler", e);
                return null;
            }
        }
        //new AwsProxyResponse()
        //reutn  Response.ok(context).build();
        //context.getLogger().log("Input: " + input);
        //AwsProxyResponse response =new AwsProxyResponse(200);
        //response.setBody("");
        //return response;
        AwsProxyResponse temp = new AwsProxyResponse(200);
        try {
		String json = TextUtilities.dump(context);
		String json1 = TextUtilities.dump(awsProxyRequest);
		LOG.debug(json);
		LOG.debug(json1);
		LOG.info(json);
		LOG.info(json1);
		temp.setBody(json1);
        }catch(Exception e) {
        	e.printStackTrace();
        }
		
		
		return temp;
        //return handler.proxy(awsProxyRequest, context);
    }

    public static void main(String args[]) {
    		
            try {
                handler = SpringLambdaContainerHandler.getAwsProxyHandler(MvcConfig.class);
            } catch (ContainerInitializationException e) {
                LOG.warn("Unable to create handler", e);
            }
            AwsProxyResponse temp = new AwsProxyResponse(200);
       	 AwsProxyRequest awsProxyRequest = new AwsProxyRequestBuilder("/articles/1", "GET").build();
           AwsProxyResponse response =  handler.proxy(awsProxyRequest, new MockLambdaContext()); 
        }
 }

