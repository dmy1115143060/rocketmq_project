import com.rocketmq.trade.common.exception.TradeMQException;
import com.rocketmq.trade.common.rocketmq.TradeMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author 杜名洋
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:xml/spring-rocketmq-producer.xml")
public class ProducerTest {

    @Autowired
    private TradeMQProducer tradeProducer;

    @Test
    public void testProducer() throws TradeMQException {
        SendResult sendResult = this.tradeProducer
                .sendMessage("testtopic", "test", "12345678", "this is a test message");
        System.out.println(sendResult);
    }
}
