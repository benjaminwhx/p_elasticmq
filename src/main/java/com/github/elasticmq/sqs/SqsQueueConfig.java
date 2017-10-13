package com.github.elasticmq.sqs;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:13 pm
 */
public class SqsQueueConfig {
    private String queueName;
    private boolean allowedToCreateQueue;

    public SqsQueueConfig() {
        this.allowedToCreateQueue = false;
    }

    public SqsQueueConfig(String queueName, boolean allowedToCreateQueue) {
        this.queueName = queueName;
        this.allowedToCreateQueue = allowedToCreateQueue;
    }

    public String getQueueName() {
        return this.queueName;
    }

    public boolean isAllowedToCreateQueue() {
        return this.allowedToCreateQueue;
    }
}
