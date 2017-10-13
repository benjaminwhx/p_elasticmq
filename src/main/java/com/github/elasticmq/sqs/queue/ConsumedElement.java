package com.github.elasticmq.sqs.queue;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 0:57 pm
 */
public interface ConsumedElement<T> {

    /**
     * get the message
     * @return
     */
    T getElement();

    /**
     * delete the message from the queue
     */
    void ack();

    /**
     * use change the message visibility to suspend
     * @param millTimes
     */
    void suspend(long millTimes);

    /**
     * let the message visible immediately
     * change the message visibility to 0
     */
    void release();
}
