package com.rocketmq.trade.common.api;

import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;

/**
 * @author 杜名洋
 * 供其他模块使用的公用优惠券服务接口
 */
public interface ICouponApi {

    /**
     * 查询优惠券
     * @param queryCouponReq 查询参数
     * @return 查询结果
     */
    QueryCouponRes queryCoupon(QueryCouponReq queryCouponReq);

    /**
     * 改变优惠券状态的接口方法，使用优惠券后需要改变优惠券的使用状态，
     * 当取消订单或者支付失败时也需要改变优惠券状态为原始状态。
     * @param changeCouponStatusReq 请求参数对象
     * @return 操作结果
     */
    ChangeCouponStatusRes changeCouponStatus(ChangeCouponStatusReq changeCouponStatusReq);
}
