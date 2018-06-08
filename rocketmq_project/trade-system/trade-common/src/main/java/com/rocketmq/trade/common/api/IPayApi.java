package com.rocketmq.trade.common.api;

import com.rocketmq.trade.common.protocol.pay.CallbackPaymentReq;
import com.rocketmq.trade.common.protocol.pay.CallbackPaymentRes;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentReq;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentRes;

/**
 * @author 杜名洋
 * 供其他模块使用的公用支付服务接口
 */
public interface IPayApi {

    /**
     * 创建支付订单
     * @param createPaymentReq 请求参数对象
     * @return 创建结果
     */
    CreatePaymentRes createPayment(CreatePaymentReq createPaymentReq);

    /**
     * 供第三方回调的接口
     * @param callbackPaymentReq 请求参数对象
     * @return 返回结果
     */
    CallbackPaymentRes callbackPayment(CallbackPaymentReq callbackPaymentReq);
}
