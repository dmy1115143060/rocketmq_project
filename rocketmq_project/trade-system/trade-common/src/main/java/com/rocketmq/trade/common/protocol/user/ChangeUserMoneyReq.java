package com.rocketmq.trade.common.protocol.user;

import java.math.BigDecimal;

/**
 * @author 杜名洋
 * 修改用户余额参数类
 **/
public class ChangeUserMoneyReq {
    /**
     * 用户Id
     */
    private Integer userId;
    /**
     * 修改量
     */
    private BigDecimal userMoney;
    /**
     * 记录日志类型：支付成功，退款等
     */
    private String moneyLogType;
    /**
     * 支付的订单Id
     */
    private String orderId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getUserMoney() {
        return userMoney;
    }

    public void setUserMoney(BigDecimal userMoney) {
        this.userMoney = userMoney;
    }

    public String getMoneyLogType() {
        return moneyLogType;
    }

    public void setMoneyLogType(String moneyLogType) {
        this.moneyLogType = moneyLogType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
