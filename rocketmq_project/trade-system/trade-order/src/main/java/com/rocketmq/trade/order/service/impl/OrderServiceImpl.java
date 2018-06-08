package com.rocketmq.trade.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.api.ICouponApi;
import com.rocketmq.trade.common.api.IGoodsApi;
import com.rocketmq.trade.common.api.IUserApi;
import com.rocketmq.trade.common.constants.MQEnums;
import com.rocketmq.trade.common.constants.TradeConstants;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.exception.OrderException;
import com.rocketmq.trade.common.exception.TradeMQException;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsReq;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsRes;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumReq;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumRes;
import com.rocketmq.trade.common.protocol.mq.CancelOrderMessage;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusReq;
import com.rocketmq.trade.common.protocol.order.ChangeCouponStatusRes;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderReq;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderRes;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import com.rocketmq.trade.common.rocketmq.TradeMQProducer;
import com.rocketmq.trade.common.util.IDGenerator;
import com.rocketmq.trade.entity.TradeOrder;
import com.rocketmq.trade.mapper.TradeOrderMapper;
import com.rocketmq.trade.order.service.IOrderService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Mingyang Du
 * 实际的服务实现
 **/
@Service
public class OrderServiceImpl implements IOrderService {

    /**
     * 远程提供的商品服务
     */
    @Autowired
    private IGoodsApi goodsApi;

    /**
     * 远程提供的优惠券服务
     */
    @Autowired
    private ICouponApi couponApi;

    /**
     * 远程提供的用户服务
     */
    @Autowired
    private IUserApi userApi;

    /**
     * 操作订单实体类
     */
    @Autowired
    private TradeOrderMapper tradeOrderMapper;

    /**
     * RocketMQ生产者实现类
     */
    @Autowired
    private TradeMQProducer tradeMQProducer;

