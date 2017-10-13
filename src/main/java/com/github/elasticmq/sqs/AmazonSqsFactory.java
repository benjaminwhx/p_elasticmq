package com.github.elasticmq.sqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:02 pm
 */
public class AmazonSqsFactory {
    private static final Map<SqsConfig, AmazonSQS> sqsMap = new ConcurrentHashMap();

    private AmazonSqsFactory() {
    }

    public static synchronized AmazonSQS getSqs(SqsConfig sqsConfig) {
        if(sqsMap.containsKey(sqsConfig)) {
            return sqsMap.get(sqsConfig);
        } else {
            AmazonSQSClientBuilder builder = AmazonSQSClientBuilder.standard();
            if(sqsConfig.getAccessKey() != null && sqsConfig.getSecretKey() != null) {
                builder.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(sqsConfig.getAccessKey(), sqsConfig.getSecretKey())));
            }

            if(sqsConfig.getEndpoint() != null) {
                builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsConfig.getEndpoint(), sqsConfig.getRegion()));
            } else {
                builder.withRegion(sqsConfig.getRegion());
            }

            AmazonSQS amazonSqs = builder.build();
            sqsMap.put(sqsConfig, amazonSqs);
            return amazonSqs;
        }
    }

    static int getSqsMapSize() {
        return sqsMap.size();
    }

    static void clearSqsMap() {
        sqsMap.clear();
    }
}
