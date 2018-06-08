package com.rocketmq.trade.common.client;

import com.rocketmq.trade.common.api.IUserApi;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import org.springframework.web.client.RestTemplate;

/**
 * @author 杜名洋
 **/
public class RestClient {

    /**
     * 使用spring提供的rest服务
     */
    private static RestTemplate restTemplate = new RestTemplate();

    private IUserApi userApi;

    public static void main(String[] args) {
        // 设置一个请求参数，即查询用户userId=1的用户信息
        QueryUserReq queryUserReq = new QueryUserReq();
        queryUserReq.setUserId(1);
        // 提交一个post请求，url为http://localhost:8080/user/queryUserById
        QueryUserRes queryUserRes = restTemplate.postForObject(TradeEnums.RestEnum.USER.getServerUrl() + "queryUser"
                , queryUserReq
                , QueryUserRes.class);
        System.out.println(queryUserRes);
    }
}
