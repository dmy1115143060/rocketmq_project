package com.rocketmq.trade.mapper;

import com.rocketmq.trade.entity.TradeMqProducerTemp;
import com.rocketmq.trade.entity.TradeMqProducerTempExample;
import com.rocketmq.trade.entity.TradeMqProducerTempKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TradeMqProducerTempMapper {
    long countByExample(TradeMqProducerTempExample example);

    int deleteByExample(TradeMqProducerTempExample example);

    int deleteByPrimaryKey(TradeMqProducerTempKey key);

    int insert(TradeMqProducerTemp record);

    int insertSelective(TradeMqProducerTemp record);

    List<TradeMqProducerTemp> selectByExample(TradeMqProducerTempExample example);

    TradeMqProducerTemp selectByPrimaryKey(TradeMqProducerTempKey key);

    int updateByExampleSelective(@Param("record") TradeMqProducerTemp record, @Param("example") TradeMqProducerTempExample example);

    int updateByExample(@Param("record") TradeMqProducerTemp record, @Param("example") TradeMqProducerTempExample example);

    int updateByPrimaryKeySelective(TradeMqProducerTemp record);

    int updateByPrimaryKey(TradeMqProducerTemp record);
}