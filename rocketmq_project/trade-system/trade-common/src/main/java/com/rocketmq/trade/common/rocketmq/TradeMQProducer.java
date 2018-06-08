package com.rocketmq.trade.common.rocketmq;

import com.rocketmq.trade.common.constants.MQEnums;
import com.rocketmq.trade.common.exception.TradeMQException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 杜名洋
 * 将Producer进行封装，使得可以使用spring来创建一个producer
 **/
public class TradeMQProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeMQProducer.class);
    private DefaultMQProducer producer;
    /**
     * Name Server集群地址
     */
    private String namesrvAddr;
    /**
     * producer的组名称
     */
    private String groupName;
    /**
     * 发送的messgae最大大小
     */
    private int maxMessageSize = 1024 * 1024 * 4;
    /**
     * 最大等待时长
     */
    private int sendMsgTimeout = 10000;

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public void setSendMsgTimeout(int sendMsgTimeout) {
        this.sendMsgTimeout = sendMsgTimeout;
    }

    /**
     * 用于初始化producer，spring中负责执行该方法
     * @throws TradeMQException
     */
    public void init() throws TradeMQException {
        if (StringUtils.isEmpty(groupName)) {
            throw new TradeMQException("groupName is blank!");
        }
        if (StringUtils.isEmpty(namesrvAddr)) {
            throw new TradeMQException("namesrvAddr is blank!");
        }
        this.producer = new DefaultMQProducer(this.groupName);
        this.producer.setNamesrvAddr(this.namesrvAddr);
        try {
            this.producer.start();
            LOGGER.info(String.format("producer start!groupName:[%s],namesrvAddr:[%s]", this.groupName, this.namesrvAddr));
        } catch (MQClientException e) {
            LOGGER.error(String.format("producer error!groupName:[%s],namesrvAddr:[%s]", this.groupName, this.namesrvAddr), e);
            throw new TradeMQException(e);
        }
    }

    public SendResult sendMessage(String topic, String tags, String keys, String body)
            throws TradeMQException {
        if (StringUtils.isEmpty(topic)) {
            throw new TradeMQException("topic is blank!");
        }
        if (StringUtils.isEmpty(body)) {
            throw new TradeMQException("body is blank!");
        }
        Message message = new Message(topic, tags, keys, body.getBytes());
        try {
            SendResult sendResult = this.producer.send(message);
            return sendResult;
        } catch (Exception e) {
            LOGGER.error("send message error:{}", e.getMessage(), e);
            throw new TradeMQException(e);
        }
    }

    public SendResult sendMessage(MQEnums.TopicEnum topicEnum, String keys, String body)
            throws TradeMQException {
       return this.sendMessage(topicEnum.getTopic(), topicEnum.getTag(), keys, body);
    }

}
