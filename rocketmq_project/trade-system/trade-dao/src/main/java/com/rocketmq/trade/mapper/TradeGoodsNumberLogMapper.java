package com.rocketmq.trade.mapper;

import com.rocketmq.trade.entity.TradeGoodsNumberLog;
import com.rocketmq.trade.entity.TradeGoodsNumberLogExample;
import com.rocketmq.trade.entity.TradeGoodsNumberLogKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TradeGoodsNumberLogMapper {
    long countByExample(TradeGoodsNumberLogExample example);

    int deleteByExample(TradeGoodsNumberLogExample example);

    int deleteByPrimaryKey(TradeGoodsNumberLogKey key);

    int insert(TradeGoodsNumberLog record);

    int insertSelective(TradeGoodsNumberLog record);

    List<TradeGoodsNumberLog> selectByExample(TradeGoodsNumberLogExample example);

    TradeGoodsNumberLog selectByPrimaryKey(TradeGoodsNumberLogKey key);

    int updateByExampleSelective(@Param("record") TradeGoodsNumberLog record, @Param("example") TradeGoodsNumberLogExample example);

    int updateByExample(@Param("record") TradeGoodsNumberLog record, @Param("example") TradeGoodsNumberLogExample example);

    int updateByPrimaryKeySelective(TradeGoodsNumberLog record);

    int updateByPrimaryKey(TradeGoodsNumberLog record);
}