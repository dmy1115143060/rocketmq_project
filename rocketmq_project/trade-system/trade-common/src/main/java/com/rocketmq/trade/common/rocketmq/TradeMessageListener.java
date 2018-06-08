package com.rocketmq.trade.common.rocketmq;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @author 杜名洋
 * 实现消息处理的接口
 **/
public class TradeMessageListener implements MessageListenerConcurrently {

    /**
     * 真正实现消息处理的接口
     */
    private IMessageProcessor processor;

    public void setProcessor(IMessageProcessor processor) {
        this.processor = processor;
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
                                                    ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt messageExt : list) {
            boolean result = processor.handleMessage(messageExt);
            if (!result) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
