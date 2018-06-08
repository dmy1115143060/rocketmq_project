package com.rocketmq.trade.common.protocol.coupon;

import com.rocketmq.trade.common.protocol.BaseRes;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杜名洋
 * 优惠券请求返回值
 **/
public class QueryCouponRes extends BaseRes {
    /**
     * 优惠券ID
     */
    private String couponId;

    /**
     * 优惠券对应金额
     */
    private BigDecimal couponPrice;

    /**
     * 绑定的用户ID
     */
    private Integer userId;

    /**
     * 对应的订单ID
     */
    private String orderId;

    /**
     * 是否被使用
     */
    private String isUsed;

    /**
     * 使用时间
     */
    private Date usedTime;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(BigDecimal couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }
}
