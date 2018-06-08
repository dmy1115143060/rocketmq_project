package com.rocketmq.trade.goods.service;

import com.rocketmq.trade.common.protocol.goods.*;

/**
 * @author 杜名洋
 **/
public interface IGoodsService {

    /**
     * 查询商品
     * @param queryGoodsReq 查询商品的请求参数
     * @return 查询结果
     */
    QueryGoodsRes queryGoods(QueryGoodsReq queryGoodsReq);

    /**
     * 减少商品库存服务接口
     * @param reduceGoodsNumReq 修改商品库存参数对象
     * @return 修改结果
     */
    ReduceGoodsNumRes reduceGoodsNum(ReduceGoodsNumReq reduceGoodsNumReq);

    /**
     * 增加商品库存服务接口
     * @param reduceGoodsNumReq 修改商品库存参数对象
     * @return 修改结果
     */
    AddGoodsNumRes addGoodsNum(AddGoodsNumReq reduceGoodsNumReq);
}
