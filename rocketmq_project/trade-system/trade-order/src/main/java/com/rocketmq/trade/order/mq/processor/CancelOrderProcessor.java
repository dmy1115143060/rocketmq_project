package com.rocketmq.trade.order.mq.processor;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.mq.CancelOrderMessage;
import com.rocketmq.trade.common.rocketmq.IMessageProcessor;
import com.rocketmq.trade.entity.TradeOrder;
import com.rocketmq.trade.mapper.TradeOrderMapper;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 杜名洋
 * 对于订单模块而言，其本身也可以收单取消订单消息，此时该模块需要将数据库中的订单状态改为取消订单
 **/
public class CancelOrderProcessor implements IMessageProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    /**
     * order模块对于取消订单的处理逻辑：将数据库中的用户订单状态修改为取消
     * @param messageExt 得到的消息
     * @return
     */
    @Override
    public boolean handleMessage(MessageExt messageExt) {
        try {
            // 获得取消订单的消息
            String messageBody = new String(messageExt.getBody(), "utf-8");
            String messageId = messageExt.getMsgId();
            String messgaeTags = messageExt.getTags();
            String messgaeKeys = messageExt.getKeys();
            LOGGER.info("order CancelOrderProcessor receive messgae: " + messageExt);

            // 将接收到的消息转换为封装好的CancelOrderMessage对象
            CancelOrderMessage cancelOrderMessage = JSON.parseObject(messageBody, CancelOrderMessage.class);

            TradeOrder record = new TradeOrder();
            record.setOrderId(cancelOrderMessage.getOrderId());
            // 更改订单状态为取消订单
            record.setOrderStatus(TradeEnums.OrderStatusEnum.CANCEL.getCode());
            tradeOrderMapper.updateByPrimaryKey(record);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
