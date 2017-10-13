package com.github.elasticmq.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.github.elasticmq.sqs.queue.ConsumedElement;
import com.github.elasticmq.sqs.queue.Queue;
import com.github.elasticmq.sqs.serialization.DefaultSqsMessageSerializer;
import com.github.elasticmq.sqs.serialization.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:14 pm
 */
public final class SqsQueue<T> implements Queue<T> {
    private static final Logger logger = LoggerFactory.getLogger(SqsQueue.class);
    private static final int DEFAULT_MAX_NUMBER_OF_MESSAGES = 10;
    private static final int DEFAULT_MAX_DELIVERY_DELAY = 900;
    private final AmazonSQS amazonSqs;
    private final MessageSerializer<T> messageSerializer;
    private final String queueUrl;

    public SqsQueue(AmazonSQS amazonSqs, SqsQueueConfig sqsQueueConfig, MessageSerializer<T> messageSerializer) {
        String queueUrl;
        try {
            queueUrl = amazonSqs.getQueueUrl(sqsQueueConfig.getQueueName()).getQueueUrl();
        } catch (QueueDoesNotExistException var6) {
            if(!sqsQueueConfig.isAllowedToCreateQueue()) {
                throw new IllegalStateException("SQS queue with name " + sqsQueueConfig.getQueueName() + " does not exist.");
            }

            queueUrl = amazonSqs.createQueue(sqsQueueConfig.getQueueName()).getQueueUrl();
        } catch (Exception e) {
            throw new IllegalStateException("Caught exception during initialization", e);
        }

        this.amazonSqs = amazonSqs;
        this.messageSerializer = messageSerializer;
        this.queueUrl = queueUrl;
    }

    public SqsQueue(AmazonSQS amazonSqs, SqsQueueConfig sqsQueueConfig, Type type) {
        this(amazonSqs, sqsQueueConfig, new DefaultSqsMessageSerializer(type));
    }

    public boolean enqueue(T element) {
        return this.enqueue(element, null);
    }

    public T dequeue() {
        List<T> messages = this.dequeue(1);
        return !messages.isEmpty() ? messages.get(0) : null;
    }

    public List<T> dequeue(int maxNumberOfElements) {
        List<T> messages = new ArrayList();
        List<ConsumedElement<T>> consumedMessages = this.peek(maxNumberOfElements);

        for (ConsumedElement<T> consumedMessage : consumedMessages) {
            messages.add(consumedMessage.getElement());
            consumedMessage.ack();
        }

        return messages;
    }

    public ConsumedElement<T> peek() {
        List<ConsumedElement<T>> messages = this.peek(1);
        return !messages.isEmpty() ? messages.get(0) : null;
    }

    public List<ConsumedElement<T>> peek(int maxNumberOfElements) {
        return this.peek(maxNumberOfElements, null);
    }

    public List<ConsumedElement<T>> peek(int maxNumberOfMessages, Integer waitTimeSeconds) {
        try {
            List<ConsumedElement<T>> messages = new ArrayList();
            if(maxNumberOfMessages <= 0) {
                maxNumberOfMessages = 1;
            } else if(maxNumberOfMessages > DEFAULT_MAX_NUMBER_OF_MESSAGES) {
                maxNumberOfMessages = DEFAULT_MAX_NUMBER_OF_MESSAGES;
            }

            ReceiveMessageRequest receiveMessageRequest = (new ReceiveMessageRequest(this.queueUrl)).withMaxNumberOfMessages(maxNumberOfMessages);
            if(waitTimeSeconds != null && waitTimeSeconds > 0) {
                receiveMessageRequest.withWaitTimeSeconds(waitTimeSeconds);
            }

            ReceiveMessageResult receiveMessageResult = this.amazonSqs.receiveMessage(receiveMessageRequest);
            if(receiveMessageRequest != null) {
                List<Message> messageList = receiveMessageResult.getMessages();
                if (messageList != null && messageList.size() > 0) {
                    for (Message message : messageList) {
                        try {
                            T deserializedMessage = this.messageSerializer.deserialize(message.getBody());
                            ConsumedElement<T> consumedMessage = new SqsQueue.SqsConsumedMessage(message, deserializedMessage);
                            messages.add(consumedMessage);
                        } catch (Exception var11) {
                            logger.error("Fail to deserialize message with body: " + message.getBody());
                        }
                    }
                }
            }

            return messages;
        } catch (Exception e) {
            logger.error("Exception during peek", e);
            return new ArrayList();
        }
    }

    public boolean enqueue(T element, Integer delaySeconds) {
        try {
            String serializedMessage = this.messageSerializer.serialize(element);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(this.queueUrl, serializedMessage);
            if(delaySeconds != null && delaySeconds >= 0) {
                sendMessageRequest.withDelaySeconds(Math.min(delaySeconds, DEFAULT_MAX_DELIVERY_DELAY));
            }

            this.amazonSqs.sendMessage(sendMessageRequest);
            return true;
        } catch (Exception e) {
            logger.error("Exception during enqueue", e);
            return false;
        }
    }

    private class SqsConsumedMessage implements ConsumedElement<T> {
        private final Message sqsMessage;
        private final T element;

        private SqsConsumedMessage(Message message, T element) {
            this.sqsMessage = message;
            this.element = element;
        }

        public T getElement() {
            return this.element;
        }

        public void ack() {
            try {
                SqsQueue.this.amazonSqs.deleteMessage(new DeleteMessageRequest(SqsQueue.this.queueUrl, this.sqsMessage.getReceiptHandle()));
            } catch (Exception e) {
                SqsQueue.logger.error("Exception during ack", e);
            }

        }

        public void suspend(long suspendDurationMsec) {
            try {
                SqsQueue.this.amazonSqs.changeMessageVisibility(SqsQueue.this.queueUrl, this.sqsMessage.getReceiptHandle(), (int)(suspendDurationMsec / 1000));
            } catch (Exception e) {
                SqsQueue.logger.error("Exception during suspend", e);
            }

        }

        public void release() {
            try {
                SqsQueue.this.amazonSqs.changeMessageVisibility(SqsQueue.this.queueUrl, this.sqsMessage.getReceiptHandle(), 0);
            } catch (Exception e) {
                SqsQueue.logger.error("Exception during release", e);
            }

        }
    }
}
