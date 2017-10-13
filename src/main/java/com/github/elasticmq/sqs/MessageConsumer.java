package com.github.elasticmq.sqs;

import com.github.elasticmq.sqs.queue.ConsumedElement;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:13 pm
 */
public interface MessageConsumer<T> {
    void onMessageConsumed(ConsumedElement<T> var1);
}
