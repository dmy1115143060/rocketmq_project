package com.rocketmq.trade.coupon.service.impl;

import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;
import com.rocketmq.trade.coupon.service.ICouponService;
import com.rocketmq.trade.entity.TradeCoupon;
import com.rocketmq.trade.mapper.TradeCouponMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mingyang Du
 * 具体优惠券服务实现类
 **/
@Service
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private TradeCouponMapper tradeCouponMapper;

    /**
     * 查询优惠券信息
     *
     * @param queryCouponReq 查询参数
     * @return
     */
    @Override
    public QueryCouponRes queryCoupon(QueryCouponReq queryCouponReq) {
        QueryCouponRes queryCouponRes = new QueryCouponRes();
        queryCouponRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        queryCouponRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 首先检查请求参数
            if (queryCouponReq == null || StringUtils.isBlank(queryCouponReq.getCouponId())) {
                throw new Exception("请求参数不正确,优惠券编号为空!");
            }
            // 使用Mybatis从数据库中读取该优惠券
            TradeCoupon tradeCoupon = tradeCouponMapper.selectByPrimaryKey(queryCouponReq.getCouponId());
            if (tradeCoupon != null) {
                // 将参数1中和参数2相同的属性赋值给参数2
                BeanUtils.copyProperties(tradeCoupon, queryCouponRes);
            } else {
                throw new Exception("未查询到该优惠券!");
            }
        } catch (Exception e) {
            queryCouponRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            queryCouponRes.setReturnInfo(e.getMessage());
        }
        return queryCouponRes;
    }

    /**
     * 改变优惠券状态：未使用 =》 已使用  已使用 =》 未使用
     *
     * @param changeCouponStatusReq 请求参数对象
     * @return
     */
    @Override
    public ChangeCouponStatusRes changeCouponStatus(ChangeCouponStatusReq changeCouponStatusReq) {
        ChangeCouponStatusRes changeCouponStatusRes = new ChangeCouponStatusRes();
        changeCouponStatusRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        changeCouponStatusRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 首先判断请求参数
            if (changeCouponStatusReq == null || StringUtils.isBlank(changeCouponStatusReq.getCouponId())) {
                throw new Exception("请求参数不正确,优惠券编号为空!");
            }
            TradeCoupon record = new TradeCoupon();
            record.setCouponId(changeCouponStatusReq.getCouponId());
            record.setOrderId(changeCouponStatusReq.getOrderId());
            // 使用优惠券的前提条件是优惠券未使用
            if (changeCouponStatusReq.getIsUsed().equals(TradeEnums.YesNoEnum.Yes.getCode())) {
                // 调用useCoupon方法改变数据库状态
                int i = this.tradeCouponMapper.useCoupon(record);
                if (i <= 0) {
                    throw new Exception("使用该优惠券失败!");
                }
            }
            // 请求信息为优惠券未使用，则使用unUseCoupon方法更新优惠券状态为未使用
            else if (changeCouponStatusReq.getIsUsed().equals(TradeEnums.YesNoEnum.NO.getCode())) {
                this.tradeCouponMapper.unUseCoupon(record);
            }
        } catch (Exception e) {
            // 抛出异常时，返回操作失败结果
            changeCouponStatusRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            changeCouponStatusRes.setReturnInfo(e.getMessage());
        }
        return changeCouponStatusRes;
    }
}
