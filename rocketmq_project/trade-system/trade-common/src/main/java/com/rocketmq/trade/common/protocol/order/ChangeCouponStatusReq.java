package com.rocketmq.trade.common.protocol.order;

/**
 * @author 杜名洋
 * 改变优惠券参数对象
 **/
public class ChangeCouponStatusReq {

    private String couponId;
    private String orderId;
    private String isUsed;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
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
}
