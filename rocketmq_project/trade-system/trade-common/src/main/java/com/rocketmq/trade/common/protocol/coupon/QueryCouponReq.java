package com.rocketmq.trade.common.protocol.coupon;

/**
 * @author 杜名洋
 * 查询优惠券请求参数
 **/
public class QueryCouponReq {
    /**
     * 优惠券ID
     */
    private String couponId;

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }
}
