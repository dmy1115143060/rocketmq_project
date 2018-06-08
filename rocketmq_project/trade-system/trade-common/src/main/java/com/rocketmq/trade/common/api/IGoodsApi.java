package com.rocketmq.trade.common.api;

import com.rocketmq.trade.common.protocol.goods.QueryGoodsReq;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsRes;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumReq;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumRes;

/**
 * @author 杜名洋
 * 供其他模块使用的公用商品服务接口
 */
public interface IGoodsApi {

    /**
     * 查询商品
     * @param queryGoodsReq 查询商品的请求参数
     * @return 查询结果
     */
    QueryGoodsRes queryGoods(QueryGoodsReq queryGoodsReq);

    /**
     * 修改商品库存服务接口
     * @param reduceGoodsNumReq 修改商品库存参数对象
     * @return 修改结果
     */
    ReduceGoodsNumRes reduceGoodsNum(ReduceGoodsNumReq reduceGoodsNumReq);
}
