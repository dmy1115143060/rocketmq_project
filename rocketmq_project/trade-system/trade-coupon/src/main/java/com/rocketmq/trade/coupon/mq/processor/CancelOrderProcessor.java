package com.rocketmq.trade.coupon.mq.processor;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.mq.CancelOrderMessage;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;
import com.rocketmq.trade.common.rocketmq.IMessageProcessor;
import com.rocketmq.trade.coupon.service.ICouponService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.UnsupportedEncodingException;

/**
 * @author Mingyang Du
 * coupon模块收到MQ的取消订单消息处理实现类
 **/
public class CancelOrderProcessor implements IMessageProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private ICouponService couponService;

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
            LOGGER.info("coupon CancelOrderProcessor receive messgae: " + messageExt);

            // 将接收到的消息转换为封装好的CancelOrderMessage对象
            CancelOrderMessage cancelOrderMessage = JSON.parseObject(messageBody, CancelOrderMessage.class);
            if (StringUtils.isNoneBlank(cancelOrderMessage.getCouponId())) {
                // 封装请求体，并请求更新优惠券信息
                ChangeCouponStatusReq changeCouponStatusReq = new ChangeCouponStatusReq();
                changeCouponStatusReq.setOrderId(cancelOrderMessage.getOrderId());
                changeCouponStatusReq.setCouponId(cancelOrderMessage.getCouponId());
                changeCouponStatusReq.setIsUsed(TradeEnums.YesNoEnum.NO.getCode());
                ChangeCouponStatusRes changeCouponStatusRes = this.couponService.changeCouponStatus(changeCouponStatusReq);
            }
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
