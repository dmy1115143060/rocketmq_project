package com.rocketmq.trade.common.protocol.pay;

/**
 * @author Mingyang Du
 **/
public class CallbackPaymentReq {

    private String payId;
    private String isPaid;

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }
}
