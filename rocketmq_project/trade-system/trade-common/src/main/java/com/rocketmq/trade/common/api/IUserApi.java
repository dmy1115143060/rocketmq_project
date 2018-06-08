package com.rocketmq.trade.common.api;

import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;

/**
 * @author 杜名洋
 * 供其他模块使用的公用用户服务接口
 */
public interface IUserApi {
    /**
     * 查询用户信息服务接口
     * @param queryUserReq 请求参数
     * @return 查询结果
     */
    QueryUserRes queryUser(QueryUserReq queryUserReq);

    /**
     * 修改用户余额服务接口
     * @param changeUserMoneyReq 修改参数
     * @return 修改结果
     */
    ChangeUserMoneyRes changeUserMoney(ChangeUserMoneyReq changeUserMoneyReq);
}
