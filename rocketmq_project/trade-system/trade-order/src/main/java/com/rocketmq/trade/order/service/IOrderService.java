package com.rocketmq.trade.order.service;

import com.rocketmq.trade.common.protocol.order.ConfirmOrderReq;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderRes;

/**
 * 提供的订单服务
 */
public interface IOrderService {
    /**
     * 下订单操作接口
     * @param confirmOrderReq 下订单时传入的参数对象
     * @return 下订单操作的返回结果
     */
    ConfirmOrderRes confirmOrder(ConfirmOrderReq confirmOrderReq);
}
