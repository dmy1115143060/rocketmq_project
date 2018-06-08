package com.rocketmq.trade.order.api;

import com.rocketmq.trade.common.api.IOrderApi;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderReq;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderRes;
import com.rocketmq.trade.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 杜名洋
 * 订单服务实现实现类，服务器调用
 **/
@Controller
public class OrderApiImpl implements IOrderApi {

    @Autowired
    private IOrderService orderService;

    /**
     * 实现订单服务
     * @param confirmOrderReq 下订单时传入的参数对象
     * @return 订单结果
     */
    @RequestMapping(value = "/confirmOrder", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ConfirmOrderRes confirmOrder(@RequestBody ConfirmOrderReq confirmOrderReq) {
        return this.orderService.confirmOrder(confirmOrderReq);
    }
}
