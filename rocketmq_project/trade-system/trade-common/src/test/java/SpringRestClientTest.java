import com.rocketmq.trade.common.api.IUserApi;
import com.rocketmq.trade.common.protocol.user.QueryUserReq;
import com.rocketmq.trade.common.protocol.user.QueryUserRes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author 杜名洋
 * 动态代理的测试类
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:xml/spring-rest-client.xml")
public class SpringRestClientTest {

    /**
     * 利用spring注解自动生成IUserApi对象实例
     */
    @Autowired
    private IUserApi userApi;

    @Test
    public void test() {
        QueryUserReq queryUserReq = new QueryUserReq();
        queryUserReq.setUserId(1);
        QueryUserRes queryUserRes = userApi.queryUser(queryUserReq);
        System.out.println(queryUserRes);
    }
}
