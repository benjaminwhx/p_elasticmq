package com.github.elasticmq.sqs.serialization;

import java.lang.reflect.Type;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:15 pm
 */
public interface MessageSerializer<T> {

    String serialize(T obj) throws Exception;

    T deserialize(String obj) throws Exception;
}
