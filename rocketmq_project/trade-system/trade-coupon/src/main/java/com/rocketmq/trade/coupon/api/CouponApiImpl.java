package com.rocketmq.trade.coupon.api;

import com.rocketmq.trade.common.api.ICouponApi;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;
import com.rocketmq.trade.coupon.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mingyang Du
 * 供其他模块调用的优惠券服务实现
 **/
@Controller
public class CouponApiImpl implements ICouponApi {

    @Autowired
    private ICouponService couponService;

    /**
     * 查询代金券信息
     * @param queryCouponReq 查询参数
     * @return 查询结果
     */
    @RequestMapping(value = "/queryCoupon", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public QueryCouponRes queryCoupon(@RequestBody QueryCouponReq queryCouponReq) {
        return couponService.queryCoupon(queryCouponReq);
    }

    /**
     * 改变代金券状态
     * @param changeCouponStatusReq 请求参数对象
     * @return 改变结果
     */
    @RequestMapping(value = "/changeCouponStatus", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ChangeCouponStatusRes changeCouponStatus(@RequestBody ChangeCouponStatusReq changeCouponStatusReq) {
        return couponService.changeCouponStatus(changeCouponStatusReq);
    }
}
