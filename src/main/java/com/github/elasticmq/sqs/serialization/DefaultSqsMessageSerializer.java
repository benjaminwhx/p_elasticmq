package com.github.elasticmq.sqs.serialization;

import com.github.elasticmq.sqs.utils.GsonUtils;

import java.lang.reflect.Type;

/**
 * User: benjamin.wuhaixu
 * Date: 2017-10-13
 * Time: 1:24 pm
 */
public class DefaultSqsMessageSerializer<T> implements MessageSerializer<T> {

    private Type type;

    public DefaultSqsMessageSerializer(Type type) {
        this.type = type;
    }
    @Override
    public String serialize(T obj) throws Exception {
        return GsonUtils.toJson(obj);
    }

    @Override
    public T deserialize(String obj) throws Exception {
        return (T) GsonUtils.fromJson(obj, type);
    }
}
