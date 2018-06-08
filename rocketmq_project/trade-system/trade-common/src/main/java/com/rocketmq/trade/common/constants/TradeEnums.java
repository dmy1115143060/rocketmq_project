package com.rocketmq.trade.common.constants;

/**
 * @author 杜名洋
 **/
public class TradeEnums {

    /**
     * 返回值的枚举类
     */
    public enum RetEnum {

        SUCCESS("1", "success"), FAIL("0", "fail");

        private String code;
        private String desc;

        RetEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 封装不同模块的请求url枚举类
     */
    public enum RestEnum {

        ORDER("localhost", 8081, "order"),
        PAY("localhost", 8082, "pay"),
        COUPON("localhost", 8083, "coupon"),
        GOODS("localhost", 8084, "goods"),
        USER("localhost", 8085, "user");

        private int serverPort;
        private String serverHost;
        private String contextPath;

        RestEnum(String serverHost, int serverPort, String contextPath) {
            this.serverPort = serverPort;
            this.serverHost = serverHost;
            this.contextPath = contextPath;
        }

        public int getServerPort() {
            return serverPort;
        }

        public String getContextPath() {
            return contextPath;
        }

        public String getServerHost() {
            return serverHost;
        }

        public String getServerUrl() {
            return "http://" + this.serverHost + ":" + this.serverPort + "/" + this.contextPath + "/";
        }
    }

    /**
     * 订单状态枚举类
     */
    public enum OrderStatusEnum {

        NO_CONFIRM("0", "no_confirm"), CONFIRM("1", "confirm"),
        CANCEL("2", "cancel"), INVALID("3", "invalid"), QUITED("4", "quited");

        private String code;
        private String desc;

        OrderStatusEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 支付状态枚举类
     */
    public enum PayStatusEnum {

        NO_PAY("0", "no_pay"), PAYING("1", "paying"), PAIED("2", "paied");

        private String code;
        private String desc;

        PayStatusEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 物流状态枚举类
     */
    public enum ShippingStatusEnum {

        NO_SHIP("0", "no_ship"), SHIPPED("1", "shipped"), RECEIVED("2", "received");

        private String code;
        private String desc;

        ShippingStatusEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum YesNoEnum {

        Yes("1", "yes"), NO("0", "no");

        private String code;
        private String desc;

        YesNoEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * 用户金额修改类型枚举类
     */
    public enum UserMoneyLogTypeEnum {

        PAIED("1", "order_paied"), RETURNED("2", "order_returned");

        private String code;
        private String desc;

        UserMoneyLogTypeEnum(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
