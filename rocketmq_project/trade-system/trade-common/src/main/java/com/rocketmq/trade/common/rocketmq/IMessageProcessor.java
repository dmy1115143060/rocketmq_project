package com.rocketmq.trade.common.rocketmq;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author 杜名洋
 * 真正处理消息的逻辑接口
 */
public interface IMessageProcessor {
    /**
     * 处理消息的方法
     * @param messageExt 得到的消息
     * @return 处理结果
     */
    boolean handleMessage(MessageExt messageExt);
}
