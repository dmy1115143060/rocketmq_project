package com.rocketmq.trade.common.protocol.mq;

import java.math.BigDecimal;

/**
 * @author Mingyang Du
 * 支付模块发送的MQ消息
 **/
public class PaidMQ {

    private String payId;
    private String orderId;
    private BigDecimal payAmount;

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }
}
