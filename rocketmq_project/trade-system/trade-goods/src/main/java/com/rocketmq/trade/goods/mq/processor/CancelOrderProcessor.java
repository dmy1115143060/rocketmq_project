package com.rocketmq.trade.goods.mq.processor;

import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.constants.MQEnums;
import com.rocketmq.trade.common.protocol.goods.AddGoodsNumReq;
import com.rocketmq.trade.common.protocol.mq.CancelOrderMessage;
import com.rocketmq.trade.common.rocketmq.IMessageProcessor;
import com.rocketmq.trade.entity.TradeMqConsumerLog;
import com.rocketmq.trade.entity.TradeMqConsumerLogExample;
import com.rocketmq.trade.entity.TradeMqConsumerLogKey;
import com.rocketmq.trade.goods.service.IGoodsService;
import com.rocketmq.trade.mapper.TradeMqConsumerLogMapper;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Mingyang Du
 * 库存模块收到MQ的取消订单消息处理实现类：需要考虑重复消息的去重处理
 **/
public class CancelOrderProcessor implements IMessageProcessor {

    public static final Logger LOGGER = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private IGoodsService goodsService;

    /**
     * 消息日志记录表
     */
    @Autowired
    private TradeMqConsumerLogMapper tradeMqConsumerLogMapper;

    /**
     * user模块接收到MQ的取消订单消息后的处理逻辑
     * @param messageExt 得到的消息
     * @return 处理的结果 true(成功) false(失败)
     */
    @Override
    public boolean handleMessage(MessageExt messageExt) {
        TradeMqConsumerLog mqConsumerLog = null;
        try {
            // 获得取消订单消息的相关属性
            String groupName = "goods_orderTopic_cancel_group";
            String messageBody = new String(messageExt.getBody(), "utf-8");
            String messageId = messageExt.getMsgId();
            String messgaeTags = messageExt.getTags();
            String messgaeKeys = messageExt.getKeys();
            LOGGER.info("goods CancelOrderProcessor receive messgae: " + messageExt);

            // 先判断数据库中是否包含该消息的消费记录
            TradeMqConsumerLogKey key = new TradeMqConsumerLogKey();
            key.setGroupName(groupName);
            key.setMsgTag(messgaeTags);
            key.setMsgKeys(messgaeKeys);
            mqConsumerLog = this.tradeMqConsumerLogMapper.selectByPrimaryKey(key);
            // 日志表中已经包含消息
            if (mqConsumerLog != null) {
                // 获取该消息的消费状态
                String consumerStatus = mqConsumerLog.getConsumerStatus();
                // 消息已经被处理，因此不需要重复处理
                if (consumerStatus.equals(MQEnums.ConsumerStatusEnum.SUCCESS.getCode())) {
                    LOGGER.warn("消息已经处理过，无需重复处理!");
                    return true;
                }
                // 消息正在处理中，说明有其他的消费者正在处理消息
                else if (consumerStatus.equals(MQEnums.ConsumerStatusEnum.PROCESSING.getCode())) {
                    LOGGER.warn("正在处理中，稍后再试!");
                    return false;
                }
                // 消息处理失败
                else if (consumerStatus.equals(MQEnums.ConsumerStatusEnum.FAIL.getCode())) {
                    // 更新次数超过3次时，不再处理
                    if (mqConsumerLog.getConsumerTimes() >= 3) {
                        LOGGER.warn("消息处理次数超过3次，不再处理!");
                        return true;
                    }
                    // 更新消息状态为正在处理中
                    TradeMqConsumerLog updateMqConsumerLog = new TradeMqConsumerLog();
                    updateMqConsumerLog.setGroupName(mqConsumerLog.getGroupName());
                    updateMqConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
                    updateMqConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
                    updateMqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.PROCESSING.getCode());

                    // 由于在更新数据库日志记录信息时，可能会存在多线程并发的情况。因此为了避免并发，这里采用乐观锁的方式
                    // 即在更新数据库日志记录信息时必须保证数据库当前的状态与刚刚读出的状态保持一致
                    TradeMqConsumerLogExample example = new TradeMqConsumerLogExample();
                    example.createCriteria()
                            .andGroupNameEqualTo(mqConsumerLog.getGroupName())
                            .andMsgKeysEqualTo(mqConsumerLog.getMsgKeys())
                            .andMsgTagEqualTo(mqConsumerLog.getMsgTag())
                            .andConsumerTimesEqualTo(mqConsumerLog.getConsumerTimes());
                    int i = this.tradeMqConsumerLogMapper.updateByExampleSelective(updateMqConsumerLog, example);
                    if (i <= 0) {
                        LOGGER.warn("并发更新处理状态，稍后重试!");
                        return false;
                    }
                }
            }
            // 不包含消息
            else {
                try {
                    // 将消息插入到去重表中，并发时用主键冲突控制
                    mqConsumerLog = new TradeMqConsumerLog();
                    mqConsumerLog.setGroupName(groupName);
                    mqConsumerLog.setMsgKeys(messgaeKeys);
                    mqConsumerLog.setMsgTag(messgaeTags);
                    mqConsumerLog.setMsgId(messageId);
                    mqConsumerLog.setConsumerTimes(0);
                    mqConsumerLog.setMsgBody(messageBody);
                    mqConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.PROCESSING.getCode());
                    this.tradeMqConsumerLogMapper.insert(mqConsumerLog);
                } catch (Exception e) {
                    LOGGER.warn("主键冲突，说明有订阅者正在处理，稍后再试!");
                    return false;
                }
            }
            // 业务处理逻辑
            CancelOrderMessage cancelOrderMessage = JSON.parseObject(messageBody, CancelOrderMessage.class);
            AddGoodsNumReq addGoodsNumReq = new AddGoodsNumReq();
            addGoodsNumReq.setGoodsId(cancelOrderMessage.getGoodsId());
            addGoodsNumReq.setGoodsNum(cancelOrderMessage.getGoodsNum());
            addGoodsNumReq.setOrderId(cancelOrderMessage.getOrderId());
            this.goodsService.addGoodsNum(addGoodsNumReq);

            // 更新数据库中的日志消息状态为处理成功
            TradeMqConsumerLog updateConsumerLog = new TradeMqConsumerLog();
            updateConsumerLog.setGroupName(mqConsumerLog.getGroupName());
            updateConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
            updateConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
            updateConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.SUCCESS.getCode());
            updateConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
            this.tradeMqConsumerLogMapper.updateByPrimaryKeySelective(updateConsumerLog);
            return true;
        } catch (Exception e) {
            TradeMqConsumerLog updateConsumerLog = new TradeMqConsumerLog();
            updateConsumerLog.setGroupName(mqConsumerLog.getGroupName());
            updateConsumerLog.setMsgTag(mqConsumerLog.getMsgTag());
            updateConsumerLog.setMsgKeys(mqConsumerLog.getMsgKeys());
            updateConsumerLog.setConsumerStatus(MQEnums.ConsumerStatusEnum.FAIL.getCode());
            updateConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes() + 1);
            this.tradeMqConsumerLogMapper.updateByPrimaryKeySelective(updateConsumerLog);
            e.printStackTrace();
            return false;
        }
    }
}
