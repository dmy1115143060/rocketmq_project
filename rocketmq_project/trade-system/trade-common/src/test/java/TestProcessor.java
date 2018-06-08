import com.rocketmq.trade.common.rocketmq.IMessageProcessor;
import org.apache.rocketmq.common.message.MessageExt;

import java.io.UnsupportedEncodingException;

/**
 * @author 杜名洋
 **/
public class TestProcessor implements IMessageProcessor {
    public boolean handleMessage(MessageExt messageExt) {
        try {
            System.out.println("收到消息：" + new String(messageExt.getBody(), "utf-8"));
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }
}
