package com.rocketmq.trade.user.service;

import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;

/**
 * 提供用户服务的接口
 */
public interface IUserService {
    /**
     * 提供的服务，根据请求的参数返回查询的结果
     * @param queryUserReq 请求参数，封装了用户的id
     * @return 返回最终查询结果
     */
    QueryUserRes queryUserById(QueryUserReq queryUserReq);

    /**
     * 修改用户余额的方法
     * @param changeUserMoneyReq 修改余额的参数对象
     * @return 修改结果
     */
    ChangeUserMoneyRes changeUserMoney(ChangeUserMoneyReq changeUserMoneyReq);
}
