package com.rocketmq.trade.goods.service.impl;

import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.goods.*;
import com.rocketmq.trade.entity.TradeGoods;
import com.rocketmq.trade.entity.TradeGoodsNumberLog;
import com.rocketmq.trade.entity.TradeGoodsNumberLogKey;
import com.rocketmq.trade.goods.service.IGoodsService;
import com.rocketmq.trade.mapper.TradeGoodsMapper;
import com.rocketmq.trade.mapper.TradeGoodsNumberLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Mingyang Du
 * 具体优惠券服务实现类
 **/
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private TradeGoodsMapper tradeGoodsMapper;

    @Autowired
    private TradeGoodsNumberLogMapper tradeGoodsNumberLogMapper;

    /**
     * 查询商品库存信息
     *
     * @param queryGoodsReq 查询商品的请求参数
     * @return 查询结果
     */
    @Override
    public QueryGoodsRes queryGoods(QueryGoodsReq queryGoodsReq) {
        QueryGoodsRes queryGoodsRes = new QueryGoodsRes();
        queryGoodsRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        queryGoodsRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 判断参数信息
            if (queryGoodsReq == null || queryGoodsReq.getGoodsId() == null) {
                throw new Exception("查询商品信息ID不准确!");
            }
            // 查询商品信息
            TradeGoods tradeGoods = this.tradeGoodsMapper.selectByPrimaryKey(queryGoodsReq.getGoodsId());
            if (tradeGoods != null) {
                BeanUtils.copyProperties(tradeGoods, queryGoodsRes);
            } else {
                throw new Exception("未查询到该商品!");
            }
        } catch (Exception e) {
            queryGoodsRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            queryGoodsRes.setReturnInfo(e.getMessage());
        }
        return queryGoodsRes;
    }

    /**
     * 减少商品库存，这里需要保证扣减库存操作和写日志操作的事务性
     *
     * @param reduceGoodsNumReq 修改商品库存参数对象
     * @return 修改状态
     */
    @Transactional
    @Override
    public ReduceGoodsNumRes reduceGoodsNum(ReduceGoodsNumReq reduceGoodsNumReq) {
        ReduceGoodsNumRes reduceGoodsNumRes = new ReduceGoodsNumRes();
        reduceGoodsNumRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        reduceGoodsNumRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        // 判断参数信息
        if (reduceGoodsNumReq == null || reduceGoodsNumReq.getGoodsId() == null
                || reduceGoodsNumReq.getGoodsNum() <= 0) {
            throw new RuntimeException("扣减库存参数不正确!");
        }
        // 执行扣减库存操作
        TradeGoods tradeGoods = new TradeGoods();
        tradeGoods.setGoodsId(reduceGoodsNumReq.getGoodsId());
        tradeGoods.setGoodsNumber(reduceGoodsNumReq.getGoodsNum());
        int i = this.tradeGoodsMapper.reduceGoodsNum(tradeGoods);
        if (i <= 0) {
            throw new RuntimeException("扣减库存操作失败!");
        }
        // 将扣减库存的操作日志记录下来
        TradeGoodsNumberLog tradeGoodsNumberLog = new TradeGoodsNumberLog();
        tradeGoodsNumberLog.setGoodsId(reduceGoodsNumReq.getGoodsId());
        tradeGoodsNumberLog.setGoodsNumber(reduceGoodsNumReq.getGoodsNum());
        tradeGoodsNumberLog.setOrderId(reduceGoodsNumReq.getOrderId());
        tradeGoodsNumberLog.setLogTime(new Date());
        this.tradeGoodsNumberLogMapper.insert(tradeGoodsNumberLog);
        return reduceGoodsNumRes;
    }

    /**
     * 增加商品库存，用于接收到退单消息时调用。这里需要注意的是，对于增加库存的操作，
     * 必须校验是否之前有对该商品扣减库存操作
     *
     * @param addGoodsNumReq 修改商品库存参数对象
     * @return
     */
    @Override
    public AddGoodsNumRes addGoodsNum(AddGoodsNumReq addGoodsNumReq) {
        AddGoodsNumRes addGoodsNumRes = new AddGoodsNumRes();
        addGoodsNumRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        addGoodsNumRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());
        try {
            // 判断参数信息
            if (addGoodsNumReq == null || addGoodsNumReq.getGoodsId() == null
                    || addGoodsNumReq.getGoodsNum() <= 0) {
                throw new Exception("增加库存参数不正确!");
            }
            // 增加库存前需要判断日志中是否有相应的扣减库存记录
            if (addGoodsNumReq.getOrderId() != null) {
                TradeGoodsNumberLogKey tradeGoodsNumberLogKey = new TradeGoodsNumberLogKey();
                tradeGoodsNumberLogKey.setGoodsId(addGoodsNumReq.getGoodsId());
                tradeGoodsNumberLogKey.setOrderId(addGoodsNumReq.getOrderId());
                TradeGoodsNumberLog tradeGoodsNumberLog =
                        this.tradeGoodsNumberLogMapper.selectByPrimaryKey(tradeGoodsNumberLogKey);
                if (tradeGoodsNumberLog == null) {
                    throw new Exception("未找到扣库存记录!");
                }
            }
            TradeGoods tradeGoods = new TradeGoods();
            tradeGoods.setGoodsId(addGoodsNumReq.getGoodsId());
            tradeGoods.setGoodsNumber(addGoodsNumReq.getGoodsNum());
            int i = this.tradeGoodsMapper.addGoodsNum(tradeGoods);
            if (i <= 0) {
                throw new Exception("增加库存操作失败!");
            }
        } catch (Exception e) {
            addGoodsNumRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            addGoodsNumRes.setReturnInfo(e.getMessage());
        }
        return addGoodsNumRes;
    }
}
