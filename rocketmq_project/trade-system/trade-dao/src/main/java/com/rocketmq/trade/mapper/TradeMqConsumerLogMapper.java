package com.rocketmq.trade.mapper;

import com.rocketmq.trade.entity.TradeMqConsumerLog;
import com.rocketmq.trade.entity.TradeMqConsumerLogExample;
import com.rocketmq.trade.entity.TradeMqConsumerLogKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TradeMqConsumerLogMapper {
    long countByExample(TradeMqConsumerLogExample example);

    int deleteByExample(TradeMqConsumerLogExample example);

    int deleteByPrimaryKey(TradeMqConsumerLogKey key);

    int insert(TradeMqConsumerLog record);

    int insertSelective(TradeMqConsumerLog record);

    List<TradeMqConsumerLog> selectByExample(TradeMqConsumerLogExample example);

    TradeMqConsumerLog selectByPrimaryKey(TradeMqConsumerLogKey key);

    int updateByExampleSelective(@Param("record") TradeMqConsumerLog record, @Param("example") TradeMqConsumerLogExample example);

    int updateByExample(@Param("record") TradeMqConsumerLog record, @Param("example") TradeMqConsumerLogExample example);

    int updateByPrimaryKeySelective(TradeMqConsumerLog record);

    int updateByPrimaryKey(TradeMqConsumerLog record);
}