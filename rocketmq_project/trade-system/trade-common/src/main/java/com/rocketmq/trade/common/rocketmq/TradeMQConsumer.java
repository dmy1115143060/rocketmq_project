package com.rocketmq.trade.common.rocketmq;

import com.rocketmq.trade.common.exception.TradeMQException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 杜名洋
 * 封装consumer, 使得可以通过spring来创建consumer对象
 **/
public class TradeMQConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TradeMQProducer.class);
    private String groupName;
    private String topic;
    private String tag = "*";
    private String namesrvAddr;
    private int consumeThreadMin = 20;
    private int consumeThreadMax = 60;
    private DefaultMQPushConsumer consumer;
    /**
     * 消息处理的接口
     */
    private IMessageProcessor processor;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumeThreadMin(int consumeThreadMin) {
        this.consumeThreadMin = consumeThreadMin;
    }

    public void setConsumeThreadMax(int consumeThreadMax) {
        this.consumeThreadMax = consumeThreadMax;
    }

    public void setiMessageProcessor(IMessageProcessor processor) {
        this.processor = processor;
    }

    /**
     * init方法，初始化consumer，用于spring的init
     */
    public void init() throws TradeMQException {
        if (StringUtils.isEmpty(groupName)) {
            throw new TradeMQException("groupName is blank!");
        }
        if (StringUtils.isEmpty(namesrvAddr)) {
            throw new TradeMQException("namesrvAddr is blank!");
        }
        consumer = new DefaultMQPushConsumer(this.groupName);
        try {
            consumer.setNamesrvAddr(namesrvAddr);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.setConsumeThreadMax(consumeThreadMax);
            consumer.setConsumeThreadMin(consumeThreadMin);
            consumer.subscribe(topic, tag);
            TradeMessageListener messageListener = new TradeMessageListener();
            messageListener.setProcessor(processor);
            consumer.registerMessageListener(messageListener);
            consumer.start();
            LOGGER.info(String.format("consumer started!groupName:[%s],topic:[%s],namesrvAddr:[%s]",
                    this.groupName, this.topic, this.namesrvAddr));
        } catch (MQClientException e) {
            LOGGER.error(String.format("consumer started!groupName:[%s],topic:[%s],namesrvAddr:[%s]",
                    this.groupName, this.topic, this.namesrvAddr), e);
            throw new TradeMQException(e);
        }
    }
}
