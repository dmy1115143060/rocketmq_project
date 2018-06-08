package com.rocketmq.trade.common.constants;

/**
 * @author 杜名洋
 **/
public class MQEnums {

    public enum TopicEnum {
        /**
         * 订单确认的消息
         */
        ORDER_TOPIC("orderTopic", "confirm"),
        /**
         * 取消订单的消息
         */
        ORDER_CANCEL("orderTopic", "cancel"),
        /**
         * 订单已支付的消息
         */
        PAY_PAIED("payTopic", "paied");

        /**
         * 主题
         */
        private String topic;

        /**
         * 标签
         */
        private String tag;

        TopicEnum(String topic, String tag) {
            this.topic = topic;
            this.tag = tag;
        }

        public String getTopic() {
            return topic;
        }

        public String getTag() {
            return tag;
        }
    }

    public enum ConsumerStatusEnum {
        PROCESSING("0", "正在处理中"), SUCCESS("1", "处理成功"), FAIL("2", "处理失败");

        ConsumerStatusEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private String code;
        private String desc;

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