    /**
     * 订单服务实现方法
     *
     * @param confirmOrderReq 下订单时传入的参数对象
     * @return
     */
    @Override
    public ConfirmOrderRes confirmOrder(ConfirmOrderReq confirmOrderReq) {
        ConfirmOrderRes confirmOrderRes = new ConfirmOrderRes();
        confirmOrderRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        confirmOrderRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 调用远程服务查看商品库存信息
            QueryGoodsReq queryGoodsReq = new QueryGoodsReq();
            queryGoodsReq.setGoodsId(confirmOrderReq.getGoodsId());
            QueryGoodsRes queryGoodsRes = goodsApi.queryGoods(queryGoodsReq);
            // 1、参数校验
            checkConfirmOrder(confirmOrderReq, queryGoodsRes);
            // 2、创建用户不可见订单
            String orderId = saveNoConfirmOrder(confirmOrderReq);
            // 3、调用远程服务，扣优惠券，扣库存，如果调用成功 -》 更改订单为用户可见，失败 -》 发送MQ消息，进行取消订单
            callRemoteService(confirmOrderReq, orderId);
        } catch (Exception e) {
            confirmOrderRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            confirmOrderRes.setReturnInfo(e.getMessage());
        }
        return confirmOrderRes;
    }

    /**
     * 订单参数检查
     *
     * @param confirmOrderReq 商品订单参数信息
     * @param queryGoodsRes   远程调用查询商品结果
     */
    private void checkConfirmOrder(ConfirmOrderReq confirmOrderReq, QueryGoodsRes queryGoodsRes) {
        if (confirmOrderReq == null) {
            throw new OrderException("OrderServiceImpl 114 : 订单不能为空!");
        }
        if (confirmOrderReq.getUserId() == null) {
            throw new OrderException("OrderServiceImpl 117 : 用户ID不能为空!");
        }
        if (confirmOrderReq.getGoodsId() == null) {
            throw new OrderException("OrderServiceImpl 120 : 商品ID不能为空!");
        }
        if (confirmOrderReq.getGoodsNumber() == null || confirmOrderReq.getGoodsNumber() <= 0) {
            throw new OrderException("OrderServiceImpl 123 : 购买的商品数量不能为0!");
        }
        if (confirmOrderReq.getAddress() == null) {
            throw new OrderException("OrderServiceImpl 126 : 收货地址不能为空!");
        }
        if (queryGoodsRes == null || queryGoodsRes.getReturnCode().equals(TradeEnums.RetEnum.FAIL.getCode())) {
            throw new OrderException("OrderServiceImpl 129 : 该商品不存在: [" + confirmOrderReq.getGoodsId() + "]!" + JSON.toJSONString(queryGoodsRes));
        }
        if (queryGoodsRes.getGoodsNumber() < confirmOrderReq.getGoodsNumber()) {
            throw new OrderException("OrderServiceImpl 132 : 商品库存不足!");
        }
        if ((queryGoodsRes.getGoodsPrice().compareTo(confirmOrderReq.getGoodsPrice()) != 0)) {
            throw new OrderException("OrderServiceImpl 135 : 当前商品价格发生变化，请重新购买!");
        }
        if (confirmOrderReq.getShippingFee() == null) {
            confirmOrderReq.setShippingFee(BigDecimal.ZERO);
        }
        if (confirmOrderReq.getOrderAmount() == null) {
            confirmOrderReq.setOrderAmount(BigDecimal.ZERO);
        }
    }

    /**
     * 保存不可见的订单
     *
     * @param confirmOrderReq
     */
    private String saveNoConfirmOrder(ConfirmOrderReq confirmOrderReq) {

        // 初始化订单信息
        TradeOrder tradeOrder = new TradeOrder();
        String orderId = IDGenerator.generatorUUID();
        tradeOrder.setOrderId(orderId);
        tradeOrder.setUserId(confirmOrderReq.getUserId());
        tradeOrder.setOrderStatus(TradeEnums.OrderStatusEnum.NO_CONFIRM.getCode());
        tradeOrder.setPayStatus(TradeEnums.PayStatusEnum.NO_PAY.getCode());
        tradeOrder.setShippingStatus(TradeEnums.ShippingStatusEnum.NO_SHIP.getCode());
        tradeOrder.setAddress(confirmOrderReq.getAddress());
        tradeOrder.setConsignee(confirmOrderReq.getConsignee());
        tradeOrder.setGoodsId(confirmOrderReq.getGoodsId());
        tradeOrder.setGoodsNumber(confirmOrderReq.getGoodsNumber());
        tradeOrder.setGoodsPrice(confirmOrderReq.getGoodsPrice());

        // 计算商品总价
        BigDecimal goodsAmount = confirmOrderReq.getGoodsPrice()
                .multiply(new BigDecimal(confirmOrderReq.getGoodsNumber()));
        tradeOrder.setGoodsAmount(goodsAmount);

        // 计算运费
        BigDecimal shippingFee = calculateShippingFee(goodsAmount);
        if (confirmOrderReq.getShippingFee().compareTo(shippingFee) != 0) {
            throw new OrderException("OrderServiceImpl 174 : 快递费用不正确!");
        }
        tradeOrder.setShippingFee(calculateShippingFee(goodsAmount));

        // 计算订单总价
        BigDecimal orderAmount = goodsAmount.add(shippingFee);
        if (orderAmount.compareTo(confirmOrderReq.getOrderAmount()) != 0) {
            throw new OrderException("OrderServiceImpl 181 : 订单总价异常!");
        }
        tradeOrder.setOrderAmount(orderAmount);

        // 使用远程服务来调用优惠券相关功能
        String couponId = confirmOrderReq.getCouponId();
        if (StringUtils.isNotEmpty(couponId) || StringUtils.isNoneBlank(couponId)) {
            QueryCouponReq queryCouponReq = new QueryCouponReq();
            queryCouponReq.setCouponId(couponId);
            QueryCouponRes queryCouponRes = couponApi.queryCoupon(queryCouponReq);
            if (queryCouponRes == null || queryCouponRes.getReturnCode().equals(TradeEnums.RetEnum.FAIL.getCode())) {
                throw new OrderException("OrderServiceImpl 192 : 优惠券非法!");
            }
            if (!queryCouponRes.getIsUsed().equals(TradeEnums.YesNoEnum.NO.getCode())) {
                throw new OrderException("OrderServiceImpl 195 : 优惠券已使用!");
            }
            tradeOrder.setCouponId(couponId);
            tradeOrder.setCouponPaid(queryCouponRes.getCouponPrice());
        } else {
            tradeOrder.setMoneyPaid(BigDecimal.ZERO);
        }

        // 使用远程服务来调用用户余额相关信息
        if (confirmOrderReq.getMoneyPaid() != null) {
            int r = confirmOrderReq.getMoneyPaid().compareTo(BigDecimal.ZERO);
            if (r == -1) {
                throw new OrderException("OrderServiceImpl 207 : 余额金额非法!");
            }
            if (r == 1) {
                QueryUserReq queryUserReq = new QueryUserReq();
                queryUserReq.setUserId(confirmOrderReq.getUserId());
                QueryUserRes queryUserRes = userApi.queryUser(queryUserReq);
                if (queryUserRes == null || queryUserRes.getReturnCode().equals(TradeEnums.RetEnum.FAIL.getCode())) {
                    throw new OrderException("OrderServiceImpl 214 : 该用户非法!");
                }
                if (queryUserRes.getUserMoney().compareTo(confirmOrderReq.getMoneyPaid()) == -1) {
                    throw new OrderException("OrderServiceImpl 216 : 用户余额不足!");
                }
                tradeOrder.setMoneyPaid(confirmOrderReq.getMoneyPaid());
            }
        } else {
            tradeOrder.setMoneyPaid(BigDecimal.ZERO);
        }

        BigDecimal payAmount = orderAmount.subtract(tradeOrder.getCouponPaid()).subtract(tradeOrder.getMoneyPaid());
        tradeOrder.setPayAmount(payAmount);
        tradeOrder.setAddTime(new Date());

        int ret = this.tradeOrderMapper.insert(tradeOrder);
        if (ret != 1) {
            throw new OrderException("OrderServiceImpl 231 : 保存订单失败!");
        }
        return orderId;
    }

    /**
     * 计算当前订单的运费
     *
     * @param goodsAmount 商品费用
     * @return
     */
    private BigDecimal calculateShippingFee(BigDecimal goodsAmount) {
        if (goodsAmount.compareTo(TradeConstants.SHIPPINGFEETHRESHOLD) > 0) {
            return BigDecimal.ZERO;
        } else {
            return TradeConstants.SHIPPINGFEEBASE;
        }
    }

    /**
     * 调用远程服务，扣优惠券，扣库存，如果调用成功 -》 更改订单为用户可见，失败 -》 发送MQ消息，进行取消订单
     * @param confirmOrderReq
     */
    private void callRemoteService(ConfirmOrderReq confirmOrderReq, String orderId) {
        try {
            // 调用远程优惠券服务
            if (!StringUtils.isEmpty(confirmOrderReq.getCouponId())
                    && StringUtils.isNoneBlank(confirmOrderReq.getCouponId())) {
                ChangeCouponStatusReq changeCouponStatusReq = new ChangeCouponStatusReq();
                changeCouponStatusReq.setCouponId(confirmOrderReq.getCouponId());
                changeCouponStatusReq.setIsUsed(TradeEnums.YesNoEnum.Yes.getCode());
                changeCouponStatusReq.setOrderId(orderId);
                ChangeCouponStatusRes changeCouponStatusRes = couponApi.changeCouponStatus(changeCouponStatusReq);
                if (!changeCouponStatusRes.getReturnCode().equals(TradeEnums.RetEnum.SUCCESS.getCode())) {
                    throw new Exception("OrderServiceImpl 265 : 使用优惠券失败!");
                }
            }

            // 远程调用用户服务，扣除余额
            if (confirmOrderReq.getMoneyPaid() != null && confirmOrderReq.getMoneyPaid().compareTo(BigDecimal.ZERO) == 1) {
                ChangeUserMoneyReq changeUserMoneyReq = new ChangeUserMoneyReq();
                changeUserMoneyReq.setOrderId(orderId);
                changeUserMoneyReq.setUserId(confirmOrderReq.getUserId());
                changeUserMoneyReq.setUserMoney(confirmOrderReq.getMoneyPaid());
                changeUserMoneyReq.setMoneyLogType(TradeEnums.UserMoneyLogTypeEnum.PAIED.getCode());
                ChangeUserMoneyRes changeUserMoneyRes = userApi.changeUserMoney(changeUserMoneyReq);
                if (!changeUserMoneyRes.getReturnCode().equals(TradeEnums.RetEnum.SUCCESS.getCode())) {
                    throw new Exception("OrderServiceImpl 278 : 扣用户余额失败!");
                }
            }

            // 扣除商品库存
            if (confirmOrderReq.getGoodsNumber() != null && confirmOrderReq.getGoodsNumber() > 0) {
                ReduceGoodsNumReq reduceGoodsNumReq = new ReduceGoodsNumReq();
                reduceGoodsNumReq.setOrderId(orderId);
                reduceGoodsNumReq.setGoodsId(confirmOrderReq.getGoodsId());
                reduceGoodsNumReq.setGoodsNum(confirmOrderReq.getGoodsNumber());
                ReduceGoodsNumRes reduceGoodsNumRes = goodsApi.reduceGoodsNum(reduceGoodsNumReq);
                if (!reduceGoodsNumRes.getReturnCode().equals(TradeEnums.RetEnum.SUCCESS.getCode())) {
                    throw new Exception("OrderServiceImpl 290 : 扣库存失败!");
                }
            }

            // 更改订单状态
            TradeOrder tradeOrder = new TradeOrder();
            tradeOrder.setOrderId(orderId);
            tradeOrder.setOrderStatus(TradeEnums.OrderStatusEnum.CONFIRM.getCode());
            tradeOrder.setAddTime(new Date());
            int i = tradeOrderMapper.updateByPrimaryKeySelective(tradeOrder);
            if (i <= 0) {
                throw new Exception("OrderServiceImpl 301 : 更改订单状态失败!");
            }
        }
        // 出现异常，此时需要进行回退操作，如优惠券的重置，用户余额的还原等
        catch (Exception e) {
            // 订单出现异常，发送MQ消息，将发送的MQ消息封装成一个CancleOrderMessage对象
            CancelOrderMessage cancelOrderMessage = new CancelOrderMessage();
            cancelOrderMessage.setOrderId(orderId);
            cancelOrderMessage.setUserId(confirmOrderReq.getUserId());
            cancelOrderMessage.setCouponId(confirmOrderReq.getCouponId());
            cancelOrderMessage.setGoodsId(confirmOrderReq.getGoodsId());
            cancelOrderMessage.setGoodsNum(confirmOrderReq.getGoodsNumber());
            cancelOrderMessage.setUserMoney(confirmOrderReq.getMoneyPaid());
            try {
                // 发送消息给MQ
                SendResult sendResult = this.tradeMQProducer.sendMessage(MQEnums.TopicEnum.ORDER_CANCEL
                        , orderId
                        , JSON.toJSONString(cancelOrderMessage));
                System.out.println(sendResult);
            } catch (TradeMQException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(e.getMessage());
        }
    }
}
