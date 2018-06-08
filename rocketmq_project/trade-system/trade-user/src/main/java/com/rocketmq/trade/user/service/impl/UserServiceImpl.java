package com.rocketmq.trade.user.service.impl;

import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import com.rocketmq.trade.entity.TradeUser;
import com.rocketmq.trade.entity.TradeUserMoneyLog;
import com.rocketmq.trade.entity.TradeUserMoneyLogExample;
import com.rocketmq.trade.mapper.TradeUserMapper;
import com.rocketmq.trade.mapper.TradeUserMoneyLogMapper;
import com.rocketmq.trade.user.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 杜名洋
 * 真正的用户服务实现类
 **/
@Service
public class UserServiceImpl implements IUserService {

    /**
     * 数据库中trade_user表对应的DAO层封装类
     */
    @Autowired
    private TradeUserMapper tradeUserMapper;

    /**
     * 用户余额操作类
     */
    @Autowired
    private TradeUserMoneyLogMapper tradeUserMoneyLogMapper;

    /**
     * 根据用户ID来查询用户
     * @param queryUserReq
     * @return 查询结果
     */
    @Override
    public QueryUserRes queryUserById(QueryUserReq queryUserReq) {
        QueryUserRes queryUserRes = new QueryUserRes();
        queryUserRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        queryUserRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());

        try {
            if (queryUserReq == null || queryUserReq.getUserId() == null) {
                throw new RuntimeException("请求参数不正确!");
            }
            TradeUser tradeUser = tradeUserMapper.selectByPrimaryKey(queryUserReq.getUserId());
            if (tradeUser != null) {
                BeanUtils.copyProperties(tradeUser, queryUserRes);
            } else {
                throw new RuntimeException("未查询到该用户!");
            }
        } catch (Exception e) {
            queryUserRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            queryUserRes.setReturnInfo(TradeEnums.RetEnum.FAIL.getDesc());
        }
        return queryUserRes;
    }

    /**
     * 实现用户修改余额的逻辑，由于在更新用户余额时需要将操作记录写入到日志中，而这两个操作必须同时执行
     * 这里采用spring的事务注解来保证两个操作的事务性。
     * @param changeUserMoneyReq 修改余额的参数对象
     * @return 修改结果
     */
    @Transactional
    @Override
    public ChangeUserMoneyRes changeUserMoney(ChangeUserMoneyReq changeUserMoneyReq) {

        ChangeUserMoneyRes changeUserMoneyRes = new ChangeUserMoneyRes();
        changeUserMoneyRes.setReturnCode(TradeEnums.RetEnum.SUCCESS.getCode());
        changeUserMoneyRes.setReturnInfo(TradeEnums.RetEnum.SUCCESS.getDesc());

        // 1、判断请求参数是否为空
        if (changeUserMoneyReq == null || changeUserMoneyReq.getUserId() == null
                || changeUserMoneyReq.getUserMoney() == null) {
            throw new RuntimeException("请求参数不正确!");
        }

        // 判断金额是否正确
        if (changeUserMoneyReq.getUserMoney().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额不能小于0!");
        }

        // 2、将更新用户余额操作记录封装到一个日志记录对象中
        TradeUserMoneyLog tradeUserMoneyLog = new TradeUserMoneyLog();
        tradeUserMoneyLog.setOrderId(changeUserMoneyReq.getOrderId());
        tradeUserMoneyLog.setUserId(changeUserMoneyReq.getUserId());
        tradeUserMoneyLog.setUserMoney(changeUserMoneyReq.getUserMoney());
        tradeUserMoneyLog.setCreateTime(new Date());
        tradeUserMoneyLog.setMoneyLogType(changeUserMoneyReq.getMoneyLogType());

        TradeUser tradeUser = new TradeUser();
        tradeUser.setUserId(changeUserMoneyReq.getUserId());
        tradeUser.setUserMoney(changeUserMoneyReq.getUserMoney());

        // 2.1、查询是否有付款记录，即查询用户金额操作记录表中是否有相关的付款记录
        TradeUserMoneyLogExample logExample = new TradeUserMoneyLogExample();
        logExample.createCriteria().andUserIdEqualTo(changeUserMoneyReq.getUserId())
                .andOrderIdEqualTo(changeUserMoneyReq.getOrderId())
                .andMoneyLogTypeEqualTo(TradeEnums.UserMoneyLogTypeEnum.PAIED.getCode());
        long count = this.tradeUserMoneyLogMapper.countByExample(logExample);

        // 2.1.1、订单付款类型
        if (changeUserMoneyReq.getMoneyLogType().equals(TradeEnums.UserMoneyLogTypeEnum.PAIED.getCode())) {
            // (1)、防止重复付款
            if (count > 0L) {
                throw new RuntimeException("不能重复付款!");
          }
            // (2)、执行付款操作
            tradeUserMapper.reduceUserMoney(tradeUser);
        }

        // 2.1.2、订单退款类型
        if (changeUserMoneyReq.getMoneyLogType().equals(TradeEnums.UserMoneyLogTypeEnum.RETURNED.getCode())) {
            // (1)、没有付款信息，则无法进行退款操作
            if (count == 0L) {
                throw new RuntimeException("没有付款信息，无法退款!");
            }
            // (2)、防止多次退款，即查询用于金额操作记录表中是否有相关的退款记录，如果有，则说明不能重复退款
            logExample = new TradeUserMoneyLogExample();
            logExample.createCriteria().andUserIdEqualTo(changeUserMoneyReq.getUserId())
                    .andOrderIdEqualTo(changeUserMoneyReq.getOrderId())
                    .andMoneyLogTypeEqualTo(TradeEnums.UserMoneyLogTypeEnum.RETURNED.getCode());
            count = this.tradeUserMoneyLogMapper.countByExample(logExample);
            if (count > 0L) {
                throw new RuntimeException("已经退款，无法退款!");
            }
            // (3)、执行退款操作
            tradeUserMapper.addUserMoney(tradeUser);
        }

        // 2.2、写入日志
        this.tradeUserMoneyLogMapper.insert(tradeUserMoneyLog);

        return changeUserMoneyRes;
    }
}
