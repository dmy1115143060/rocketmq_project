package com.rocketmq.trade.user.api;

import com.rocketmq.trade.common.api.IUserApi;
import com.rocketmq.trade.common.constants.TradeEnums;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyReq;
import com.rocketmq.trade.common.protocol.user.ChangeUserMoneyRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import com.rocketmq.trade.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 杜名洋
 * 供其他模块调用的服务实现类
 **/
@Controller
public class UserApiImpl implements IUserApi {

    /**
     * 静态代理对象，实际上调用IUserService实现类对象来完成响应的功能
     */
    @Autowired
    private IUserService userService;

    /**
     * 查询用户信息
     * @param queryUserReq 请求参数封装的对象
     * @return 查询结果
     */
    @RequestMapping(value = "/queryUser", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public QueryUserRes queryUser(@RequestBody QueryUserReq queryUserReq) {
        return userService.queryUserById(queryUserReq);
    }

    /**
     * 修改用户余额
     * @param changeUserMoneyReq 修改参数封装的对象
     * @return 操作结果
     */
    @RequestMapping(value = "/changeUserMoney", method = RequestMethod.POST)
    @ResponseBody
    @Override
    public ChangeUserMoneyRes changeUserMoney(@RequestBody ChangeUserMoneyReq changeUserMoneyReq) {
        ChangeUserMoneyRes changeUserMoneyRes = new ChangeUserMoneyRes();
        try {
            changeUserMoneyRes = this.userService.changeUserMoney(changeUserMoneyReq);
        } catch (Exception e) {
            // 查询失败后设置查询结果中的信息为失败信息
            changeUserMoneyRes.setReturnCode(TradeEnums.RetEnum.FAIL.getCode());
            changeUserMoneyRes.setReturnInfo(e.getMessage());
        }
        return changeUserMoneyRes;
    }
}
