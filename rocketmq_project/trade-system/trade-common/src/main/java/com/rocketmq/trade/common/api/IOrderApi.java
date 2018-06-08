package com.rocketmq.trade.common.api;

import com.rocketmq.trade.common.protocol.order.ConfirmOrderReq;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderRes;
/**
 * @author 杜名洋
 * 供其他模块使用的公用订单服务接口
 */
public interface IOrderApi {
    /**
     * 下订单操作接口
     * @param confirmOrderReq 下订单时传入的参数对象
     * @return 下订单操作的返回结果
     */
    ConfirmOrderRes confirmOrder(ConfirmOrderReq confirmOrderReq);
}
