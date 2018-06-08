package com.rocketmq.trade.pay.api;

import com.rocketmq.trade.common.api.IPayApi;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.pay.CallbackPaymentReq;
import com.rocketmq.trade.common.protocol.pay.CallbackPaymentRes;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentReq;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentRes;
import com.rocketmq.trade.pay.service.IPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mingyang Du
 **/
@Controller
public class PayApiImpl implements IPayApi {

    @Autowired
    private IPayService payService;

    /**
     * 查询支付订单信息
     * @param createPaymentReq 查询商品的请求参数
     * @return 查询结果
     */
    @RequestMapping(value = "/createPayment", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public CreatePaymentRes createPayment(CreatePaymentReq createPaymentReq) {
        return payService.createPayment(createPaymentReq);
    }

    /**
     * @param callbackPaymentReq
     * @return 查询结果
     */
    @RequestMapping(value = "/callbackPayment", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public CallbackPaymentRes callbackPayment(CallbackPaymentReq callbackPaymentReq) {
        CallbackPaymentRes callbackPaymentRes = new CallbackPaymentRes();
        try {
            callbackPaymentRes = this.payService.callbackPayment(callbackPaymentReq);
        } catch (Exception e) {
            callbackPaymentRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            callbackPaymentRes.setReturnInfo(e.getMessage());
        }
        return callbackPaymentRes;
    }
}
