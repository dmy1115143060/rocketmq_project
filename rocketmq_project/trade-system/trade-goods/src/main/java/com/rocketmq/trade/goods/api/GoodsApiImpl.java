package com.rocketmq.trade.goods.api;

import com.rocketmq.trade.common.api.IGoodsApi;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsReq;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsRes;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumReq;
import com.rocketmq.trade.common.protocol.goods.ReduceGoodsNumRes;
import com.rocketmq.trade.goods.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Mingyang Du
 * 供其他模块调用的商品服务实现
 **/
@Controller
public class GoodsApiImpl implements IGoodsApi {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 查询商品信息
     * @param queryGoodsReq 查询商品的请求参数
     * @return 查询结果
     */
    @RequestMapping(value = "/queryGoods", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public QueryGoodsRes queryGoods(@RequestBody QueryGoodsReq queryGoodsReq) {
        return this.goodsService.queryGoods(queryGoodsReq);
    }

    /**
     * 修改商品库存
     * @param reduceGoodsNumReq 修改商品库存参数对象
     * @return 修改结果
     */
    @RequestMapping(value = "/reduceGoodsNum", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ReduceGoodsNumRes reduceGoodsNum(@RequestBody ReduceGoodsNumReq reduceGoodsNumReq) {
        ReduceGoodsNumRes reduceGoodsNumRes = new ReduceGoodsNumRes();
        try {
            reduceGoodsNumRes = this.goodsService.reduceGoodsNum(reduceGoodsNumReq);
        } catch (Exception e) {
            reduceGoodsNumRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            reduceGoodsNumRes.setReturnInfo(e.getMessage());
        }
        return reduceGoodsNumRes;
    }
}
