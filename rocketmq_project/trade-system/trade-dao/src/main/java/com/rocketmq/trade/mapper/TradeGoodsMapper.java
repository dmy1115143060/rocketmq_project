package com.rocketmq.trade.mapper;

import com.rocketmq.trade.entity.TradeGoods;
import com.rocketmq.trade.entity.TradeGoodsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TradeGoodsMapper {
    long countByExample(TradeGoodsExample example);

    int deleteByExample(TradeGoodsExample example);

    int deleteByPrimaryKey(Integer goodsId);

    int insert(TradeGoods record);

    int insertSelective(TradeGoods record);

    List<TradeGoods> selectByExample(TradeGoodsExample example);

    TradeGoods selectByPrimaryKey(Integer goodsId);

    int updateByExampleSelective(@Param("record") TradeGoods record, @Param("example") TradeGoodsExample example);

    int updateByExample(@Param("record") TradeGoods record, @Param("example") TradeGoodsExample example);

    int updateByPrimaryKeySelective(TradeGoods record);

    int updateByPrimaryKey(TradeGoods record);

    int reduceGoodsNum(TradeGoods record);

    int addGoodsNum(TradeGoods record);
}