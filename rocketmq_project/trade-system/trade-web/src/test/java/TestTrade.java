import com.alibaba.fastjson.JSON;
import com.rocketmq.trade.common.api.*;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponReq;
import com.rocketmq.trade.common.protocol.coupon.QueryCouponRes;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsReq;
import com.rocketmq.trade.common.protocol.goods.QueryGoodsRes;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderReq;
import com.rocketmq.trade.common.protocol.order.ConfirmOrderRes;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

/**
 * @author Mingyang Du
 **/
@ContextConfiguration(locations = {"classpath:xml/spring-rest-client.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestTrade {

    @Autowired
    private IGoodsApi goodsApi;

    @Autowired
    private ICouponApi couponApi;

    @Autowired
    private IUserApi userApi;

    @Autowired
    private IOrderApi orderApi;

    @Autowired
    private IPayApi payApi;

    /**
     * 查询用户信息
     */
    @Test
    public void testUser() {
        QueryUserReq queryUserReq = new QueryUserReq();
        queryUserReq.setUserId(1);
        QueryUserRes queryUserRes = userApi.queryUser(queryUserReq);
        System.out.println(JSON.toJSONString(queryUserRes));
    }

    /**
     * 查询商品信息
     */
    @Test
    public void testGooods() {
        QueryGoodsReq queryGoodsReq = new QueryGoodsReq();
        queryGoodsReq.setGoodsId(10000);
        QueryGoodsRes queryGoodsRes = goodsApi.queryGoods(queryGoodsReq);
        System.out.println(JSON.toJSONString(queryGoodsRes));
    }

    /**
     * 查询优惠券信息
     */
    @Test
    public void testCoupon() {
        QueryCouponReq queryCouponReq = new QueryCouponReq();
        queryCouponReq.setCouponId("123456789");
        QueryCouponRes queryCouponRes = couponApi.queryCoupon(queryCouponReq);
        System.out.println(JSON.toJSONString(queryCouponRes));
    }

    /**
     * 测试下订单功能
     */
    @Test
    public void testConfirmOrder() {
        ConfirmOrderReq confirmOrderReq = new ConfirmOrderReq();
        confirmOrderReq.setGoodsId(10000);
        confirmOrderReq.setUserId(1);
        confirmOrderReq.setGoodsNumber(1);
        confirmOrderReq.setAddress("北京");
        confirmOrderReq.setGoodsPrice(new BigDecimal("5000"));
        confirmOrderReq.setOrderAmount(new BigDecimal("5000"));
        confirmOrderReq.setMoneyPaid(new BigDecimal("100"));
        confirmOrderReq.setCouponId("123456789");
        ConfirmOrderRes confirmOrderRes = orderApi.confirmOrder(confirmOrderReq);
        System.out.println(JSON.toJSONString(confirmOrderRes));
    }

}
