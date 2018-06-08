package com.rocketmq.trade.user.mq.processor;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.mq.CancelOrderMessage;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.rocketmq.IMessageProcessor;
import com.rocketmq.trade.user.service.IUserService;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * @author Mingyang Du
 * user模块收到MQ的取消订单消息处理实现类
 **/
public class CancelOrderProcessor implements IMessageProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private IUserService userService;

    /**
     * user模块接收到MQ的取消订单消息后的处理逻辑
     * @param messageExt 得到的消息
     * @return 处理的结果 true(成功) false(失败)
     */
    @Override
    public boolean handleMessage(MessageExt messageExt) {
        try {
            // 获得取消订单消息的相关属性
            String messageBody = new String(messageExt.getBody(), "utf-8");
            String messageId = messageExt.getMsgId();
            String messgaeTags = messageExt.getTags();
            String messgaeKeys = messageExt.getKeys();

            // 将接收到的消息转换为封装好的CancelOrderMessage对象
            CancelOrderMessage cancelOrderMessage = JSON.parseObject(messageBody, CancelOrderMessage.class);
            LOGGER.info("user CancelOrderProcessor receive messgae: " + messageExt);
            if (cancelOrderMessage != null && cancelOrderMessage.getUserMoney().compareTo(BigDecimal.ZERO) == 1) {
                ChangeUserMoneyReq changeUserMoneyReq = new ChangeUserMoneyReq();
                changeUserMoneyReq.setUserId(cancelOrderMessage.getUserId());
                changeUserMoneyReq.setMoneyLogType(TradeEnums.UserMoneyLogTypeEnum.RETURNED.getCode());
                changeUserMoneyReq.setOrderId(cancelOrderMessage.getOrderId());
                changeUserMoneyReq.setUserMoney(cancelOrderMessage.getUserMoney());
                // 修改用户余额，来实现退款操作
                this.userService.changeUserMoney(changeUserMoneyReq);
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
