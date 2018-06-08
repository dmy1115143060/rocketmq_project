package com.rocketmq.trade.common.protocol.order;

import java.math.BigDecimal;

/**
 * @author 杜名洋
 * 封装下订单时的传入参数对象
 **/
public class ConfirmOrderReq {
    /**
     * 用户Id
     */
    private Integer userId;
    /**
     * 收货地址
     */
    private String address;
    /**
     * 收货人
     */
    private String consignee;
    /**
     * 购买的商品Id
     */
    private Integer goodsId;
    /**
     * 购买的商品数量
     */
    private Integer goodsNumber;
    /**
     * 优惠券ID
     */
    private String couponId;
    /**
     * 余额支付
     */
    private BigDecimal moneyPaid;
    /**
     * 物品单价
     */
    private BigDecimal goodsPrice;
    /**
     * 运费
     */
    private BigDecimal shippingFee;
    /**
     * 订单总价
     */
    private BigDecimal orderAmount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public BigDecimal getMoneyPaid() {
        return moneyPaid;
    }

    public void setMoneyPaid(BigDecimal moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }
}
