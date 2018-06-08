package com.rocketmq.trade.common.protocol.goods;

import com.rocketmq.trade.common.protocol.BaseRes;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杜名洋
 * 商品的返回信息
 **/
public class QueryGoodsRes extends BaseRes {
    /**
     * 商品Id
     */
    private Integer goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品库存
     */
    private Integer goodsNumber;

    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品上架时间
     */
    private Date addTime;

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public BigDecimal getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(BigDecimal goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Override
    public String toString() {
        return "QueryGoodsRes{" +
                "goodsId=" + goodsId +
                ", goodsName='" + goodsName + '\'' +
                ", goodsNumber=" + goodsNumber +
                ", goodsPrice=" + goodsPrice +
                ", goodsDesc='" + goodsDesc + '\'' +
                ", addTime=" + addTime +
                '}';
    }
}
