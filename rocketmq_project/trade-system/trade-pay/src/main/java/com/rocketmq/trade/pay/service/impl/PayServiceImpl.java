package com.rocketmq.trade.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.constants.MQEnums;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.exception.TradeMQException;
import com.rocketmq.trade.common.protocol.mq.PaidMQ;
import com.rocketmq.trade.common.protocol.pay.CallbackPaymentReq;
import com.rocketmq.trade.common.protocol.pay.CallbackPaymentRes;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentReq;
import com.rocketmq.trade.common.protocol.pay.CreatePaymentRes;
import com.rocketmq.trade.common.rocketmq.TradeMQProducer;
import com.rocketmq.trade.common.util.IDGenerator;
import com.rocketmq.trade.entity.TradeMqProducerTemp;
import com.rocketmq.trade.entity.TradeMqProducerTempKey;
import com.rocketmq.trade.entity.TradePay;
import com.rocketmq.trade.entity.TradePayExample;
import com.rocketmq.trade.mapper.TradeMqProducerTempMapper;
import com.rocketmq.trade.mapper.TradePayMapper;
import com.rocketmq.trade.pay.service.IPayService;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Mingyang Du
 **/
public class PayServiceImpl implements IPayService {

    @Autowired
    private TradePayMapper tradePayMapper;

    @Autowired
    private TradeMqProducerTempMapper tradeMqProducerTempMapper;

    @Autowired
    private TradeMQProducer tradeMQProducer;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public CreatePaymentRes createPayment(CreatePaymentReq createPaymentReq) {
        CreatePaymentRes createPaymentRes = new CreatePaymentRes();
        createPaymentRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        createPaymentRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 判断订单是否已经被支付
            TradePayExample payExample = new TradePayExample();
            payExample.createCriteria()
                    .andOrderIdEqualTo(createPaymentReq.getOrderId())
                    .andIsPaidEqualTo(TradeEnums.YesNoEnum.Yes.getCode());
            long count = this.tradePayMapper.countByExample(payExample);
            if (count > 0L) {
                throw new Exception("该订单已支付!");
            }
            // 进行创建订单
            String payId = IDGenerator.generatorUUID();
            TradePay tradePay = new TradePay();
            tradePay.setPayId(payId);
            tradePay.setOrderId(createPaymentReq.getOrderId());
            tradePay.setIsPaid(TradeEnums.YesNoEnum.NO.getCode());
            tradePay.setPayAmount(createPaymentReq.getPayAmount());
            tradePayMapper.insert(tradePay);
            System.out.println("创建支付订单成功：" + payId);
        } catch (Exception e) {
            createPaymentRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            createPaymentRes.setReturnInfo(e.getMessage());
        }
        return createPaymentRes;
    }

    /**
     * 生产者发送消息，在第三方支付平台回调此处提供的服务的时候会将支付订单状态变为支付成功
     * 同时会发送一个消息给订单系统，订单系统接收到该消息后会更改订单状态。这里使用事务操作
     * 保证支付订单写入数据库和利用MQ将信息传递出去的事务性
     * @param callbackPaymentReq 请求参数对象
     */
    @Transactional
    @Override
    public CallbackPaymentRes callbackPayment(CallbackPaymentReq callbackPaymentReq) {
        CallbackPaymentRes callbackPaymentRes = new CallbackPaymentRes();
        callbackPaymentRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        callbackPaymentRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        if (callbackPaymentReq.getIsPaid().equals(TradeEnums.YesNoEnum.Yes.getCode())) {
            // 更新支付状态
            TradePay tradePay = this.tradePayMapper.selectByPrimaryKey(callbackPaymentReq.getPayId());
            if (tradePay == null) {
                throw new RuntimeException("未找到该订单!");
            }
            if (tradePay.getIsPaid().equals(TradeEnums.YesNoEnum.Yes.getCode())) {
                throw new RuntimeException("该订单已支付!");
            }
            tradePay.setIsPaid(TradeEnums.YesNoEnum.Yes.getCode());

            // 向本地数据库插入一条支付订单记录
            int i = tradePayMapper.updateByPrimaryKeySelective(tradePay);
            // 发送可靠消息，把MQ的事务转换成本地的一个事务
            if (i == 1) {
                // 封装要发送的MQ消息，当第三方支付模块支付成功时，回调该函数时会发送此消息
                final PaidMQ paidMQ = new PaidMQ();
                paidMQ.setPayAmount(tradePay.getPayAmount());
                paidMQ.setOrderId(tradePay.getOrderId());
                paidMQ.setPayId(tradePay.getPayId());

                final TradeMqProducerTemp mqProducerTemp = new TradeMqProducerTemp();
                mqProducerTemp.setGroupName("payProducerGroup");
                mqProducerTemp.setMsgKeys(tradePay.getPayId());
                mqProducerTemp.setMsgTag(MQEnums.TopicEnum.PAY_PAIED.getTag());
                mqProducerTemp.setMsgBody(JSON.toJSONString(paidMQ));
                mqProducerTemp.setCreateTime(new Date());
                // 向本地producer表插入一条记录，表示用于发送的支付成功消息，和写入支付订单操作绑定在一个事务中
                tradeMqProducerTempMapper.insert(mqProducerTemp);

                // 使用MQ异步发送消息，发送成功并清空本地发送表中的记录。如果这步操作失败，会采用定时任务扫描本地的临时表
                // 来重新发送支付成功的相关消息
                executorService.submit(() -> {
                   try {
                       SendResult sendResult = tradeMQProducer.sendMessage(MQEnums.TopicEnum.PAY_PAIED,
                               paidMQ.getPayId(), JSON.toJSONString(paidMQ));
                       System.out.println(sendResult);
                       if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                           TradeMqProducerTempKey key = new TradeMqProducerTempKey();
                           key.setMsgTag(mqProducerTemp.getMsgTag());
                           key.setMsgKeys(mqProducerTemp.getMsgKeys());
                           key.setGroupName(mqProducerTemp.getGroupName());
                           // 发送成功，将本地消息发送表记录的日志进行删除
                           tradeMqProducerTempMapper.deleteByPrimaryKey(key);
                           System.out.println("删除消息成功!");
                       }
                   } catch (TradeMQException e) {
                       e.printStackTrace();
                   }
                });
            } else {
                throw new RuntimeException("该订单已成功支付!");
            }
        }
        return callbackPaymentRes;
    }
}
