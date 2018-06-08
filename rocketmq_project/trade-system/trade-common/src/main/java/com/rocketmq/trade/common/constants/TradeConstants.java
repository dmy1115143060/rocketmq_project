package com.rocketmq.trade.common.constants;

import java.math.BigDecimal;

/**
 * @author 杜名洋
 **/
public interface TradeConstants {
    /**
     * 大于100免邮
     */
    public static final BigDecimal SHIPPINGFEETHRESHOLD = new BigDecimal(100.00);
    /**
     * 基本运费
     */
    public static final BigDecimal SHIPPINGFEEBASE = new BigDecimal(10.00);
}
