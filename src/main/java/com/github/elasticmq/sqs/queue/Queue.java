package com.github.elasticmq.sqs.queue;

import java.util.List;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 0:55 pm
 */
public interface Queue<T> {

    /**
     * send a message to queue
     * @param message
     * @return
     */
    boolean enqueue(T message);

    /**
     * get a message from the queue and delete it.
     * @return
     */
    T dequeue();

    /**
     * get some messages from the queue by {@param maxNumberOfElements} and delete it.
     * @param maxNumberOfElements the num of the message to get
     * @return
     */
    List<T> dequeue(int maxNumberOfElements);

    /**
     * the same as {@link Queue#dequeue()} but not delete it.
     * @return
     */
    ConsumedElement<T> peek();

    /**
     * the same as {@link Queue#dequeue(int)} but not delete them.
     * @param maxNumberOfElements
     * @return
     */
    List<ConsumedElement<T>> peek(int maxNumberOfElements);
}
