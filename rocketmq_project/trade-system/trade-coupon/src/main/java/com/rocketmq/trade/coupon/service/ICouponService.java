package com.rocketmq.trade.coupon.service;

import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;

/**
 * @author Mingyang Du
 * 代金券服务接口
 */
public interface ICouponService {

    /**
     * 查询代金券信息
     *
     * @param queryCouponReq 查询参数
     * @return 查询结果
     */
    QueryCouponRes queryCoupon(QueryCouponReq queryCouponReq);

    /**
     * 改变代金券状态
     *
     * @param changeCouponStatusReq 请求参数对象
     * @return 改变结果
     */
    ChangeCouponStatusRes changeCouponStatus(ChangeCouponStatusReq changeCouponStatusReq);
}
