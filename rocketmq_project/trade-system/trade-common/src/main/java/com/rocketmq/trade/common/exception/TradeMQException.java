package com.rocketmq.trade.common.exception;

/**
 * @author 杜名洋
 * 封装RocketMQ异常信息
 **/
public class TradeMQException extends Exception {

    public TradeMQException() {
        super();
    }

    public TradeMQException(String message) {
        super(message);
    }

    public TradeMQException(String message, Throwable cause) {
        super(message, cause);
    }

    public TradeMQException(Throwable cause) {
        super(cause);
    }
}
