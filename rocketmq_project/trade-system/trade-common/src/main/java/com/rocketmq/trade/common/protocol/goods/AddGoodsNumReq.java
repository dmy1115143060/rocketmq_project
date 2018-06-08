package com.rocketmq.trade.common.protocol.goods;

/**
 * @author 杜名洋
 * 增加商品库存的参数类
 **/
public class AddGoodsNumReq {
    /**
     * 商品Id
     */
    private Integer goodsId;
    /**
     * 修改数量
     */
    private Integer goodsNum;
    /**
     * 订单Id
     */
    private String orderId;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
